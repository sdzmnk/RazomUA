package com.example.razomua.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.razomua.model.User
import com.example.razomua.model.Gender
import java.time.LocalDate
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
class UserViewModel : ViewModel() {

    private val _users: SnapshotStateList<User> = mutableStateListOf()
    val users: List<User> get() = _users

    fun addUser(user: User) {
        _users.add(user)
    }

    fun removeUser(user: User) {
        _users.remove(user)
    }

    fun getUserByEmail(email: String): User? {
        return _users.find { it.email == email }
    }

    init {
        _users.add(
            User(
                id = 1,
                name = "Олена",
                gender = Gender.FEMALE,
                birthday = LocalDate.of(1995, 6, 15),
                email = "olena@example.com"
            )
        )
    }
}
