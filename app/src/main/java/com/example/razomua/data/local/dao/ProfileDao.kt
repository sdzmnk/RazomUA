package com.example.razomua.data.local.dao

import androidx.room.*
import com.example.razomua.data.local.entity.ProfileEntity

@Dao
interface ProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: ProfileEntity)

    @Query("SELECT * FROM profiles WHERE userId = :userId")
    suspend fun getProfileByUserId(userId: Int): ProfileEntity?

    @Query("SELECT * FROM profiles")
    suspend fun getAllProfiles(): List<ProfileEntity>

    @Delete
    suspend fun delete(profile: ProfileEntity)
}
