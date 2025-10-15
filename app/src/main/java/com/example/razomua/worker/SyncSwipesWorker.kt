import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.razomua.RazomUAApp
import com.example.razomua.repository.SwipeRepository

class SyncSwipesWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val swipeRepo = SwipeRepository(RazomUAApp.database.swipeDao())
            val userId = inputData.getLong("userId", 0)
            swipeRepo.refreshSwipesFromServer(userId)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
