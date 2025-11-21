package com.bytedance.croissantapp.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.bytedance.croissantapp.domain.model.Post
import com.bytedance.croissantapp.domain.usecase.GetPostDetailUseCase
import com.bytedance.croissantapp.data.local.UserPreferencesRepository

/**
 * 详情页UI状态
 */
sealed class DetailUiState {
    object Loading : DetailUiState()                    // 加载中
    data class Success(val post: Post) : DetailUiState()  // 加载成功
    data class Error(val message: String) : DetailUiState()  // 加载失败
}

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getPostDetailUseCase: GetPostDetailUseCase,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    // UI状态
    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    /**
     * 加载作品详情
     */
    fun loadPostDetail(postId: String) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading

            try {
                // 从UseCase获取作品数据
                val post = getPostDetailUseCase(postId)

                // 填充本地状态（点赞、关注）
                val enrichedPost = post.copy(
                    isLiked = preferencesRepository.getLikeStatus(postId),
                    author = post.author.copy(
                        isFollowed = preferencesRepository.getFollowStatus(post.author.userId)
                    )
                )

                _uiState.value = DetailUiState.Success(enrichedPost)
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(e.message ?: "加载失败")
            }
        }
    }

    /**
     * 切换点赞状态
     */
    fun toggleLike() {
        val currentState = _uiState.value
        if (currentState is DetailUiState.Success) {
            val post = currentState.post
            val newLikedStatus = !post.isLiked

            // 更新本地存储
            preferencesRepository.setLikeStatus(post.postId, newLikedStatus)

            // 更新UI状态
            val updatedPost = post.copy(
                isLiked = newLikedStatus,
                likeCount = if (newLikedStatus) {
                    post.likeCount + 1
                } else {
                    (post.likeCount - 1).coerceAtLeast(0)
                }
            )
            _uiState.value = DetailUiState.Success(updatedPost)
        }
    }

    /**
     * 切换关注状态
     */
    fun toggleFollow() {
        val currentState = _uiState.value
        if (currentState is DetailUiState.Success) {
            val post = currentState.post
            val author = post.author
            val newFollowStatus = !author.isFollowed

            // 更新本地存储
            preferencesRepository.setFollowStatus(author.userId, newFollowStatus)

            // 更新UI状态
            val updatedPost = post.copy(
                author = author.copy(isFollowed = newFollowStatus)
            )
            _uiState.value = DetailUiState.Success(updatedPost)
        }
    }
}