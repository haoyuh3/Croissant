package com.bytedance.crossiantapp.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Application level dependency injection module
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // Dependencies will be added here
}