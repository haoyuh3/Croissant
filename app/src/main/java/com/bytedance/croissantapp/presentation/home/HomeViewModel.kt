package com.bytedance.croissantapp.presentation.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.bytedance.croissantapp.data.local.UserPreferencesRepository
import com.bytedance.croissantapp.domain.model.Post
import com.bytedance.croissantapp.domain.usecase.GetFeedUseCase
import com.bytedance.croissantapp.domain.usecase.GetFeedUseCaseCache
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Feed UI状态
 */
sealed class FeedUiState {
    object InitLoading : FeedUiState()      // 首次加载中
    object Success : FeedUiState()      // 加载成功
    object Empty : FeedUiState()        // 无数据
    data class Error(val message: String) : FeedUiState()  // 加载失败
}

/**
 * 首页ViewModel
 * @param getFeedUseCase 获取Feed列表UseCase
 * @param preferencesRepository 用户偏好设置仓库
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getFeedUseCase: GetFeedUseCase,
    private val preferencesRepository: UserPreferencesRepository,
    private val getFeedUseCaseCache: GetFeedUseCaseCache
) : ViewModel() {

    // ==================== UI状态 ====================

    // 可变
    private val _uiState = MutableStateFlow<FeedUiState>(FeedUiState.InitLoading)
    // 外部只读
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    // 错误消息（用于显示Snackbar）
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // 初始化
    init {
        loadFeed()
    }

    // ==================== 加载Feed ====================

    /**
     * 初次加载Feed
     */
    fun loadFeed() {
        viewModelScope.launch {
            _uiState.value = FeedUiState.InitLoading
            println("HomeViewModel: 开始加载Feed...")

            val result = getFeedUseCase.invokeWithResult(count = 20)
            result.fold(
                onSuccess = { posts ->
                    println("HomeViewModel: 从API获取到 ${posts.size} 条数据")
                    val postsWithLocalState = fillWithLocalState(posts)
                    _posts.value = postsWithLocalState
                    _uiState.value = if (postsWithLocalState.isEmpty()) {
                        println("HomeViewModel: API返回空数据，显示Empty状态")
                        FeedUiState.Empty
                    } else {
                        println("HomeViewModel: 加载成功，共 ${postsWithLocalState.size} 条")
                        FeedUiState.Success
                    }
                },
                onFailure = { error ->
                    println("HomeViewModel: 网络加载失败 - ${error.message}，尝试从缓存加载")
                    // 网络失败时尝试从缓存加载
                    val cachedPosts = getFeedUseCaseCache(count = 20)
                    println("HomeViewModel: 从缓存获取到 ${cachedPosts.size} 条数据")

                    if (cachedPosts.isNotEmpty()) {
                        val postsWithLocalState = fillWithLocalState(cachedPosts)
                        _posts.value = postsWithLocalState
                        _uiState.value = FeedUiState.Success
                        println("HomeViewModel: 使用缓存数据，共 ${postsWithLocalState.size} 条")
                    } else {
                        _posts.value = emptyList()
                        _uiState.value = FeedUiState.Error("网络连接失败，且无缓存数据")
                        println("HomeViewModel: 网络失败且无缓存数据")
                    }
                }
            )
        }
    }

    /**
     * 下拉刷新
     */
    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true

            val result = getFeedUseCase.invokeWithResult(count = 20)
            result.fold(
                onSuccess = { posts ->
                    println("HomeViewModel: 刷新成功，获取到 ${posts.size} 条数据")
                    val postsWithLocalState = fillWithLocalState(posts)
                    _posts.value = postsWithLocalState
                    _uiState.value = if (postsWithLocalState.isEmpty()) {
                        FeedUiState.Empty
                    } else {
                        FeedUiState.Success
                    }
                },
                onFailure = { error ->
                    println("HomeViewModel: 刷新失败 - ${error.message}")
                    // 网络错误时提示用户，保持当前数据不变
                    _errorMessage.value = "网络连接失败，请检查网络设置"
                }
            )

            _isRefreshing.value = false
        }
    }

    /**
     * 加载更多
     */
    fun loadMore() {
        if (_isLoadingMore.value) return  // 防止重复加载

        viewModelScope.launch {
            _isLoadingMore.value = true

            val result = getFeedUseCase.invokeWithResult(count = 20)
            result.fold(
                onSuccess = { newPosts ->
                    println("HomeViewModel: 加载更多成功，获取到 ${newPosts.size} 条数据")
                    val postsWithLocalState = fillWithLocalState(newPosts)

                    // 防止追加重复数据（API可能每次返回相同内容）
                    val existingPostIds = _posts.value.map { it.postId }.toSet()
                    val uniqueNewPosts = postsWithLocalState.filter { it.postId !in existingPostIds }

                    _posts.value += uniqueNewPosts
                },
                onFailure = { error ->
                    println("HomeViewModel: 加载更多失败 - ${error.message}")
                    // 网络错误时提示用户
                    _errorMessage.value = "网络连接失败，请检查网络设置"
                }
            )

            _isLoadingMore.value = false
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
                val newLikeCount = if (newLikedStatus) post.likeCount + 1 else (post.likeCount - 1).coerceAtLeast(0)

                // 保存到本地
                preferencesRepository.setLikeStatus(postId, newLikedStatus)
                preferencesRepository.setLikeCount(post.postId, newLikeCount)
                // 更新模型
                post.copy(
                    isLiked = newLikedStatus,
                    likeCount = newLikeCount
                )
            } else {
                post
            }
        }
        _posts.value = updatedPosts
    }

    // ==================== 公共方法 ====================

    /**
     * 刷新本地状态（从SharedPreferences重新读取点赞、关注状态）
     * 用于从详情页返回时同步状态
     */
    fun refreshLocalState() {
        _posts.value = fillWithLocalState(_posts.value)
    }

    /**
     * 清除错误消息（Snackbar显示后调用）
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    // ==================== 私有方法 ====================

    /**
     * 为Post列表填充本地状态（点赞、关注）
     */
    private fun fillWithLocalState(posts: List<Post>): List<Post> {
        return posts.map { post ->
            post.copy(
                isLiked = preferencesRepository.getLikeStatus(post.postId),
                author = post.author.copy(
                    isFollowed = preferencesRepository.getFollowStatus(post.author.userId)
                ),
                likeCount = preferencesRepository.getLikeCount(post.postId)
            )
        }
    }
}