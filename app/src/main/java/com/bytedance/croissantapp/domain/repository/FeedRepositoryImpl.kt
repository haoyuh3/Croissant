package com.bytedance.croissantapp.domain.repository

import com.bytedance.croissantapp.data.model.toDomain
import com.bytedance.croissantapp.data.remote.FeedApi
import com.bytedance.croissantapp.domain.model.Post
import javax.inject.Inject

/**
 * Feed数据仓库实现
 */
class FeedRepositoryImpl @Inject constructor(
    private val feedApi: FeedApi
) : FeedRepository {

    // 内存缓存：存储已加载的Post
    private val postCache = mutableMapOf<String, Post>()

    override suspend fun getFeed(count: Int, acceptVideoClip: Boolean): Result<List<Post>> {
        return try {
            println("FeedRepository: 开始请求API - count=$count, acceptVideoClip=$acceptVideoClip")
            val response = feedApi.getFeed(count, acceptVideoClip)
            println("FeedRepository: API响应 - statusCode=${response.statusCode}, postList size=${response.postList?.size ?: 0}")

            if (response.statusCode == 0 && response.postList != null) {
                val posts = response.postList
                    .mapNotNull { dto ->
                        try {
                            // 验证关键字段
                            if (dto.postId.isNullOrBlank()) {
                                println("FeedRepository: 跳过无效数据 - postId为空")
                                return@mapNotNull null
                            }
                            dto.toDomain()
                        } catch (e: Exception) {
                            println("FeedRepository: 转换失败 - ${e.message}")
                            null
                        }
                    }
                    .distinctBy { it.postId }  // 根据postId去重，防止API返回重复数据

                // 缓存到内存
                posts.forEach { post ->
                    postCache[post.postId] = post
                }
                println("FeedRepository: 成功转换 ${posts.size} 条有效数据，已缓存")
                Result.success(posts)
            } else {
                println("FeedRepository: API返回错误 - statusCode=${response.statusCode}")
                Result.failure(Exception("API返回错误: ${response.statusCode}"))
            }
        } catch (e: Exception) {
            println("FeedRepository: 请求失败 - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override fun getCachedPost(postId: String): Post? {
        return postCache[postId]
    }
}