package ru.netology.nmedia.application

import android.app.Application
import ru.netology.nmedia.auth.AppAuth

class NMediaApp: Application() {
    override fun onCreate() {
        super.onCreate()
        AppAuth.init(this)
    }
}