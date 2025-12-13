
package com.example.razomua.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.razomua.model.*
import com.example.razomua.repository.FirebaseSwipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SwipeViewModel(
    private val repository: FirebaseSwipeRepository = FirebaseSwipeRepository()
) : ViewModel() {

    private val _availableUsers = MutableStateFlow<List<ChatUser>>(emptyList())
    val availableUsers: StateFlow<List<ChatUser>> = _availableUsers.asStateFlow()

    private val _currentUserIndex = MutableStateFlow(0)
    val currentUserIndex: StateFlow<Int> = _currentUserIndex.asStateFlow()

    private val _matchResult = MutableStateFlow<Match?>(null)
    val matchResult: StateFlow<Match?> = _matchResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _likesReceived = MutableStateFlow(0)
    val likesReceived: StateFlow<Int> = _likesReceived.asStateFlow()

    init {
        loadAvailableUsers()
        observeLikes()
    }
    private fun observeLikes() {
        viewModelScope.launch {
            repository.getLikesReceivedCountFlow().collect { count ->
                _likesReceived.value = count
            }
        }
    }
    fun loadAvailableUsers() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = repository.getAvailableUsers()
                result.onSuccess { users ->
                    _availableUsers.value = users
                    _currentUserIndex.value = 0
                }
                result.onFailure { e ->
                    Log.e("SwipeViewModel", "Error loading users", e)
                }
            } catch (e: Exception) {
                Log.e("SwipeViewModel", "Exception in loadAvailableUsers", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sendSwipe(toUserId: String, action: SwipeAction) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = repository.sendSwipe(toUserId, action)
                result.onSuccess { match ->
                    if (match != null) _matchResult.value = match
                    _currentUserIndex.value += 1
                }
                result.onFailure { e ->
                    Log.e("SwipeViewModel", "Error sending swipe", e)
                }
            } catch (e: Exception) {
                Log.e("SwipeViewModel", "Exception in sendSwipe", e)
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun dismissMatchDialog() {
        viewModelScope.launch {
            _matchResult.value?.let { match ->
                repository.markMatchAsViewed(match.id)
            }
            _matchResult.value = null
        }
    }

    fun getCurrentUser(): ChatUser? {
        val index = _currentUserIndex.value
        return if (index < _availableUsers.value.size) {
            _availableUsers.value[index]
        } else {
            null
        }
    }
}
