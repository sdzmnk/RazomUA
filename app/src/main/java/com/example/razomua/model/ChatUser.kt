package com.example.razomua.model

import java.util.Calendar

data class ChatUser(
    val id: String = "",
    val name: String = "",
    val lastMessage: String = "",
    val isOnline: Boolean = false,
    val lastSeen: Long = 0,
    val photoUrl: String = "",
    val birthday: String = "",
    val gender: String = "",
    val genderPreference: String = "",
    val location: String = "",
    val purpose: String = "",
    val hobbies: List<String> = emptyList()
) {

    fun calculateAge(): Int? {
        if (birthday.isEmpty()) return null

        return try {
            val parts = birthday.split(".")
            if (parts.size != 3) return null

            val day = parts[0].toInt()
            val month = parts[1].toInt()
            val year = parts[2].toInt()

            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR)
            val currentMonth = calendar.get(Calendar.MONTH) + 1
            val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

            var age = currentYear - year

            if (currentMonth < month || (currentMonth == month && currentDay < day)) {
                age--
            }

            age
        } catch (e: Exception) {
            null
        }
    }
}