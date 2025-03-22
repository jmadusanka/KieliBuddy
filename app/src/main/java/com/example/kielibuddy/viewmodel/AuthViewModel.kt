package com.example.kielibuddy.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kielibuddy.R
import com.example.kielibuddy.model.User
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.tasks.await
import com.facebook.CallbackManager


class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState
    val user = MutableLiveData<User?>(null)
    private val callbackManager: CallbackManager = CallbackManager.Factory.create()

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        _authState.value =
            if (auth.currentUser == null) AuthState.Unauthenticated else AuthState.Authenticated
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            _authState.value = if (task.isSuccessful) AuthState.Authenticated else AuthState.Error(
                task.exception?.message ?: "Something went wrong"
            )
        }
    }

    fun signup(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            _authState.value = if (task.isSuccessful) AuthState.Authenticated else AuthState.Error(
                task.exception?.message ?: "Something went wrong"
            )
        }
    }

    fun signout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
        user.value = null
    }

    /** Google Sign-In **/
    private suspend fun googleSignIn(context: Context): Flow<Result<AuthResult>> = flow {
        val credentialManager = CredentialManager.create(context)
        val ranNonce: String = java.util.UUID.randomUUID().toString()
        val bytes: ByteArray = ranNonce.toByteArray()
        val md: java.security.MessageDigest = java.security.MessageDigest.getInstance("SHA-256")
        val digest: ByteArray = md.digest(bytes)
        val hashedNonce: String = digest.fold("") { str, it -> str + "%02x".format(it) }

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.Googler_Client_id))
            .setNonce(hashedNonce)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = credentialManager.getCredential(context, request)
        val credential = result.credential

        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val authCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
            val authResult = auth.signInWithCredential(authCredential).await()
            emit(Result.success(authResult))
        } else {
            throw RuntimeException("Invalid credential type")
        }
    }.catch { e ->
        emit(Result.failure(e))
    }

    fun handleGoogleSignIn(context: Context, navController: androidx.navigation.NavController) {
        viewModelScope.launch {
            googleSignIn(context).collect { result ->
                result.fold(
                    onSuccess = { authResult ->
                        val currentUser = authResult.user
                        if (currentUser != null) {
                            user.value = User(
                                currentUser.uid,
                                currentUser.displayName ?: "",
                                currentUser.email ?: "",
                                currentUser.photoUrl.toString()
                            )
                            _authState.value = AuthState.Authenticated
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    },
                    onFailure = { e ->
                        _authState.value = AuthState.Error(e.localizedMessage ?: "Sign-in failed")
                    }
                )
            }
        }
    }
}

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}
