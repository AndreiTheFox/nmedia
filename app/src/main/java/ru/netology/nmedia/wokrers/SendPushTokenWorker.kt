package ru.netology.nmedia.wokrers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.api.ApiNmedia
import ru.netology.nmedia.dto.PushToken

class SendPushTokenWorker(
    context: Context,
    parameters: WorkerParameters,
) : CoroutineWorker(context, parameters) {
    companion object {
        const val TOKEN_KEY="TOKEN_KEY"
        const val NAME="SendPushTokenWorker"

    }
    override suspend fun doWork(): Result {
        val token = inputData.getString(TOKEN_KEY)
        return try {
            ApiNmedia.service.savePushToken(
                PushToken(
                    token ?: FirebaseMessaging.getInstance().token.await()
                )
            )
            Result.success()
        }catch (e:Exception){
            e.printStackTrace()
            Result.retry()
        }
    }
}