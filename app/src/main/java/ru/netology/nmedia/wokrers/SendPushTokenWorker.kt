package ru.netology.nmedia.wokrers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dto.PushToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SendPushTokenWorker @Inject constructor (
    @ApplicationContext
    private val context: Context,
    parameters: WorkerParameters,
) : CoroutineWorker(context, parameters) {
//    @Inject
//    lateinit var apiService: ApiService
    companion object {
        const val TOKEN_KEY="TOKEN_KEY"
        const val NAME="SendPushTokenWorker"

    }
    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface AppAuthEntryPoint {
        fun getApiService(): ApiService
    }
    override suspend fun doWork(): Result {
        val token = inputData.getString(TOKEN_KEY)
        return try {
            val entryPoint = EntryPointAccessors.fromApplication(context, AppAuthEntryPoint::class.java)
            entryPoint.getApiService().savePushToken(
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