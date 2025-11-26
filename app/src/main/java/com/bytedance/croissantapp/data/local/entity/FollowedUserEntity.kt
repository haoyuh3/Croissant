package com.bytedance.croissantapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 关注用户数据库实体
 */
@Entity(tableName = "followed_users")
data class FollowedUserEntity(
    @PrimaryKey
    val userId: String,
    val nickname: String,
    val avatar: String,
    val bio: String = "",
    val followedTime: Long = System.currentTimeMillis()
)
