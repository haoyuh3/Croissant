package com.bytedance.croissantapp.di

import com.bytedance.croissantapp.data.local.dao.PostDao
import com.bytedance.croissantapp.data.remote.FeedApi
import com.bytedance.croissantapp.domain.repository.FeedRepository
import com.bytedance.croissantapp.domain.repository.FeedRepositoryImpl
import com.bytedance.croissantapp.domain.usecase.GetPostDetailUseCase
import com.bytedance.croissantapp.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * 提供OkHttpClient
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * 提供Retrofit实例
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * 提供FeedApi实例
     */
    @Provides
    @Singleton
    fun provideFeedApi(retrofit: Retrofit): FeedApi {
        return retrofit.create(FeedApi::class.java)
    }

    /**
     * 提供FeedRepository实现
     */
    @Provides
    @Singleton
    fun provideFeedRepository(
        feedApi: FeedApi,
        postDao: PostDao
    ): FeedRepository {
        return FeedRepositoryImpl(feedApi, postDao)
    }

    @Provides
    @Singleton
    fun provideGetPostDetailUseCase(
        feedRepository: FeedRepository
    ): GetPostDetailUseCase {
        return GetPostDetailUseCase(feedRepository)
    }
}