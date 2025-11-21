package com.bytedance.croissantapp.data.local

import com.tencent.mmkv.MMKV
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 用户偏好设置管理（基于MMKV）
 */
@Singleton
class UserPreferencesRepository @Inject constructor() {

    private val mmkv: MMKV? = MMKV.defaultMMKV()

    // ==================== 点赞状态 ====================

    fun setLikeStatus(postId: String, isLiked: Boolean) {
        mmkv?.encode(KEY_LIKE_PREFIX + postId, isLiked)
    }

    fun getLikeStatus(postId: String): Boolean {
        return mmkv?.decodeBool(KEY_LIKE_PREFIX + postId, false) ?: false
    }

    // ==================== 关注状态 ====================

    fun setFollowStatus(userId: String, isFollowed: Boolean) {
        mmkv?.encode(KEY_FOLLOW_PREFIX + userId, isFollowed)
    }

    fun getFollowStatus(userId: String): Boolean {
        return mmkv?.decodeBool(KEY_FOLLOW_PREFIX + userId, false) ?: false
    }

    // ==================== 音乐静音状态 ====================

    fun setMusicMuteStatus(isMuted: Boolean) {
        mmkv?.encode(KEY_MUSIC_MUTE, isMuted)
    }

    fun getMusicMuteStatus(): Boolean {
        return mmkv?.decodeBool(KEY_MUSIC_MUTE, false) ?: false
    }

    // ==================== 用户资料 ====================

    fun setUserNickname(nickname: String) {
        mmkv?.encode(KEY_USER_NICKNAME, nickname)
    }

    fun getUserNickname(): String {
        return mmkv?.decodeString(KEY_USER_NICKNAME, "用户昵称") ?: "用户昵称"
    }

    fun setUserBio(bio: String) {
        mmkv?.encode(KEY_USER_BIO, bio)
    }

    fun getUserBio(): String {
        return mmkv?.decodeString(KEY_USER_BIO, "这里是个人简介") ?: "这里是个人简介"
    }

    fun setUserAvatar(uri: String) {
        mmkv?.encode(KEY_USER_AVATAR, uri)
    }

    fun getUserAvatar(): String? {
        return mmkv?.decodeString(KEY_USER_AVATAR, null)
    }

    companion object {
        private const val KEY_LIKE_PREFIX = "like_status_"
        private const val KEY_FOLLOW_PREFIX = "follow_status_"
        private const val KEY_MUSIC_MUTE = "music_mute_status"
        private const val KEY_USER_NICKNAME = "user_nickname"
        private const val KEY_USER_BIO = "user_bio"
        private const val KEY_USER_AVATAR = "user_avatar"
    }
}