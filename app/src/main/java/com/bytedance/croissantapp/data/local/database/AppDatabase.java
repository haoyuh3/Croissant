package com.bytedance.croissantapp.data.local.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.bytedance.croissantapp.data.local.dao.FollowedUserDao;
import com.bytedance.croissantapp.data.local.dao.PostDao;
import com.bytedance.croissantapp.data.local.entity.FollowedUserEntity;
import com.bytedance.croissantapp.data.local.entity.PostEntity;

/**
 * Croissant App 的 Room 数据库
 * 用于本地缓存 Feed 数据和关注用户数据，提升用户体验
 *
 */
@Database(
    entities = {PostEntity.class, FollowedUserEntity.class},
    version = 2,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    /**
     * 获取 PostDao 实例
     * Room 会自动实现这个抽象方法
     */
    public abstract PostDao postDao();

    /**
     * 获取 FollowedUserDao 实例
     * Room 会自动实现这个抽象方法
     */
    public abstract FollowedUserDao followedUserDao();

    /**
     * 数据库名称
     */
    public static final String DATABASE_NAME = "croissant_database";
}