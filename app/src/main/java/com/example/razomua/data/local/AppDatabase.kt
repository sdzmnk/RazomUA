//package com.example.razomua.data.local
//
//import androidx.databinding.adapters.Converters
//import androidx.room.Database
//import androidx.room.RoomDatabase
//import androidx.room.TypeConverters
//import com.example.razomua.data.local.dao.UserDao
//import com.example.razomua.data.local.dao.ProfileDao
//import com.example.razomua.data.local.dao.SwipeDao
//import com.example.razomua.data.local.entity.UserEntity
//import com.example.razomua.data.local.entity.ProfileEntity
//import com.example.razomua.data.local.entity.SwipeEntity
//
//@Database(
//    entities = [UserEntity::class, ProfileEntity::class, SwipeEntity::class],
//    version = 1
//)
//@TypeConverters(Converters::class)
//abstract class AppDatabase : RoomDatabase() {
//    abstract fun userDao(): UserDao
//    abstract fun profileDao(): ProfileDao
//    abstract fun swipeDao(): SwipeDao
//}



package com.example.razomua.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.razomua.data.local.dao.UserDao
import com.example.razomua.data.local.dao.ProfileDao
import com.example.razomua.data.local.dao.SwipeDao
import com.example.razomua.data.local.entity.UserEntity
import com.example.razomua.data.local.entity.ProfileEntity
import com.example.razomua.data.local.entity.SwipeEntity

@Database(
    entities = [UserEntity::class, ProfileEntity::class, SwipeEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun profileDao(): ProfileDao
    abstract fun swipeDao(): SwipeDao
}
