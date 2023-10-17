package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Token
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.model.LoginState
import java.io.IOException
import java.net.HttpURLConnection
import javax.inject.Inject
@HiltViewModel
class LoginViewModel @Inject constructor (
    private val apiService : ApiService,
    private val appAuth: AppAuth

): ViewModel() {
    private val _dataState = MutableLiveData<LoginState>()
    val dataState: LiveData<LoginState>
        get() = _dataState

    //Managing response from server after auth try
    fun tryLogin(username: String, password: String) {
        viewModelScope.launch {
            try {
                val result = login(username, password)
                appAuth.setAuth(result.id, result.token)

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
    private suspend fun login(username: String, password: String): Token {

        val response = try {
            apiService.updateUser(username, password)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
        if (!response.isSuccessful) {
            throw ApiError(status = response.code(), code = response.message())
        }
        return response.body() ?: throw ApiError(
            status = response.code(),
            code = response.message()
        )
    }
}

