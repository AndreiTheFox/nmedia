package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState

class AuthViewModel : ViewModel() {
    val state: LiveData<AuthState> = AppAuth.getInstance()
        .authStateFlow
        .asLiveData()
    val authorized: Boolean
        get() = AppAuth.getInstance().authStateFlow.value.id != 0L
}