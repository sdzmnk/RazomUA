package com.example.razomua.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.razomua.model.Swipe
import com.example.razomua.model.SwipeAction
import java.util.Date
import kotlin.random.Random

class SwipeViewModel : ViewModel() {

    private val _swipes = MutableLiveData<List<Swipe>>(emptyList())
    val swipes: LiveData<List<Swipe>> get() = _swipes

    fun addSwipe(fromUserId: Long, toUserId: Long, action: SwipeAction) {
        val newSwipe = Swipe(
            id = generateSwipeId(),
            fromUserId = fromUserId,
            toUserId = toUserId,
            action = action,
            createdAt = Date()
        )

        _swipes.value = _swipes.value?.plus(newSwipe)
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
