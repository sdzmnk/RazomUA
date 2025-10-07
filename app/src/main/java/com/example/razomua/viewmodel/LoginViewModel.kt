package com.example.razomua.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.razomua.model.Profile
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class LoginViewModel : ViewModel() {

    private val _email = MutableLiveData("")
    val email: LiveData<String> = _email

    private val _password = MutableLiveData("")
    val password: LiveData<String> = _password

    private val _profile = MutableStateFlow<Profile?>(null)
    val profile = _profile

    private val _events = MutableSharedFlow<String>()
    val events = _events

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun login() {
        if (_email.value.isNullOrEmpty() || _password.value.isNullOrEmpty()) {
            sendEvent("Заповніть всі поля!")
            return
        }

        val dummyProfile = Profile(
            id = 1,
            userId = 1,
            photoUrl = null,
            location = "Kyiv"
        )

        _profile.value = dummyProfile
        sendEvent("Логін успішний!")
    }

    private fun sendEvent(message: String) {
        GlobalScope.launch {
            _events.emit(message)
        }
    }
}
