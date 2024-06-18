package com.practica.buscov2.ui.viewModel

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.practica.buscov2.BuildConfig
import com.practica.buscov2.R
import com.practica.buscov2.data.dataStore.StoreToken
import com.practica.buscov2.data.repository.BuscoRepository
import com.practica.buscov2.model.LoginResult
import com.practica.buscov2.model.LoginToken
import com.practica.buscov2.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoogleLoginViewModel @Inject constructor(
    private val repo: BuscoRepository,
    private val storeToken: StoreToken
) : ViewModel() {
    private val googleClientId: String = BuildConfig.GOOGLE_OAUTH_ID
    private lateinit var googleSignInClient: GoogleSignInClient

    fun initialize(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(googleClientId)
            .requestId()
            .requestProfile()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    fun signOut() {
        try {
            googleSignInClient.signOut()
        }catch (e:Exception){
            Log.d("Error", "Error al cerrar sesion con google + ${e.message}")
        }
    }

    fun handleSignInResult(
        completedTask: Task<GoogleSignInAccount>
    ) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            Log.d("USUARIO LOGUEADO", "signInResult:success")
            // Obtener el nombre, el ID de Google y el correo electrónico
            val name = account?.displayName
            val email = account?.email
            val googleId = account?.id
            val photoUrl = account?.photoUrl
            val givenName = account?.givenName
            val familyName = account?.familyName
            val idToken = account?.idToken
            val serverAuthCode = account?.serverAuthCode
            val accountInfo = account?.account
            val grantedScopes = account?.grantedScopes
            val requestedScopes = account?.requestedScopes


            // Imprimir los valores
            Log.d("GoogleSignIn", "Name: $name")
        } catch (e: ApiException) {
            Log.w("Error", "signInResult:failed code=" + e.statusCode)
        }
    }

}