package com.bytedance.croissantapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bytedance.croissantapp.data.local.entity.FollowedUserEntity
import kotlinx.coroutines.flow.Flow

/**
 * 关注用户数据访问对象
 */
@Dao
interface FollowedUserDao {

    /**
     * 插入或更新关注的用户（非 suspend，供 Java 调用）
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFollowedUser(user: FollowedUserEntity)

    /**
     * 删除关注的用户
     */
    @Delete
    fun deleteFollowedUser(user: FollowedUserEntity)

    /**
     * 根据用户ID删除关注（非 suspend，供 Java 调用）
     */
    @Query("DELETE FROM followed_users WHERE userId = :userId")
    fun deleteFollowedUserById(userId: String)

    /**
     * 获取所有关注的用户列表（按关注时间倒序）
     */
    @Query("SELECT * FROM followed_users ORDER BY followedTime DESC")
    fun getAllFollowedUsers(): Flow<List<FollowedUserEntity>>

    /**
     * 获取所有关注的用户列表（一次性查询，非 suspend，供 Java 调用）
     */
    @Query("SELECT * FROM followed_users ORDER BY followedTime DESC")
    fun getAllFollowedUsersOnce(): List<FollowedUserEntity>

    /**
     * 检查用户是否已关注
     */
    @Query("SELECT EXISTS(SELECT 1 FROM followed_users WHERE userId = :userId)")
    fun isUserFollowed(userId: String): Boolean

    /**
     * 获取关注用户数量
     */
    @Query("SELECT COUNT(*) FROM followed_users")
    fun getFollowedUserCount(): Int
}
