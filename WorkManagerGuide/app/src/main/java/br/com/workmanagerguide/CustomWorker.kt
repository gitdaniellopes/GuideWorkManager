package br.com.workmanagerguide

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.net.UnknownHostException

@HiltWorker
class CustomWorker @AssistedInject constructor(
    @Assisted private val api: DemoApi,
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        return try {
            val response = api.getPost()
            return if (response.isSuccessful) {
                Log.d("CustomerWorker", "doWork: Success")
                Log.d(
                    "CustomerWorker",
                    "Id: ${response.body()?.id} Title: ${response.body()?.title}"
                )
                Result.success()
            } else {
                Log.d("CustomerWorker", "doWork: Retrying...")
                Result.retry()
            }

        } catch (e: Exception) {
            if (e is UnknownHostException) {
                Log.d("CustomerWorker", "doWork: Retrying...")
                Result.retry()
            } else {
                Log.d("CustomerWorker", "doWork: Retrying...")
                Result.failure(Data.Builder().putString("error", e.toString()).build())
            }
        }
    }
}