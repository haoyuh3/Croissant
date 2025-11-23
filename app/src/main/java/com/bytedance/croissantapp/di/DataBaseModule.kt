package com.bytedance.croissantapp.di

import android.content.Context
import androidx.room.Room
import com.bytedance.croissantapp.data.local.dao.PostDao
import com.bytedance.croissantapp.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {
    /**
     * 提供DataBase Module
     */
    @Provides
    @Singleton // 保证数据库实例在整个应用中只有一个
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME // 数据库文件名
        )
            .build()
    }

    /**
     * 提供 PostDao 的实例。
     * 这个方法依赖于上面的 provideDatabase() 方法。
     * Hilt 会自动处理这个依赖关系：它会先调用 provideDatabase() 获取 AppDatabase 实例，
     * 然后再将该实例作为参数传入此方法。
     */
    @Provides
    fun providePostDao(appDatabase: AppDatabase): PostDao {
        return appDatabase.postDao()
    }
}
