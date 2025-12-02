//package com.example.razomua.viewmodel
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import com.example.razomua.model.Profile
//import com.google.firebase.auth.FirebaseAuth
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.flow.MutableSharedFlow
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.launch


//class LoginViewModel : ViewModel() {
//
//    private val _email = MutableLiveData("")
//    val email: LiveData<String> = _email
//
//    private val _password = MutableLiveData("")
//    val password: LiveData<String> = _password
//
//    private val _profile = MutableStateFlow<Profile?>(null)
//    val profile = _profile
//
//    private val _events = MutableSharedFlow<String>()
//    val events = _events
//
//    fun onEmailChange(newEmail: String) {
//        _email.value = newEmail
//    }
//
//    fun onPasswordChange(newPassword: String) {
//        _password.value = newPassword
//    }
//
//    fun login() {
//        if (_email.value.isNullOrEmpty() || _password.value.isNullOrEmpty()) {
//            sendEvent("Заповніть всі поля!")
//            return
//        }
//
//        val dummyProfile = Profile(
//            id = 1,
//            userId = 1,
//            photoUrl = null,
//            location = "Kyiv"
//        )
//
//        _profile.value = dummyProfile
//        sendEvent("Логін успішний!")
//    }
//
//    private fun sendEvent(message: String) {
//        GlobalScope.launch {
//            _events.emit(message)
//        }
//    }
//}


package com.example.razomua.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val email = MutableLiveData("")
    val password = MutableLiveData("")

    private val _events = MutableSharedFlow<String>()
    val events = _events

    fun onEmailChange(newEmail: String) {
        email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        password.value = newPassword
    }

    fun login() {
        val emailValue = email.value ?: ""
        val passwordValue = password.value ?: ""

        if (emailValue.isBlank() || passwordValue.isBlank()) {
            emitEvent("Заповніть всі поля!")
            return
        }

        auth.signInWithEmailAndPassword(emailValue, passwordValue)
            .addOnSuccessListener {
                emitEvent("Логін успішний!")
            }
            .addOnFailureListener { e ->
                emitEvent("Помилка входу: ${e.localizedMessage}")
            }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun emitEvent(message: String) {
        kotlinx.coroutines.GlobalScope.launch {
            _events.emit(message)
        }
    }
}
