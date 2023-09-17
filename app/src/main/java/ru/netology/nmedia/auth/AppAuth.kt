package ru.netology.nmedia.auth

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.*
import ru.netology.nmedia.dto.Token

class AppAuth private constructor(context: Context) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
//    private val idKey = "id"
//    private val tokenKey = "token"
    private val _authFlow = MutableStateFlow<Token?>(null)
    val authFlow = _authFlow.asStateFlow()

    init {
        val token = prefs.getString(TOKEN_KEY, null)
        val id = prefs.getLong(ID_KEY, 0L)
        if (token == null || id == 0L){
            prefs.edit { clear() }
        } else{
            _authFlow.value = Token(id=id, token= token)
        }
    }
    fun setAuth (token: Token){
        prefs.edit {
            putLong(ID_KEY, token.id)
            putString(TOKEN_KEY, token.token)
        }
        _authFlow.value = token
    }

    fun clear(){
        prefs.edit { clear() }
        _authFlow.value = null
    }

    companion object {
        @Volatile
        private var instance : AppAuth? = null

        fun getINstance (): AppAuth = requireNotNull(instance){
            "Initilize Instance: AppAuth"
        }

        private const val TOKEN_KEY = "TOKEN_KEY"
        private const val ID_KEY = "ID_KEY"


        fun init (context: Context){
            instance = AppAuth(context.applicationContext)
        }
    }
}