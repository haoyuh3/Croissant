package com.bytedance.croissantapp.presentation.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.bytedance.croissantapp.data.local.UserPreferencesRepository
import com.bytedance.croissantapp.domain.model.Post
import com.bytedance.croissantapp.domain.usecase.GetFeedUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 首页ViewModel
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getFeedUseCase: GetFeedUseCase,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    // ==================== UI状态 ====================

    private val _uiState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    init {
        loadFeed()
    }

    // ==================== 加载Feed ====================

    /**
     * 初次加载Feed
     */
    fun loadFeed() {
        viewModelScope.launch {
            _uiState.value = FeedUiState.Loading
            println("HomeViewModel: 开始加载Feed...")

            try {
                val posts = getFeedUseCase(count = 20)
                println("HomeViewModel: 从API获取到 ${posts.size} 条数据")

                val postsWithLocalState = enrichPostsWithLocalState(posts)

                _posts.value = postsWithLocalState
                _uiState.value = if (postsWithLocalState.isEmpty()) {
                    println("HomeViewModel: 数据为空，显示Empty状态")
                    FeedUiState.Empty
                } else {
                    println("HomeViewModel: 加载成功，共 ${postsWithLocalState.size} 条")
                    FeedUiState.Success
                }
            } catch (e: Exception) {
                println("HomeViewModel: 加载失败 - ${e.message}")
                e.printStackTrace()
                _uiState.value = FeedUiState.Error(e.message ?: "未知错误")
            }
        }
    }

    /**
     * 下拉刷新
     */
    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true

            try {
                val posts = getFeedUseCase(count = 20)
                val postsWithLocalState = enrichPostsWithLocalState(posts)

                _posts.value = postsWithLocalState
                _uiState.value = if (postsWithLocalState.isEmpty()) {
                    FeedUiState.Empty
                } else {
                    FeedUiState.Success
                }
            } catch (e: Exception) {
                // 刷新失败时保持当前数据，不改变uiState
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    /**
     * 加载更多
     */
    fun loadMore() {
        if (_isLoadingMore.value) return  // 防止重复加载

        viewModelScope.launch {
            _isLoadingMore.value = true

            try {
                val newPosts = getFeedUseCase(count = 20)
                val postsWithLocalState = enrichPostsWithLocalState(newPosts)

                // 防止追加重复数据（API可能每次返回相同内容）
                val existingPostIds = _posts.value.map { it.postId }.toSet()
                val uniqueNewPosts = postsWithLocalState.filter { it.postId !in existingPostIds }

                _posts.value += uniqueNewPosts
            } catch (e: Exception) {
                // 加载更多失败时忽略
            } finally {
                _isLoadingMore.value = false
            }
        }
    }

    // ==================== 交互操作 ====================

    /**
     * 切换点赞状态
     */
    fun toggleLike(postId: String) {
        val currentPosts = _posts.value
        val updatedPosts = currentPosts.map { post ->
            if (post.postId == postId) {
                val newLikedStatus = !post.isLiked
                // 保存到本地
                preferencesRepository.setLikeStatus(postId, newLikedStatus)
                // 更新模型
                post.copy(
                    isLiked = newLikedStatus,
                    likeCount = if (newLikedStatus) {
                        post.likeCount + 1
                    } else {
                        (post.likeCount - 1).coerceAtLeast(0)
                    }
                )
            } else {
                post
            }
        }
        _posts.value = updatedPosts
    }

    // ==================== 私有方法 ====================

    /**
     * 为Post列表填充本地状态（点赞、关注）
     */
    private fun enrichPostsWithLocalState(posts: List<Post>): List<Post> {
        return posts.map { post ->
            post.copy(
                isLiked = preferencesRepository.getLikeStatus(post.postId),
                author = post.author.copy(
                    isFollowed = preferencesRepository.getFollowStatus(post.author.userId)
                )
            )
        }
    }
}

/**
 * Feed UI状态
 */
sealed class FeedUiState {
    object Loading : FeedUiState()      // 首次加载中
    object Success : FeedUiState()      // 加载成功
    object Empty : FeedUiState()        // 无数据
    data class Error(val message: String) : FeedUiState()  // 加载失败
}