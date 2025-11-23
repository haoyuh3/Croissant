package com.bytedance.croissantapp.data.local.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.bytedance.croissantapp.data.local.dao.PostDao;
import com.bytedance.croissantapp.data.local.entity.PostEntity;

/**
 * Croissant App 的 Room 数据库
 *
 * 用于本地缓存 Feed 数据，提升用户体验
 *
 * @Database 注解参数：
 * - entities: 数据库包含的表（Entity 类）
 * - version: 数据库版本号（升级时需要递增）
 * - exportSchema: 是否导出数据库 schema（建议开发时设为 true）
 */
@Database(
    entities = {PostEntity.class},
    version = 1,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    /**
     * 获取 PostDao 实例
     * Room 会自动实现这个抽象方法
     */
    public abstract PostDao postDao();

    /**
     * 数据库名称
     */
    public static final String DATABASE_NAME = "croissant_database";
}