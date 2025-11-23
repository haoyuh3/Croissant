package com.bytedance.croissantapp.data.model

/**
 * Data layer models (DTOs) will be defined here
 */
import com.bytedance.croissantapp.domain.model.*
import com.google.gson.annotations.SerializedName

/**
 * Feed接口响应
 */
data class FeedResponse(
    @SerializedName("status_code") // 转化json
    val statusCode: Int,

    @SerializedName("has_more")
    val hasMore: Int,

    @SerializedName("post_list")
    val postList: List<PostDto>?
){
    val postListSize: Int
        get() = postList?.size ?: 0
}

/**
 * 作品DTO
 */
data class PostDto(
    @SerializedName("post_id")
    val postId: String?, // postId 可能为null

    val title: String?,
    val content: String?,

    @SerializedName("hashtag")
    val hashtags: List<HashtagDto>?,  // 可能为 null

    @SerializedName("create_time")
    val createTime: Long?,

    val author: AuthorDto?,
    val clips: List<ClipDto>?,  // 可能为 null
    val music: MusicDto?
)

data class AuthorDto(
    @SerializedName("user_id")
    val userId: String,
    val nickname: String,
    val avatar: String
)

data class ClipDto(
    val type: Int,  // 0: 图片, 1: 视频
    val width: Int,
    val height: Int,
    val url: String
)

data class HashtagDto(
    val start: Int,
    val end: Int
)

data class MusicDto(
    val volume: Int,
    @SerializedName("seek_time")
    val seekTime: Long,
    val url: String
)


// ==================== Mapper ====================

/**
 * DTO → Domain Model 映射
 */
fun PostDto.toDomain(): Post {
    return Post(
        postId = postId ?: "",
        title = title ?: "",
        content = content ?: "",
        hashtags = hashtags?.map { it.toDomain() } ?: emptyList(),  // 处理 null
        createTime = createTime ?: 0L,
        author = author?.toDomain() ?: Author("", "空用户名, 未知用户", ""),
        clips = clips?.map { it.toDomain() } ?: emptyList(),  // 处理 null
        music = music?.toDomain(),
        likeCount = 0,  // 默认值，实际应从API返回
        isLiked = false  // 从sharePreference读取的本地状态
    )
}

fun AuthorDto.toDomain(): Author {
    return Author(
        userId = userId,
        nickname = nickname,
        avatar = avatar,
        isFollowed = false  // 从sharePreference读取的本地状态
    )
}

fun ClipDto.toDomain(): Clip {
    return Clip(
        type = if (type == 0) ClipType.IMAGE else ClipType.VIDEO,
        width = width,
        height = height,
        url = url
    )
}

fun HashtagDto.toDomain(): Hashtag{
    return Hashtag(
        start = start,
        end = end,
    )
}

fun MusicDto.toDomain(): Music {
    return Music(
        volume = volume,
        seekTime = seekTime,
        url = url
    )
}