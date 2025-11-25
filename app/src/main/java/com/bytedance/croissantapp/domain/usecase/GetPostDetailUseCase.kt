package com.bytedance.croissantapp.domain.usecase

import com.bytedance.croissantapp.domain.model.Post
import com.bytedance.croissantapp.domain.repository.FeedRepository
import javax.inject.Inject

/**
 * 获取作品详情UseCase
 */
class GetPostDetailUseCase @Inject constructor(
    private val feedRepository: FeedRepository
) {
    /**
     * 根据postId获取作品详情
     * 优先从数据库读取，未命中时再请求API
     */
    suspend operator fun invoke(postId: String): Post {
        // 1. 优先从数据库获取
        feedRepository.getCachedPost(postId)?.let { cachedPost ->
            println("GetPostDetailUseCase: 从缓存获取 postId=$postId")
            return cachedPost
        }

        // 2. 未命中，请求API
        println("GetPostDetailUseCase: 缓存未命中，请求API - postId=$postId")
        val result = feedRepository.getFeed(count = 20, acceptVideoClip = true)

        // 处理Result类型
        val posts = result.getOrElse { error ->
            throw Exception("获取作品列表失败: ${error.message}")
        }

        // 在列表中查找对应的Post（此时应该已经缓存了）
        return posts.find { it.postId == postId }
            ?: throw Exception("未找到该作品")
    }
}