package com.practica.buscov2.ui.viewModel.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.reflect.TypeToken
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.practica.buscov2.data.dataStore.StoreToken
import com.practica.buscov2.model.busco.Chat
import com.practica.buscov2.model.busco.Message
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.model.busco.auth.LoginToken
import com.practica.buscov2.util.Constants.Companion.BASE_URL_CHAT
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val storeToken: StoreToken
) : ViewModel() {
    private val _token = MutableStateFlow<LoginToken?>(null)
    val token: StateFlow<LoginToken?> = _token

    private val hubConnection: HubConnection

    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats = _chats

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages

    // Configura el listener para el evento "Chats"
    val chatListType = object : TypeToken<List<Chat>>() {}.type
    val messageListType = object : TypeToken<List<Message>>() {}.type

    init {
        observeToken()

        hubConnection = HubConnectionBuilder.create(BASE_URL_CHAT)
            .withAccessTokenProvider(Single.defer {
                Single.just(token.value!!.token)
            })
            .build()

        hubConnection.on("Chats", { chats: List<Chat> ->
            _chats.value = chats
        }, chatListType)

        hubConnection.on("ReceiveMessage", { message: Message ->
            // Manejar el mensaje recibido
            _messages.value += message
        }, Message::class.java)

        hubConnection.on("Messages", { messages: List<Message> ->
            _messages.value = messages
        }, messageListType)

        hubConnection.start().blockingAwait()

        getChats()
    }

    fun sendMessage(userIdReceiver: Int, message: String) {
        hubConnection.send("SendMessage", userIdReceiver, message)
    }

    private fun getChats() {
        hubConnection.send("ViewChats")
    }

    fun getMessagesWithUser(toUserId: Int) {
        hubConnection.send("GetMessagesWithUser", toUserId)
    }

    private fun observeToken() {
        viewModelScope.launch(Dispatchers.IO) {
            storeToken.getTokenFlow()
                .catch { e -> Log.e("Error al obtener el token", e.message.toString()) }
                .collect { newToken -> _token.value = newToken }
        }
    }
}
