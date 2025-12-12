package com.example.razomua.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.razomua.repository.FirebaseSwipeRepository

class SwipeViewModelFactory(
    private val repository: FirebaseSwipeRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SwipeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SwipeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
