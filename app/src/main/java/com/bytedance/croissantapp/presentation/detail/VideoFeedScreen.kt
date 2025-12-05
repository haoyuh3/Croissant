package com.bytedance.croissantapp.presentation.detail

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bytedance.croissantapp.domain.model.Post
import com.bytedance.croissantapp.presentation.detail.components.DetailTopBar
import com.bytedance.croissantapp.presentation.detail.components.DetailBottomBar
import com.bytedance.croissantapp.presentation.detail.components.DetailContent

/**
 * 垂直滑动视频流页面
 * 支持上下滑动切换视频，类似抖音/快手
 *
 * @param postList 视频列表
 * @param initialIndex 初始显示的视频索引
 * @param sharedTransitionScope 共享转场作用域
 * @param animatedVisibilityScope 动画可见性作用域
 * @param onNavigateBack 返回回调
 * @param onHashtagClick 话题点击回调
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun VideoFeedScreen(
    postList: List<Post>,
    initialIndex: Int = 0,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onNavigateBack: () -> Unit,
    onHashtagClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = hiltViewModel()
) {
    // VerticalPager 状态
    val pagerState = rememberPagerState(
        initialPage = initialIndex,
        pageCount = { postList.size }
    )

    // 当前显示的视频
    val currentPost = postList.getOrNull(pagerState.currentPage)

    // 监听页面变化，加载当前视频数据
    LaunchedEffect(pagerState.currentPage) {
        currentPost?.let { post ->
            viewModel.setInitialPost(post)
        }
    }

    // 观察UI状态
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // 垂直分页器
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            val post = postList[pageIndex]

            // 单个视频页面
            VideoPageContent(
                post = post,
                isCurrentPage = pageIndex == pagerState.currentPage,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                onHashtagClick = onHashtagClick,
                viewModel = viewModel,
                uiState = uiState
            )
        }

        // 顶部导航栏（浮动在视频上方）
        currentPost?.let { post ->
            DetailTopBar(
                post = post,
                onNavigateBack = onNavigateBack,
                onFollowClick = { viewModel.toggleFollow() },
                modifier = Modifier.align(Alignment.TopStart)
            )
        }

        // 底部操作栏（浮动在视频上方）
        currentPost?.let { post ->
            DetailBottomBar(
                post = post,
                onLikeClick = { viewModel.toggleLike() },
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }

        // 页面指示器（可选）
        if (postList.size > 1) {
            Text(
                text = "${pagerState.currentPage + 1} / ${postList.size}",
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp, 60.dp, 16.dp, 16.dp)
            )
        }
    }
}

/**
 * 单个视频页面内容
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun VideoPageContent(
    post: Post,
    isCurrentPage: Boolean,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onHashtagClick: (String) -> Unit,
    viewModel: DetailViewModel,
    uiState: DetailUiState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        when (uiState) {
            is DetailUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is DetailUiState.Success -> {
                DetailContent(
                    post = post,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    onHashtagClick = onHashtagClick,
                    modifier = Modifier.fillMaxSize()
                )
            }
            is DetailUiState.Error -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("加载失败: ${uiState.message}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        viewModel.setInitialPost(post)
                    }) {
                        Text("重试")
                    }
                }
            }
        }
    }
}