package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Token
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.model.LoginState
import java.io.IOException
import java.net.HttpURLConnection

class LoginViewModel : ViewModel() {
    //Feed state: loading, error, refreshing
    private val _dataState = MutableLiveData<LoginState>()
    val dataState: LiveData<LoginState>
        get() = _dataState

    //Managing response from server after auth try
    fun tryLogin(username: String, password: String) {
        viewModelScope.launch {
            try {
                //    _dataState.value = LoginState(loading = true)
                val result = login(username, password)
                AppAuth.getINstance().setAuth(result)

            } catch (e: ApiError) {
                _dataState.value = when (e.status) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        LoginState(userNotFoundError = true)
                    }

                    HttpURLConnection.HTTP_BAD_REQUEST -> {
                        LoginState(incorrectPasswordError = true)
                    }

                    else -> {
                        LoginState(error = true)
                    }
                }
            } catch (e: Exception) {
                _dataState.value = LoginState(error = true)
            }
        }

    }

    //Request to server for auth
    suspend fun login(username: String, password: String): Token {
        try {
            val response = PostsApi.service.updateUser(username, password)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val token = response.body() ?: throw ApiError(response.code(), response.message())
            return token
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}