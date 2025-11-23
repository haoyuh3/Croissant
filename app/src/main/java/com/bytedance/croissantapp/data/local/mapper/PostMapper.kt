package com.bytedance.croissantapp.data.local.mapper

import com.bytedance.croissantapp.data.local.entity.PostEntity
import com.bytedance.croissantapp.domain.model.Author
import com.bytedance.croissantapp.domain.model.Clip
import com.bytedance.croissantapp.domain.model.Hashtag
import com.bytedance.croissantapp.domain.model.Music
import com.bytedance.croissantapp.domain.model.Post
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Post 与 PostEntity 之间的转换工具
 *
 * 使用 Gson 将复杂对象序列化为 JSON 字符串存储到数据库
 */

// Gson 实例（单例）
private val gson = Gson()

/**
 * 将 Post (领域模型) 转换为 PostEntity (数据库实体)
 *
 * 复杂对象转换为 JSON 字符串：
 * - List<Hashtag> -> hashtagsJson
 * - Author -> authorJson
 * - List<Clip> -> clipsJson
 * - Music? -> musicJson
 */
fun Post.toEntity(): PostEntity {
    return PostEntity(
        this.postId,
        this.title,
        this.content,
        gson.toJson(this.hashtags),
        this.createTime,
        gson.toJson(this.author),
        gson.toJson(this.clips),
        this.music?.let { gson.toJson(it) },
        this.likeCount,
        this.isLiked
    )
}

/**
 * 将 PostEntity (数据库实体) 转换为 Post (领域模型)
 *
 * JSON 字符串反序列化为复杂对象
 */
fun PostEntity.toDomain(): Post {
    // 定义泛型类型
    val hashtagsType = object : TypeToken<List<Hashtag>>() {}.type
    val clipsType = object : TypeToken<List<Clip>>() {}.type

    return Post(
        postId = postId,
        title = title,
        content = content,
        hashtags = gson.fromJson(hashtagsJson, hashtagsType),
        createTime = createTime,
        author = gson.fromJson(authorJson, Author::class.java),
        clips = gson.fromJson(clipsJson, clipsType),
        music = musicJson?.let { gson.fromJson(it, Music::class.java) },
        likeCount = likeCount,
        isLiked = isLiked
    )
}

/**
 * 批量转换：List<Post> -> List<PostEntity>
 */
fun List<Post>.toEntityList(): List<PostEntity> {
    return this.map { it.toEntity() }
}

/**
 * 批量转换：List<PostEntity> -> List<Post>
 */
fun List<PostEntity>.toDomainList(): List<Post> {
    return this.map { it.toDomain() }
}
