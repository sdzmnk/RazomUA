package com.example.razomua.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.razomua.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@RequiresApi(Build.VERSION_CODES.O)
class UserViewModel : ViewModel() {

    private val _users: SnapshotStateList<User> = mutableStateListOf()
    val users: List<User> get() = _users

    private val _usersFlow = MutableStateFlow<List<User>>(emptyList())
    val usersFlow = _usersFlow.asStateFlow()

    fun addUser(user: User) {
        _users.add(user)
        _usersFlow.value = _users.toList()
    }

    fun removeUser(user: User) {
        _users.remove(user)
        _usersFlow.value = _users.toList()
    }

    fun getUserByEmail(email: String): User? {
        return _users.find { it.email == email }
    }

    init {
        _usersFlow.value = _users.toList()
    }
}
