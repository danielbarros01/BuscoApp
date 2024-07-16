package com.practica.buscov2.ui.viewModel.users

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practica.buscov2.data.dataStore.StoreToken
import com.practica.buscov2.data.repository.busco.UsersRepository
import com.practica.buscov2.model.busco.auth.ErrorBusco
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.model.busco.UserResult
import com.practica.buscov2.util.FilesUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repo: UsersRepository,
    private val storeToken: StoreToken
) : ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile

    //Cerrar sesion
    fun logout(onSuccesss: () -> Unit) {
        viewModelScope.launch {
            storeToken.clearToken()
            onSuccesss()
        }
    }

    fun getMyProfile(token: String, onError: (ErrorBusco) -> Unit, onSuccess: (User) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repo.getMyProfile(token)

            withContext(Dispatchers.Main) {
                when (result) {
                    //Si tenemos un usuario
                    is UserResult.Success -> {
                        _user.value = result.user
                        onSuccess(result.user)
                    }

                    //Si tenemos un error
                    is UserResult.Error -> {
                        val error =
                            ErrorBusco(title = result.error.title, message = result.error.message)
                        onError(error)
                    }
                }
            }
        }
    }

    fun updatePictureProfile(
        context: Context,
        uri: Uri,
        token: String,
        onError: (ErrorBusco) -> Unit,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val filePart = FilesUtils.getFilePart(context, uri, "image")

            val result = repo.updatePictureProfile(token, filePart)

            withContext(Dispatchers.Main) {
                when (result) {
                    //Si tenemos un usuario
                    is Boolean -> {
                        onSuccess()
                    }

                    //Si tenemos un error
                    is ErrorBusco -> {
                        val error =
                            ErrorBusco(title = result.title, message = result.message)
                        onError(error)
                    }
                }
            }
        }
    }


    fun getProfile(userId:Int, onError: (ErrorBusco) -> Unit, onSuccess: (User) -> Unit){
        viewModelScope.launch{
            try {
                val response = withContext(Dispatchers.IO){
                    repo.getProfile(userId)
                }

                when (response) {
                    //Si tenemos un usuario
                    is UserResult.Success -> {
                        _user.value = response.user
                        onSuccess(response.user)
                    }

                    //Si tenemos un error
                    is UserResult.Error -> {
                        val error =
                            ErrorBusco(title = response.error.title, message = response.error.message)
                        onError(error)
                    }
                }
            }catch (e:Exception){
                val error = ErrorBusco(title = "Error", message = "Ha ocurrido un error al obtener el perfil")
                onError(error)
            }
        }
    }

    fun changeUserProfile(user:User){
        _userProfile.value = user
    }
}