package com.bytedance.croissantapp.domain.usecase

import com.bytedance.croissantapp.domain.model.Post
import com.bytedance.croissantapp.domain.repository.FeedRepository
import javax.inject.Inject

class GetFeedUseCaseCache @Inject constructor (
    private val feedRepository: FeedRepository
) {
    suspend operator fun invoke(
        count: Int = 10,
    ): List<Post> {
        return feedRepository.getLatestCachedPosts(count)
    }
}