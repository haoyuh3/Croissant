package com.bytedance.croissantapp.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import com.bytedance.croissantapp.util.Constants
import androidx.core.content.edit

/**
 * 用户偏好设置管理（基于SharedPreferences）
 */
@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext context: Context
) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("croissant_prefs", Context.MODE_PRIVATE)

    // ==================== 点赞状态 ====================
    fun setLikeStatus(postId: String, isLiked: Boolean) {
        sharedPreferences.edit { putBoolean(Constants.KEY_LIKE_PREFIX + postId, isLiked) }
    }

    fun getLikeStatus(postId: String): Boolean {
        return sharedPreferences.getBoolean(Constants.KEY_LIKE_PREFIX + postId, false)
    }

    // ==================== 关注状态 ====================

    fun setFollowStatus(userId: String, isFollowed: Boolean) {
        sharedPreferences.edit { putBoolean(Constants.KEY_FOLLOW_PREFIX + userId, isFollowed) }
    }

    fun getFollowStatus(userId: String): Boolean {
        return sharedPreferences.getBoolean(Constants.KEY_FOLLOW_PREFIX + userId, false)
    }

    // ==================== 音乐静音状态 ====================

    fun setMusicMuteStatus(isMuted: Boolean) {
        sharedPreferences.edit { putBoolean(Constants.KEY_MUSIC_MUTE, isMuted) }
    }

    fun getMusicMuteStatus(): Boolean {
        return sharedPreferences.getBoolean(Constants.KEY_MUSIC_MUTE, false)
    }

    // ==================== 用户资料 ====================

    fun setUserNickname(nickname: String) {
        sharedPreferences.edit { putString(Constants.KEY_USER_NICKNAME, nickname) }
    }

    fun getUserNickname(): String {
        return sharedPreferences.getString(Constants.KEY_USER_NICKNAME, "用户昵称") ?: "用户昵称"
    }

    fun setUserBio(bio: String) {
        sharedPreferences.edit { putString(Constants.KEY_USER_BIO, bio) }
    }

    fun getUserBio(): String {
        return sharedPreferences.getString(Constants.KEY_USER_BIO, "这里是个人简介") ?: "这里是个人简介"
    }

    fun setUserAvatar(uri: String) {
        sharedPreferences.edit { putString(Constants.KEY_USER_AVATAR, uri) }
    }

    fun getUserAvatar(): String? {
        return sharedPreferences.getString(Constants.KEY_USER_AVATAR, null)
    }
}