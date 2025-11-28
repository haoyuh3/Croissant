package com.bytedance.croissantapp.domain.repository

import com.bytedance.croissantapp.domain.model.Post

/**
 * Feed数据仓库接口
 */
interface FeedRepository {
    // TODO: Set acceptVideoClip
    // Image Only
//    suspend fun getFeed(count: Int, acceptVideoClip: Boolean = false): Result<List<Post>>
    suspend fun getFeed(count: Int, acceptVideoClip: Boolean = true): Result<List<Post>>

    /**
     * 从缓存中获取指定Post
     */
    suspend fun getCachedPost(postId: String): Post?

    /**
     * 从缓存中随机获取指定数量的Post
     */
    suspend fun getLatestCachedPosts(count: Int): List<Post>
}