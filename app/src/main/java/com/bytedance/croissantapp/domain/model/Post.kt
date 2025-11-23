package com.bytedance.croissantapp.domain.model

/**
 * 作品（帖子）模型
 */
data class Post(
    val postId: String,
    val title: String,
    val content: String,
    val hashtags: List<Hashtag>,
    val createTime: Long,
    val author: Author,
    val clips: List<Clip>,
    val music: Music?,
    val likeCount: Int = 0,
    val isLiked: Boolean = false  // 从sharePreference读取的本地状态
)

/**
 * 作者信息
 */
data class Author(
    val userId: String,
    val nickname: String,
    val avatar: String,
    val isFollowed: Boolean = false  // 从sharePreference读取的本地状态
)

/**
 * 图片/视频片段
 */
data class Clip(
    val type: ClipType,
    val width: Int,
    val height: Int,
    val url: String
) {
    /**
     * 计算显示用的宽高比
     * 根据设计要求，限制在 3:4 ~ 4:3 之间
     */
    val displayAspectRatio: Float
        get() {
            val ratio = width.toFloat() / height
            return if (ratio < 0.75f) {
                0.75f

            } else if (ratio > 1.33f) {
                1.33f
            } else {
                ratio
            }
        }
}

/**
 * 片段类型
 */
enum class ClipType {
    IMAGE,  // 图片
    VIDEO   // 视频
}

/**
 * 话题词位置信息
 */
data class Hashtag(
    val start: Int,  // 起始下标（闭区间）
    val end: Int     // 终止下标（开区间）
)

/**
 * 背景音乐信息
 */
data class Music(
    val volume: Int,
    val seekTime: Long,
    val url: String
)