package ru.netology.nmedia.model

data class RegisterState(
    val userAlreadyExists: Boolean = false,
    val error: Boolean = false,
)

