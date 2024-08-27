package com.practica.buscov2.ui.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import com.practica.buscov2.data.dataStore.StoreToken
import com.practica.buscov2.data.pagination.NotificationsDataSource
import com.practica.buscov2.data.pagination.QualificationsDataSource
import com.practica.buscov2.data.repository.busco.NotificationsRepository
import com.practica.buscov2.model.busco.Chat
import com.practica.buscov2.model.busco.Message
import com.practica.buscov2.model.busco.Notification
import com.practica.buscov2.model.busco.auth.LoginToken
import com.practica.buscov2.notifications.NotificationService
import com.practica.buscov2.notifications.NotificationWorker
import com.practica.buscov2.util.Constants
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val storeToken: StoreToken,
    private val notificationsRepo: NotificationsRepository
) : ViewModel() {
    private val _token = MutableStateFlow<LoginToken?>(null)
    val token: StateFlow<LoginToken?> = _token

    private val _context = MutableStateFlow<Context?>(null)

    private var hubConnection: HubConnection? = null
    private var hubConnectionChat: HubConnection? = null

    private val _refreshTrigger = MutableStateFlow(0)

    init {
        observeToken()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val notificationsPage = _refreshTrigger
        .flatMapLatest {
            Pager(PagingConfig(pageSize = 6)) {
                NotificationsDataSource(
                    notificationsRepo,
                    tokenP = token.value!!.token
                )
            }.flow.cachedIn(viewModelScope)
        }

    fun sendNotification(notification: Notification) {
        hubConnection?.send("SendNotification", notification)
    }

    private fun observeToken() {
        viewModelScope.launch(Dispatchers.IO) {
            storeToken.getTokenFlow()
                .catch { e -> Log.e("Error al obtener el token", e.message.toString()) }
                .collect { newToken ->
                    _token.value = newToken
                    newToken?.let { setupHubConnection(it.token) }
                }
        }
    }

    private fun setupHubConnection(token: String) {
        hubConnection = HubConnectionBuilder.create(Constants.BASE_URL_NOTIFICATIONS)
            .withAccessTokenProvider(Single.defer { Single.just(token) })
            .build()

        hubConnectionChat = HubConnectionBuilder.create(Constants.BASE_URL_CHAT)
            .withAccessTokenProvider(Single.defer { Single.just(token) })
            .build()

        hubConnection?.on("ReceiveNotification", { notification: Notification ->
            NotificationWorker.releaseNotification(_context.value!!, notification)
        }, Notification::class.java)

        hubConnectionChat?.on("ReceiveMessageNotification", { message: Message ->
            NotificationWorker.releaseNotification(_context.value!!, Notification(
                userReceiveId = message.userIdReceiver,
                userSenderId = message.userIdSender,
                text = message.text,
                dateAndTime = message.dateAndTime,
                title = message.userSender?.username ?: "Usuario ${message.userIdSender}",
                notificationType = "MESSAGE"
            ))
        }, Message::class.java)

        hubConnection?.start()?.blockingAwait()
        hubConnectionChat?.start()?.blockingAwait()
    }

    fun apllyContext(context: Context) {
        _context.value = context
    }
}