package com.example.razomua

import android.app.Application
import androidx.room.Room
import com.example.razomua.data.local.AppDatabase

class RazomUAApp : Application() {

    companion object {
        lateinit var database: AppDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "razomua_db"
        ).build()
    }
}
