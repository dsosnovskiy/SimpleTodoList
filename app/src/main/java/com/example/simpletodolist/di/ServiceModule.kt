package com.example.simpletodolist.di

import android.content.Context
import com.example.simpletodolist.notifications.NotificationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Singleton
    @Provides
    fun provideNotificationService(@ApplicationContext context: Context): NotificationService {
        return NotificationService(context)
    }
}