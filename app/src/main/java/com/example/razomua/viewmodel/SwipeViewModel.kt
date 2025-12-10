//package com.example.razomua.viewmodel
//
//import androidx.lifecycle.ViewModel
//import com.example.razomua.model.Swipe
//import com.example.razomua.model.SwipeAction
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlin.random.Random
//
//class SwipeViewModel : ViewModel() {
//
//    private val _swipes = MutableStateFlow<List<Swipe>>(emptyList())
//    val swipesFlow = _swipes.asStateFlow()
//
//    fun addSwipe(fromUserId: Int, toUserId: Int, action: SwipeAction) {
//        val newSwipe = Swipe(
//            id = Random.nextInt(1, Int.MAX_VALUE),
//            fromUserId = fromUserId,
//            toUserId = toUserId,
//            action = action.name,
//            createdAt = String()
//        )
//        _swipes.value = _swipes.value + newSwipe
//    }
//
//    private fun generateSwipeId(): Long {
//        return Random.nextLong(1, Long.MAX_VALUE)
//    }
//}
package com.example.razomua.viewmodel

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

    init {
        loadAvailableUsers()
    }

    fun loadAvailableUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getAvailableUsers()
            result.onSuccess { users ->
                _availableUsers.value = users
                _currentUserIndex.value = 0
            }
            _isLoading.value = false
        }
    }

    fun sendSwipe(toUserId: String, action: SwipeAction) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.sendSwipe(toUserId, action)

            result.onSuccess { match ->
                if (match != null) {
                    // Є match!
                    _matchResult.value = match
                }

                // Перейти до наступного користувача
                _currentUserIndex.value = _currentUserIndex.value + 1
            }

            _isLoading.value = false
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
