package ru.netology.nmedia.auth

import android.content.Context
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.api.ApiNmedia
import ru.netology.nmedia.dto.PushToken

class AppAuth private constructor(context: Context) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val _authStateFlow: MutableStateFlow<AuthState>
    private val idKey = "id"
    private val tokenKey = "token"

//    init {
//        val token = prefs.getString(TOKEN_KEY, null)
//        val id = prefs.getLong(ID_KEY, 0L)
//        if (token == null || id == 0L) {
//            prefs.edit { clear() }
//        } else {
//            _authFlow.value = Token(id = id, token = token)
//        }
//        sendPushToken()
//    }
init {
    val id = prefs.getLong(idKey, 0)
    val token = prefs.getString(tokenKey, null)

    if (id == 0L || token == null) {
        _authStateFlow = MutableStateFlow(AuthState())
        with(prefs.edit()) {
            clear()
            apply()
        }
    } else {
        _authStateFlow = MutableStateFlow(AuthState(id, token))
    }
    sendPushToken()
}
    val authStateFlow: StateFlow<AuthState> = _authStateFlow.asStateFlow()

    @Synchronized
    fun setAuth(id: Long, token: String) {
        _authStateFlow.value = AuthState(id, token)
        with(prefs.edit()) {
            putLong(idKey, id)
            putString(tokenKey, token)
            apply()
        }
        sendPushToken()
    }

    @Synchronized
    fun removeAuth() {
        _authStateFlow.value = AuthState()
        with(prefs.edit()) {
            clear()
            commit()
        }
        sendPushToken()
    }

    fun sendPushToken(token: String? = null) {
        CoroutineScope(Dispatchers.Default).launch {
            try{
                ApiNmedia.service.savePushToken(
                    PushToken(
                        token ?: FirebaseMessaging.getInstance().token.await()
                    )
                )
            } catch (e: Exception){
                e.printStackTrace()
            }
        }
//        WorkManager.getInstance(context)
//            .enqueueUniqueWork(
//                SendPushTokenWorker.NAME,
//                ExistingWorkPolicy.REPLACE,
//                OneTimeWorkRequestBuilder<SendPushTokenWorker>()
//                    .setConstraints(
//                        Constraints.Builder()
//                            .setRequiredNetworkType(NetworkType.CONNECTED)
//                            .build()
//                    )
//                    .setInputData(
//                        Data.Builder()
//                            .putString(SendPushTokenWorker.TOKEN_KEY, token)
//                            .build()
//                    )
//                    .build()
//            )
    }

    companion object {
        @Volatile
        private var instance: AppAuth? = null

        fun getInstance(): AppAuth = synchronized(this) {
            instance ?: throw IllegalStateException(
                "AppAuth is not initialized, you must call AppAuth.initializeApp(Context context) first."
            )
        }

        fun initApp(context: Context): AppAuth = instance ?: synchronized(this) {
            instance ?: buildAuth(context).also { instance = it }
        }

        private fun buildAuth(context: Context): AppAuth = AppAuth(context)
    }
}
data class AuthState(val id: Long = 0, val token: String? = null)