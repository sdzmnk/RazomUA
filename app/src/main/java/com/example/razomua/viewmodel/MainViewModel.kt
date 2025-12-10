//package com.example.razomua.viewmodel
//
//import android.os.Build
//import androidx.annotation.RequiresApi
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.razomua.model.Profile
//import com.example.razomua.model.Swipe
//import com.example.razomua.model.SwipeAction
//import com.example.razomua.model.User
//import kotlinx.coroutines.flow.*
//import kotlinx.coroutines.launch
//
//class MainViewModel(
//    private val userViewModel: UserViewModel,
//    private val swipeViewModel: SwipeViewModel
//) : ViewModel() {
//
//    private val _profile = MutableStateFlow<Profile?>(null)
//    val profile: StateFlow<Profile?> = _profile.asStateFlow()
//
//    private val _events = MutableSharedFlow<String>()
//    val events = _events.asSharedFlow()
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    val availableUsers: StateFlow<List<User>> = combine(
//        profile,
//        userViewModel.usersFlow
//    ) { currentProfile, users ->
//        if (currentProfile == null) users
//        else users.filter { it.id != currentProfile.userId }
//    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
//
//    val userSwipes: StateFlow<List<Swipe>> = combine(
//        profile,
//        swipeViewModel.swipesFlow
//    ) { currentProfile, allSwipes ->
//        if (currentProfile == null) emptyList()
//        else allSwipes.filter { it.fromUserId == currentProfile.userId }
//    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
//
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun login(email: String, password: String) {
//        val user = userViewModel.getUserByEmail(email)
//        if (user == null) {
//            sendEvent("Користувача не знайдено!")
//            return
//        }
//
//        val newProfile = Profile(
//            id = user.id,
//            userId = user.id,
//            photoUrl = null,
//            location = "Kyiv"
//        )
//        _profile.value = newProfile
//        sendEvent("Вхід успішний!")
//    }
//
//    fun logout() {
//        _profile.value = null
//        sendEvent("Ви вийшли з акаунту.")
//    }
//
//    fun likeUser(targetUserId: Int) {
//        val currentUserId = _profile.value?.userId ?: return
//        swipeViewModel.addSwipe(currentUserId, targetUserId, SwipeAction.LIKE)
//    }
//
//    fun dislikeUser(targetUserId: Int) {
//        val currentUserId = _profile.value?.userId ?: return
//        swipeViewModel.addSwipe(currentUserId, targetUserId, SwipeAction.DISLIKE)
//    }
//
//    private fun sendEvent(message: String) {
//        viewModelScope.launch {
//            _events.emit(message)
//        }
//    }
//}


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

    // Current user profile
    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    // Available users for swiping
    private val _availableUsers = MutableStateFlow<List<ChatUser>>(emptyList())
    val availableUsers: StateFlow<List<ChatUser>> = _availableUsers.asStateFlow()

    // User's swipes
    private val _userSwipes = MutableStateFlow<List<Swipe>>(emptyList())
    val userSwipes: StateFlow<List<Swipe>> = _userSwipes.asStateFlow()

    // User's matches
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

                // Notify about new matches
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