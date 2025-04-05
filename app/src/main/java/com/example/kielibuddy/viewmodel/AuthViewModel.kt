package com.example.kielibuddy.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kielibuddy.R
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
import androidx.navigation.NavController
import com.example.kielibuddy.model.SubscriptionStatus
import com.example.kielibuddy.model.UserModel
import com.example.kielibuddy.model.UserRole
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.tasks.await
import com.facebook.CallbackManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.example.kielibuddy.repository.UserRepository


class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState
    val user = MutableLiveData<User?>(null)
    private val callbackManager: CallbackManager = CallbackManager.Factory.create()
    private val fireStore = FirebaseFirestore.getInstance()

    private val repository = UserRepository()

    private val _userData = MutableLiveData<UserModel?>()
    val userData: LiveData<UserModel?> = _userData

    fun loadUserData(userId: String) {
        println("📦 loadUserData() CALLED with userId: $userId")

        viewModelScope.launch {
            val user = repository.getUserDetails(userId)
            if (user != null) {
                println("🎉 userData fetched from Firestore: $user")
                _userData.value = user
            } else {
                println("⚠️ userData is NULL from Firestore")
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    fun loadUserDataAndNavigate(userId: String, navController: NavController) {
        viewModelScope.launch {
            val user = repository.getUserDetails(userId)
            if (user != null) {
                _userData.value = user
                if (user.profileCompleted) {
                    _authState.value = AuthState.Authenticated
                    fetchUserRole(userId, navController)
                } else {
                    navController.navigate("completeSignup")
                }
            } else {
                _authState.value = AuthState.Unauthenticated
            }
        }
    }


    fun checkAuthStatus(navController: NavController) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            loadUserDataAndNavigate(currentUser.uid, navController)  // Load user data and navigate
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun login(email: String, password: String, navController: NavController) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = task.result.user?.uid
                if (userId != null) {
                    //navController.navigate("studentHome")
                    fetchUserRole(userId, navController)
                }
            } else {
                _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
            }
        }
    }

    fun signup(firstName: String, lastName: String, email: String, password: String, userType: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            _authState.value = if (task.isSuccessful){
                val userid = task.result.user?.uid
                val userRole = getUserRole(userType)

                val userModel = UserModel(userid!!, firstName ?: "", lastName?: "", email ?: "", "", SubscriptionStatus.ACTIVE, userRole, 1)
                storeUserData(userModel)
                AuthState.Authenticated
            } else AuthState.Error(
                task.exception?.message ?: "Something went wrong"
            )
        }
    }

    fun getUserRole(role: String?): UserRole {
        return when (role?.lowercase()) {
            "student" -> UserRole.STUDENT
            "teacher" -> UserRole.TEACHER
            "admin" -> UserRole.ADMIN
            else -> UserRole.STUDENT
        }
    }

    fun signout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
        user.value = null
        _userData.value = null

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

    fun handleGoogleSignIn(context: Context, navController: NavController) {
        viewModelScope.launch {
            googleSignIn(context).collect { result ->
                result.fold(
                    onSuccess = { authResult ->
                        val currentUser = authResult.user
                        if (currentUser != null) {
                            val uid = currentUser.uid
                            val userRef = fireStore.collection("users").document(uid)
                            val doc = userRef.get().await()
                            if (doc.exists()) {
                                val isCompleted = doc.getBoolean("profileCompleted") ?: false
                                if (!isCompleted) {
                                    _userData.value = doc.toObject(UserModel::class.java)  // Pass to screen
                                    navController.navigate("completeSignup")
                                } else {
                                    fetchUserRole(uid, navController)
                                }
                            } else {
                                // first time login, create dummy entry with profileCompleted = false
                                val nameParts = currentUser.displayName.orEmpty().split(" ")
                                val user = UserModel(
                                    id = uid,
                                    firstName = nameParts.getOrNull(0).orEmpty(),
                                    lastName = nameParts.getOrNull(1).orEmpty(),
                                    email = currentUser.email.orEmpty(),
                                    profileImg = currentUser.photoUrl.toString(),
                                    role = UserRole.STUDENT,
                                    subscription = SubscriptionStatus.FREE,
                                    profileCompleted = false
                                )
                                userRef.set(user).await()
                                _userData.value = user
                                navController.navigate("completeSignup")
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

    private fun fetchUserRole(userId: String, navController: NavController) {
        fireStore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userRole = document.getString("role") ?: "student" // Default role
                    navigateBasedOnRole(userRole, navController, userId)
                } else {
                    _authState.value = AuthState.Error("User data not found")
                }
            }
            .addOnFailureListener { e ->
                _authState.value = AuthState.Error(e.localizedMessage ?: "Error fetching user data")
            }
    }

    private fun navigateBasedOnRole(role: String, navController: NavController, userId: String) {
        println("Role: $role")
        val destination = when (role.lowercase()) {
            "student" -> "studentHome"
            "teacher" -> "tutorHome"
            else -> "home"
        }

        navController.navigate(destination) {
            popUpTo(0) { inclusive = true }
        }
        _authState.value = AuthState.Authenticated
    }

    fun storeUserData(user: UserModel) {
        fireStore.collection("users").document(user.id).set(user)
            .addOnSuccessListener {
                _authState.value = AuthState.Authenticated
            }
            .addOnFailureListener { e ->
                _authState.value = AuthState.Error(e.localizedMessage ?: "Something went wrong")
            }
    }

    fun sendPasswordResetEmail(email: String, onComplete: () -> Unit) {
        if (email.isEmpty()) {
            _authState.value = AuthState.Error("Email can't be empty")
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete()
                } else {
                    _authState.value = AuthState.Error(
                        task.exception?.message ?: "Failed to send reset email"
                    )
                }
            }
    }

    fun signupComplete(firstName: String, lastName: String, email: String, userType: String, navController: NavController)
    {
        val uid = auth.currentUser?.uid ?: return
        val role = getUserRole(userType)
        val updateMap = mapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "role" to role.name,
            "profileCompleted" to true
        )
        fireStore.collection("users").document(uid).update(updateMap)
            .addOnSuccessListener {
                fetchUserRole(uid, navController)
            }
            .addOnFailureListener {
                _authState.value = AuthState.Error("Failed to complete signup")
            }
    }

    fun updateProfileImage(imageUrl: String) {
        _userData.value = _userData.value?.copy(profileImg = imageUrl)
    }

    fun updateUserProfileImageOnly(imageUrl: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        fireStore.collection("users").document(uid).update("profileImg", imageUrl)
    }

    fun updateUserProfile(firstName: String, lastName: String, role: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val updates = mapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "role" to role
        )
        fireStore.collection("users").document(uid).update(updates)
            .addOnSuccessListener {
                val updated = _userData.value?.copy(
                    firstName = firstName,
                    lastName = lastName,
                    role = com.example.kielibuddy.model.UserRole.valueOf(role)
                )
                _userData.value = updated
                onSuccess()
            }
            .addOnFailureListener {
                onFailure()
            }
    }

    fun updateTutorProfileFields(fields: Map<String, Any>, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        fireStore.collection("users").document(uid).update(fields)
            .addOnSuccessListener {
                val updated = _userData.value?.copy(
                    aboutMe = fields["aboutMe"] as? String ?: _userData.value?.aboutMe ?: "",
                    countryOfBirth = fields["countryOfBirth"] as? String ?: _userData.value?.countryOfBirth ?: "",
                    introVideoUrl = fields["introVideoUrl"] as? String ?: _userData.value?.introVideoUrl ?: "",
                    price20Min = fields["price20Min"] as? Int ?: _userData.value?.price20Min ?: 0,
                    price50Min = fields["price50Min"] as? Int ?: _userData.value?.price50Min ?: 0,
                    languagesSpoken = fields["languagesSpoken"] as? List<String> ?: _userData.value?.languagesSpoken ?: emptyList(),
                    birthday = fields["birthday"] as? String ?: _userData.value?.birthday ?: ""
                )
                _userData.value = updated
                onSuccess()
            }
            .addOnFailureListener { onFailure() }
    }

    fun updateIntroVideo(videoUrl: String) {
        _userData.value = _userData.value?.copy(introVideoUrl = videoUrl)
    }
}

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}
