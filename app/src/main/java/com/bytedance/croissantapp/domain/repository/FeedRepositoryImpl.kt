package com.bytedance.croissantapp.domain.repository

import com.bytedance.croissantapp.data.local.dao.PostDao
import com.bytedance.croissantapp.data.local.mapper.toDomain
import com.bytedance.croissantapp.data.local.mapper.toEntityList
import com.bytedance.croissantapp.data.model.toDomain
import com.bytedance.croissantapp.data.remote.FeedApi
import com.bytedance.croissantapp.domain.model.Post
import javax.inject.Inject

/**
 * Feed数据仓库实现
 */
class FeedRepositoryImpl @Inject constructor(
    private val feedApi: FeedApi,
    private val postDao: PostDao
) : FeedRepository {

    override suspend fun getFeed(count: Int, acceptVideoClip: Boolean): Result<List<Post>> {
        return try {
            println("FeedRepository: 开始请求API - count=$count, acceptVideoClip=$acceptVideoClip")
            val response = feedApi.getFeed(count, acceptVideoClip)
            println("FeedRepository: API响应 - statusCode=${response.statusCode}, postList size=${response.postListSize}")

            if (response.statusCode == 0 && response.postList?.isNotEmpty() == true) {
                val posts = mutableListOf<Post>()
                val seenPostIds = mutableSetOf<String>()

                for (dto in response.postList) {
                    try {
                        if (dto.postId.isNullOrBlank()) {
                            println("FeedRepository: 跳过无效数据 - postId为空或dto为null")
                            continue
                        }
                        // 去重
                        if (seenPostIds.contains(dto.postId)) {
                            println("FeedRepository: 跳过重复数据 - postId=${dto.postId}")
                            continue // 如果 postId 已经存在于 Set 中，跳过
                        }
                        // 转换和添加
                        val post = dto.toDomain() // 转换成领域模型
                        posts.add(post) // 添加到结果列表中
                        seenPostIds.add(post.postId) // 将这个 postId 添加到 Set 中，以便后续检查

                    } catch (e: Exception) {
                        println("FeedRepository: 转换失败 - ${e.message}")
                        continue // 转换失败也跳过
                    }
                }

                if (posts.isNotEmpty()) {
                    // 将 Post 转换为 PostEntity 后插入数据库
                    postDao.insert(posts.toEntityList())
                    println("FeedRepository: 成功转换并向数据库插入 ${posts.size} 条数据")
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
        // 从数据库查询 PostEntity，然后转换为 Post
        return postDao.findPostById(postId)?.toDomain()
    }
}