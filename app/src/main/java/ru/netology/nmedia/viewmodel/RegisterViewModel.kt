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
import ru.netology.nmedia.error.*
import ru.netology.nmedia.model.RegisterState
import java.io.IOException
import java.net.HttpURLConnection
import javax.inject.Inject
@HiltViewModel
class RegisterViewModel @Inject constructor (
    private val apiService : ApiService,
    private val appAuth: AppAuth

): ViewModel() {
    private val _dataState = MutableLiveData<RegisterState>()
    val dataState: LiveData<RegisterState>
        get() = _dataState

    //Managing response from server after auth try
    fun tryRegister(login: String, password: String, username: String) {
        viewModelScope.launch {
            try {
                val result = register(login, password, username)
                appAuth.setAuth(result.id, result.token)

            } catch (e: ApiError) {
                _dataState.value = when (e.status) {
                    HttpURLConnection.HTTP_CONFLICT -> {
                        RegisterState(userAlreadyExists = true)
                    }

                    HttpURLConnection.HTTP_BAD_REQUEST -> {
                        RegisterState(error = true)
                    }

                    else -> {
                        RegisterState(error = true)
                    }
                }
            } catch (e: Exception) {
                _dataState.value = RegisterState(error = true)
            }
        }
    }

    //Request to server for auth
    private suspend fun register(login: String, password: String, username: String): Token {

        val response = try {
            apiService.registerUser(login, password, username)
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