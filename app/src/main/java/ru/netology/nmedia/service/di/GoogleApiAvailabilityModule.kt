package ru.netology.nmedia.service.di

import com.google.android.gms.common.GoogleApiAvailability
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class GoogleApiAvailabilityModule {
    @Singleton
    @Provides
    fun provideGapi(): GoogleApiAvailability = GoogleApiAvailability.getInstance()
}