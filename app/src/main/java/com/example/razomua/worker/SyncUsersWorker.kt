//package com.example.razomua.worker
//
//import android.content.Context
//import androidx.work.CoroutineWorker
//import androidx.work.WorkerParameters
//import com.example.razomua.RazomUAApp
////import com.example.razomua.repository.UserRepository
//
//class SyncUsersWorker(
//    context: Context,
//    params: WorkerParameters
//) : CoroutineWorker(context, params) {
//
//    override suspend fun doWork(): Result {
//        return try {
//            val userRepo = UserRepository(RazomUAApp.database.userDao())
//            userRepo.refreshUsersFromServer()
//
//            Result.success()
//        } catch (e: Exception) {
//            Result.retry()
//        }
//    }
//}
