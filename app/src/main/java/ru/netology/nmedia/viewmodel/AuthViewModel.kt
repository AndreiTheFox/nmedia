//package ru.netology.nmedia.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.asLiveData
//import ru.netology.nmedia.auth.AppAuth
//
//class AuthViewModel : ViewModel() {
//    val state = AppAuth.getINstance().authFlow
//        .asLiveData()
//    val authorized: Boolean get() = AppAuth.getINstance().authFlow.value != null
//}
package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.Dispatchers
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState

class AuthViewModel : ViewModel() {
    val state: LiveData<AuthState> = AppAuth.getInstance()
        .authStateFlow
        .asLiveData(Dispatchers.Default)
    val authorized: Boolean
        get() = AppAuth.getInstance().authStateFlow.value.id != 0L
}