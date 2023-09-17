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
import ru.netology.nmedia.model.FeedModelState
import java.io.IOException

class LoginViewModel : ViewModel() {
    //Feed state: loading, error, refreshing
    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    //Managing response from server after auth try
    fun tryLogin(username: String, password: String) {
    viewModelScope.launch{
        try {
            val result = login(username, password)
            AppAuth.getINstance().setAuth(result)

        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
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






