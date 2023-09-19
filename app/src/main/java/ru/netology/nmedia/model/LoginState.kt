package ru.netology.nmedia.model

data class LoginState (
    val userNotFoundError: Boolean = false,
    val incorrectPasswordError: Boolean = false,
    val error: Boolean = false,
    val loading: Boolean = false,
)