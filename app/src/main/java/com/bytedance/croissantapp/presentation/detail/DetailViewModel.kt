package com.bytedance.croissantapp.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.bytedance.croissantapp.domain.model.Post
import com.bytedance.croissantapp.domain.usecase.GetPostDetailUseCase
import com.bytedance.croissantapp.data.local.UserPreferencesRepository
import com.bytedance.croissantapp.data.local.dao.FollowedUserDao
import com.bytedance.croissantapp.data.local.entity.FollowedUserEntity

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
    private val preferencesRepository: UserPreferencesRepository,
    private val followedUserDao: FollowedUserDao
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
                    ),
                    likeCount = preferencesRepository.getLikeCount(postId),
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
            val newLikeCount = if (newLikedStatus) post.likeCount + 1 else (post.likeCount - 1).coerceAtLeast(0)
            // 更新本地存储
            preferencesRepository.setLikeStatus(post.postId, newLikedStatus)
            preferencesRepository.setLikeCount(post.postId, newLikeCount)


            // 更新UI状态
            val updatedPost = post.copy(
                isLiked = newLikedStatus,
                likeCount = newLikeCount
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

            // 在后台线程同步更新数据库
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    if (newFollowStatus) {
                        // 关注：插入数据库
                        val followedUser = FollowedUserEntity(
                            userId = author.userId,
                            nickname = author.nickname,
                            avatar = author.avatar,
                            bio = "",
                            followedTime = System.currentTimeMillis()
                        )
                        followedUserDao.insertFollowedUser(followedUser)
                    } else {
                        // 取消关注：从数据库删除
                        followedUserDao.deleteFollowedUserById(author.userId)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // 更新UI状态
            val updatedPost = post.copy(
                author = author.copy(isFollowed = newFollowStatus)
            )
            _uiState.value = DetailUiState.Success(updatedPost)
        }
    }
}