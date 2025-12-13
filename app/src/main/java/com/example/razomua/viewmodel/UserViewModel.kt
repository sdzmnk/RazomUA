//package com.example.razomua.viewmodel
//
//import android.os.Build
//import android.util.Log
//import androidx.annotation.RequiresApi
//import androidx.lifecycle.ViewModel
//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.snapshots.SnapshotStateList
//import androidx.lifecycle.viewModelScope
//import kotlinx.coroutines.launch
//import com.example.razomua.model.User
//import com.example.razomua.repository.UserRepository
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//
//@RequiresApi(Build.VERSION_CODES.O)
//class UserViewModel(private val repository: UserRepository) : ViewModel() {
//
//    private val _users: SnapshotStateList<User> = mutableStateListOf()
//    val users: List<User> get() = _users
//
//    private val _usersFlow = MutableStateFlow<List<User>>(emptyList())
//    val usersFlow = _usersFlow.asStateFlow()
//
//    fun getUserByEmail(email: String): User? {
//        return _users.find { it.email == email }
//    }
//
//
//    fun printAllUsers() {
//        viewModelScope.launch {
//            val users = repository.getAllUsersLocal()
//            users.forEach {
//                Log.d("DB_TEST", it.toString())
//            }
//        }
//    }
//
//
//    init {
//        _usersFlow.value = _users.toList()
//    }
//}
