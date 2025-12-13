package com.example.razomua.model
import kotlinx.serialization.Serializable

data class User(
    val id: String = "",
    val name: String = "",
    val birthday: String = "",
    val gender: String = "",
    val genderPreference: String = "",
    val location: String = "",
    val purpose: String = "",
    val photoUrl: String = "",
    val hobbies: List<String> = emptyList(),
    val isOnline: Boolean = false,
    val lastSeen: Long = 0
) {
    fun getAge(): Int {
        if (birthday.isEmpty()) return 0

        return try {
            val parts = birthday.split(".")
            if (parts.size == 3) {
                val birthYear = parts[2].toInt()
                val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
                currentYear - birthYear
            } else {
                0
            }
        } catch (e: Exception) {
            0
        }
    }
}


