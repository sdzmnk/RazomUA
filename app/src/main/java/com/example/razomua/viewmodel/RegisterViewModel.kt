package com.example.razomua.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.razomua.data.local.DatabaseProvider
import com.example.razomua.data.local.entity.UserEntity
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.example.razomua.repository.FirebaseChatRepository

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    val auth = FirebaseAuth.getInstance()
    private val userDao = DatabaseProvider.getDatabase(application).userDao()
    val chatRepository = FirebaseChatRepository()

    private val _registerState = MutableLiveData<Boolean>()
    val registerState: LiveData<Boolean> = _registerState

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun register(
        email: String,
        password: String,
        name: String,
        photoUrl: String = "",
        onComplete: (success: Boolean) -> Unit
    ) {
        Log.d("REGISTER", "User tries to register: $email")

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Log.d("REGISTER", "Registration successful: $email")

                viewModelScope.launch {
                    try {
                        userDao.insert(
                            UserEntity(
                                id = 0,
                                email = email,
                                password = password,
                                name = name,
                            )
                        )

                        chatRepository.initializeCurrentUser(photoUrl)
                            .onSuccess {
                                Log.d("RegisterViewModel", "User initialized with photo: $photoUrl")
                                onComplete(true)
                            }
                            .onFailure { error ->
                                Log.e("RegisterViewModel", "Failed to initialize user", error)
                                onComplete(false)
                            }
                    } catch (e: Exception) {
                        Log.e("REGISTER", "Failed to save user data locally", e)
                        onComplete(false)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("REGISTER", "Registration failed: ${exception.localizedMessage}")
                onComplete(false)
            }
    }
    fun completeRegistrationPart2(name: String, gender: String?, birthday: String?) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            _error.value = "Користувач не авторизований"
            return
        }

        val uid = currentUser.uid

        val userMap = hashMapOf(
            "name" to name,
            "gender" to (gender ?: ""),
            "birthday" to (birthday ?: "")
        )

        chatRepository.saveUserData(uid, userMap)
            .addOnSuccessListener {
                _registerState.value = true
            }
            .addOnFailureListener { e ->
                _error.value = "Не вдалося зберегти дані: ${e.localizedMessage}"
            }
    }

}

