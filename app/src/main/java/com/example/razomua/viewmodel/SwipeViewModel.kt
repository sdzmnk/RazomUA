package com.example.razomua.viewmodel

import androidx.lifecycle.ViewModel
import com.example.razomua.model.Swipe
import com.example.razomua.model.SwipeAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class SwipeViewModel : ViewModel() {

    private val _swipes = MutableStateFlow<List<Swipe>>(emptyList())
    val swipesFlow = _swipes.asStateFlow()

    fun addSwipe(fromUserId: Long, toUserId: Long, action: SwipeAction) {
        val newSwipe = Swipe(
            id = Random.nextLong(1, Long.MAX_VALUE),
            fromUserId = fromUserId,
            toUserId = toUserId,
            action = action,
            createdAt = String()
        )
        _swipes.value = _swipes.value + newSwipe
    }

    private fun generateSwipeId(): Long {
        return Random.nextLong(1, Long.MAX_VALUE)
    }

    fun getLikesFromUser(userId: Long): List<Swipe> {
        return _swipes.value?.filter { it.fromUserId == userId && it.action == SwipeAction.LIKE } ?: emptyList()
    }

    fun getDislikesFromUser(userId: Long): List<Swipe> {
        return _swipes.value?.filter { it.fromUserId == userId && it.action == SwipeAction.DISLIKE } ?: emptyList()
    }
}
