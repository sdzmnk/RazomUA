package com.example.razomua.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.razomua.model.Profile
import com.example.razomua.model.Swipe
import com.example.razomua.model.SwipeAction
import com.example.razomua.model.User
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val userViewModel: UserViewModel,
    private val swipeViewModel: SwipeViewModel
) : ViewModel() {

    private val _profile = MutableStateFlow<Profile?>(null)
    val profile: StateFlow<Profile?> = _profile.asStateFlow()

    private val _events = MutableSharedFlow<String>()
    val events = _events.asSharedFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    val availableUsers: StateFlow<List<User>> = combine(
        profile,
        userViewModel.usersFlow
    ) { currentProfile, users ->
        if (currentProfile == null) users
        else users.filter { it.id != currentProfile.userId }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val userSwipes: StateFlow<List<Swipe>> = combine(
        profile,
        swipeViewModel.swipesFlow
    ) { currentProfile, allSwipes ->
        if (currentProfile == null) emptyList()
        else allSwipes.filter { it.fromUserId == currentProfile.userId }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())


    @RequiresApi(Build.VERSION_CODES.O)
    fun login(email: String, password: String) {
        val user = userViewModel.getUserByEmail(email)
        if (user == null) {
            sendEvent("Користувача не знайдено!")
            return
        }

        val newProfile = Profile(
            id = user.id,
            userId = user.id,
            photoUrl = null,
            location = "Kyiv"
        )
        _profile.value = newProfile
        sendEvent("Вхід успішний!")
    }

    fun logout() {
        _profile.value = null
        sendEvent("Ви вийшли з акаунту.")
    }

    fun likeUser(targetUserId: Long) {
        val currentUserId = _profile.value?.userId ?: return
        swipeViewModel.addSwipe(currentUserId, targetUserId, SwipeAction.LIKE)
    }

    fun dislikeUser(targetUserId: Long) {
        val currentUserId = _profile.value?.userId ?: return
        swipeViewModel.addSwipe(currentUserId, targetUserId, SwipeAction.DISLIKE)
    }

    private fun sendEvent(message: String) {
        viewModelScope.launch {
            _events.emit(message)
        }
    }
}
