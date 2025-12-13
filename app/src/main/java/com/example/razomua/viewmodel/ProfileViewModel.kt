package com.example.razomua.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.razomua.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference("users")

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            _isLoading.value = true
            val userId = auth.currentUser?.uid

            if (userId != null) {
                try {
                    val snapshot = usersRef.child(userId).get().await()

                    val hobbiesList = mutableListOf<String>()
                    snapshot.child("hobbies").children.forEach { hobbySnapshot ->
                        hobbySnapshot.getValue(String::class.java)?.let { hobbiesList.add(it) }
                    }

                    val user = User(
                        id = snapshot.child("id").getValue(String::class.java) ?: userId,
                        name = snapshot.child("name").getValue(String::class.java) ?: "",
                        birthday = snapshot.child("birthday").getValue(String::class.java) ?: "",
                        gender = snapshot.child("gender").getValue(String::class.java) ?: "",
                        genderPreference = snapshot.child("genderPreference").getValue(String::class.java) ?: "",
                        location = snapshot.child("location").getValue(String::class.java) ?: "",
                        purpose = snapshot.child("purpose").getValue(String::class.java) ?: "",
                        photoUrl = snapshot.child("photoUrl").getValue(String::class.java) ?: "",
                        hobbies = hobbiesList,
                        isOnline = snapshot.child("isOnline").getValue(Boolean::class.java) ?: false,
                        lastSeen = snapshot.child("lastSeen").getValue(Long::class.java) ?: 0L
                    )

                    _currentUser.value = user
                    Log.d("ProfileViewModel", "User loaded: ${user.name}")
                } catch (e: Exception) {
                    Log.e("ProfileViewModel", "Error loading user", e)
                }
            }

            _isLoading.value = false
        }
    }

    fun updateUser(updates: Map<String, Any>) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch

            try {
                usersRef.child(userId).updateChildren(updates).await()
                loadCurrentUser()
                Log.d("ProfileViewModel", "User updated successfully")
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error updating user", e)
            }
        }
    }

    fun refreshUser() {
        loadCurrentUser()
    }
}