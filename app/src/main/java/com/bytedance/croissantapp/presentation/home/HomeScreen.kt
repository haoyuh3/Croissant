package com.bytedance.croissantapp.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bytedance.croissantapp.presentation.follow.FollowListLauncher
import com.bytedance.croissantapp.presentation.home.components.HomeTabRow
import com.bytedance.croissantapp.presentation.home.components.PostCard
import kotlinx.coroutines.flow.StateFlow

/**
 * 首页主界面
 *
 * @param onNavigateToDetail 跳转到详情页的回调 返回 postID 用于锁定详情页
 * @param modifier 修饰符
 * @param viewModel
 * @note viewModel:
 * 这是连接 View 和 Model 的桥梁。它从 Model (网络、数据库等) 获取数据。
 * 处理业务逻辑：对获取到的原始数据进行处理、计算或转换，变成 View 可以直接显示的格式。◦
 * 响应 View 的事件：当用户在 View 上进行操作时（例如下拉刷新、点击按钮），View 会通知 ViewModel，由 ViewModel 来决定下一步该做什么（比如重新请求网络）。◦
 * 持有并管理 UI 状态：它保存着界面的状态（比如列表数据、加载状态、错误信息），并且这些状态在屏幕旋转等配置变更后依然能够存活，不会丢失。
 */
@Composable
fun HomeScreen(
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    shouldRefresh: StateFlow<Boolean>? = null
) {
    // 当前选中的Tab
    var selectedTab by remember { mutableStateOf(HomeTabItem.getDefault()) }
    // 获取Context用于启动Activity
    val context = LocalContext.current

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color.White),
    ) {
        // 顶部Tab栏
        HomeTabRow(
            selectedTab = selectedTab,
            onTabSelected = { tab ->
                if (tab == HomeTabItem.FOLLOW && HomeTabItem.FOLLOW.isEnabled) {
                    FollowListLauncher.launch(context)
                } else {
                    selectedTab = tab
                }
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(Color.White),
        )

        // 分割线
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color.LightGray.copy(alpha = 0.3f),
        )

        // Tab内容区域
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .weight(1f),
        ) {
            when (selectedTab) {
                HomeTabItem.COMMUNITY -> CommunityTabContent(
                    onNavigateToDetail = onNavigateToDetail,
                    viewModel = viewModel,
                    shouldRefresh = shouldRefresh
                )
//                HomeTabItem.GROUP_BUY -> DisabledTabContent("团购")
//                HomeTabItem.RECOMMEND -> DisabledTabContent("推荐")
                else -> DisabledTabContent("暂未开发")
            }
        }
    }
}

/**
 * 社区Tab内容（默认Tab）- 双列瀑布流
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CommunityTabContent(
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    shouldRefresh: StateFlow<Boolean>? = null
) {
    // 订阅ViewModel状态 只读
    val uiState by viewModel.uiState.collectAsState()
    val posts by viewModel.posts.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Snackbar状态
    val snackbarHostState = remember { SnackbarHostState() }

    // 监听错误消息并显示Snackbar
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearErrorMessage()
        }
    }

    // 监听从详情页返回的刷新信号
    // 优化：点击返回后立即渲染 否则导航后渲染
    shouldRefresh?.let { flow ->
        val refresh by flow.collectAsState()
        LaunchedEffect(refresh) {
            if (refresh) {
                viewModel.refreshLocalState()
                // 重置信号，避免下次进入时重复刷新
            }
        }
    }

    // 下拉刷新状态：连接手势和U
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.refresh() }
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        when (uiState) {
            // 首次加载中
            is FeedUiState.InitLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // 加载成功 - 显示双列瀑布流
            is FeedUiState.Success -> {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),  // 固定2列
                    horizontalArrangement = Arrangement.spacedBy(8.dp),  // 列间距
                    verticalItemSpacing = 8.dp,  // 行间距
                    contentPadding = PaddingValues(16.dp),  // 内边距
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 作品卡片列表
                    items(
                        items = posts,
                        key = { it.postId }  // 使用postId作为唯一key
                    ) { post ->
                        PostCard(
                            post = post,
                            onClick = { onNavigateToDetail(post.postId) },
                            onLikeClick = { viewModel.toggleLike(post.postId) }
                        )
                    }

                    // 加载更多触发器
                    // 首屏加载成功
                    if (posts.isNotEmpty()) {
                        // 瀑布流中单独的列
                        item(span = StaggeredGridItemSpan.FullLine) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                // 正在加载中
                                if (isLoadingMore) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp
                                    )
                                }
                            }

                            // 触发加载更多
                            LaunchedEffect(Unit) {
                                viewModel.loadMore()
                            }
                        }
                    }
                }
            }

            // 空态 网络问题导致
            is FeedUiState.Empty -> {
                EmptyState(
                    onRetry = { viewModel.loadFeed() },
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // 错误态
            is FeedUiState.Error -> {
                ErrorState(
                    message = (uiState as FeedUiState.Error).message,
                    onRetry = { viewModel.loadFeed() },
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        // 下拉刷新指示器响应用户手势
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // Snackbar 提示
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/**
 * 空态页面
 */
@Composable
private fun EmptyState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "暂无内容",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onRetry) {
            Text("重新加载")
        }
    }
}

/**
 * 错误态页面
 */
@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "加载失败",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onRetry) {
            Text("重试")
        }
    }
}

/**
 * 未启用Tab的占位内容
 * @param tabName Tab名称
 * @param modifier 修饰符
 */
@Composable
private fun DisabledTabContent(
    tabName: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "$tabName 功能暂未开放",
            fontSize = 16.sp,
            color = Color.Gray,
        )
    }
}
