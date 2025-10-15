package com.example.razomua.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.razomua.RazomUAApp
import com.example.razomua.repository.ProfileRepository

class SyncProfilesWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val profileRepo = ProfileRepository(RazomUAApp.database.profileDao())
            profileRepo.refreshProfilesFromServer()

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
