


package com.example.razomua.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.razomua.model.*
import com.example.razomua.repository.FirebaseSwipeRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val swipeRepository: FirebaseSwipeRepository = FirebaseSwipeRepository()
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()


    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()


    private val _availableUsers = MutableStateFlow<List<ChatUser>>(emptyList())
    val availableUsers: StateFlow<List<ChatUser>> = _availableUsers.asStateFlow()


    private val _userSwipes = MutableStateFlow<List<Swipe>>(emptyList())
    val userSwipes: StateFlow<List<Swipe>> = _userSwipes.asStateFlow()


    private val _userMatches = MutableStateFlow<List<Match>>(emptyList())
    val userMatches: StateFlow<List<Match>> = _userMatches.asStateFlow()

    // Events for UI (like toasts)
    private val _events = MutableSharedFlow<String>()
    val events = _events.asSharedFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // Check if user is already logged in
        _currentUserId.value = auth.currentUser?.uid

        if (_currentUserId.value != null) {
            loadUserData()
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            loadAvailableUsers()
            loadUserSwipes()
            observeMatches()
        }
    }

    private fun loadAvailableUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = swipeRepository.getAvailableUsers()
            result.onSuccess { users ->
                _availableUsers.value = users
                sendEvent("Знайдено ${users.size} користувачів")
            }.onFailure { error ->
                sendEvent("Помилка завантаження користувачів: ${error.message}")
            }
            _isLoading.value = false
        }
    }

    private fun loadUserSwipes() {
        viewModelScope.launch {
            val result = swipeRepository.getUserSwipes()
            result.onSuccess { swipes ->
                _userSwipes.value = swipes
            }
        }
    }

    private fun observeMatches() {
        viewModelScope.launch {
            swipeRepository.getUserMatches().collect { matches ->
                _userMatches.value = matches


                val newMatches = matches.filter { it.isNew }
                if (newMatches.isNotEmpty()) {
                    sendEvent("У вас ${newMatches.size} нових match!")
                }
            }
        }
    }

    fun likeUser(targetUserId: String) {
        if (_currentUserId.value == null) {
            sendEvent("Спочатку увійдіть в систему")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val result = swipeRepository.sendSwipe(targetUserId, SwipeAction.LIKE)

            result.onSuccess { match ->
                if (match != null) {
                    sendEvent("🎉 Це Match!")
                } else {
                    sendEvent("Лайк відправлено")
                }
                // Reload available users
                loadAvailableUsers()
            }.onFailure { error ->
                sendEvent("Помилка: ${error.message}")
            }

            _isLoading.value = false
        }
    }

    fun dislikeUser(targetUserId: String) {
        if (_currentUserId.value == null) {
            sendEvent("Спочатку увійдіть в систему")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val result = swipeRepository.sendSwipe(targetUserId, SwipeAction.DISLIKE)

            result.onSuccess {
                sendEvent("Дізлайк відправлено")
                // Reload available users
                loadAvailableUsers()
            }.onFailure { error ->
                sendEvent("Помилка: ${error.message}")
            }

            _isLoading.value = false
        }
    }

    fun logout() {
        auth.signOut()
        _currentUserId.value = null
        _availableUsers.value = emptyList()
        _userSwipes.value = emptyList()
        _userMatches.value = emptyList()
        sendEvent("Ви вийшли з акаунту")
    }

    fun refreshData() {
        loadUserData()
    }

    private fun sendEvent(message: String) {
        viewModelScope.launch {
            _events.emit(message)
        }
    }
}