package com.example.razomua.data.local.dao

import androidx.room.*
import com.example.razomua.data.local.entity.SwipeEntity

@Dao
interface SwipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(swipe: SwipeEntity)

    @Query("SELECT * FROM swipes WHERE fromUserId = :fromUserId")
    suspend fun getSwipesFromUser(fromUserId: Long): List<SwipeEntity>

    @Query("SELECT * FROM swipes")
    suspend fun getAllSwipes(): List<SwipeEntity>

    @Delete
    suspend fun delete(swipe: SwipeEntity)
}
