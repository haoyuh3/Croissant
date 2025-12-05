package com.bytedance.croissantapp.presentation.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bytedance.croissantapp.domain.model.Post
import com.bytedance.croissantapp.presentation.detail.DetailScreen
import com.bytedance.croissantapp.presentation.detail.VideoFeedScreen
import com.bytedance.croissantapp.presentation.hashtag.HashtagScreen
import com.bytedance.croissantapp.presentation.home.HomeScreen
import com.bytedance.croissantapp.presentation.profile.ProfileScreen

/**
 * 导航路由常量
 */
object Routes {
    const val HOME = "home"
    const val PROFILE = "profile"
    const val DETAIL = "detail/{postId}" // 详情页，带参数
    const val VIDEO_FEED = "video_feed/{postId}/{index}" // 视频流页面

    const val HASHTAG ="hashtag/{hashtag}"


    // 生成详情页路由的辅助函数
    fun detail(postId: String) = "detail/$postId"
    // 生成视频流路由的辅助函数
    fun videoFeed(postId: String, index: Int) = "video_feed/$postId/$index"
    // 话题页路由
    fun hashtag(hashtag: String) = "hashtag/$hashtag"
}

/**
 * 应用的导航图
 *
 * @param navController 导航控制器
 * @param startDestination 起始目的地
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = Routes.HOME,
) {
    // SharedTransitionLayout 共享专场容器
    // 共享元素动画使用默认值
    // 位置： 从卡片位置平滑移动到详情页位置
    // 大小： 从卡片尺寸平滑缩放到详情页尺寸
    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier,
        ) {
        // 首页
        composable(route = Routes.HOME) { backStackEntry ->
            // 监听从详情页返回的结果
            val shouldRefresh = backStackEntry
                .savedStateHandle
                .getStateFlow("refresh_from_detail", false)

            // 获取 HomeViewModel 的 posts 列表
            val homeViewModel: com.bytedance.croissantapp.presentation.home.HomeViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            val posts by homeViewModel.posts.collectAsState()

            HomeScreen(
                sharedTransitionScope = this@SharedTransitionLayout, // 共享容器
                animatedVisibilityScope = this@composable, // 动画可见性作用域
                viewModel = homeViewModel,
                onNavigateToDetail = { post ->
                    // 使用视频流页面（支持上下滑动）
//                    val clickedIndex = posts.indexOfFirst { it.postId == post.postId }
//                    navController.navigate(Routes.videoFeed(post.postId, clickedIndex.coerceAtLeast(0)))
//
//                    // 传递完整的视频列表
//                    navController.currentBackStackEntry
//                        ?.savedStateHandle
//                        ?.set("post_list", ArrayList(posts))

                    // 如果想用原来的单页详情，注释上面的代码，取消注释下面的：
                     navController.navigate(Routes.detail(post.postId))
                     navController.currentBackStackEntry
                         ?.savedStateHandle
                         ?.set("selected_post", post)
                },
                shouldRefresh = shouldRefresh
            )
        }

        // 个人主页
        composable(route = Routes.PROFILE) {
            ProfileScreen()
        }

        // 详情页
        composable(route = Routes.DETAIL) { backStackEntry ->
            // 获取路由参数
            val postId = backStackEntry.arguments?.getString("postId") ?: ""

            // 从当前页的 SavedStateHandle 获取传递的 Post 对象
            val post = backStackEntry.savedStateHandle.get<Post>("selected_post")

            DetailScreen(
                sharedTransitionScope = this@SharedTransitionLayout,
                animatedVisibilityScope = this@composable,
                postId = postId,
                initialPost = post,  // 传递 Post 对象
                onNavigateBack = {
                    // 返回前设置刷新信号（在动画开始前立即触发）
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("refresh_from_detail", true)

                    navController.navigateUp()
                },
                onHashtagClick = { hashtag ->
                    val encoded = java.net.URLEncoder.encode(hashtag, "UTF-8")
                    navController.navigate(Routes.hashtag(encoded))
                }

            )
        }

//        // 视频流页面（垂直滑动）
//        composable(route = Routes.VIDEO_FEED) { backStackEntry ->
//            // 获取路由参数
//            val postId = backStackEntry.arguments?.getString("postId") ?: ""
//            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
//
//            // 从 SavedStateHandle 获取视频列表
//            val postList = backStackEntry.savedStateHandle.get<ArrayList<Post>>("post_list")
//                ?: arrayListOf()
//
//            if (postList.isNotEmpty()) {
//                VideoFeedScreen(
//                    postList = postList,
//                    initialIndex = index,
//                    sharedTransitionScope = this@SharedTransitionLayout,
//                    animatedVisibilityScope = this@composable,
//                    onNavigateBack = {
//                        // 返回前设置刷新信号
//                        navController.previousBackStackEntry
//                            ?.savedStateHandle
//                            ?.set("refresh_from_detail", true)
//
//                        navController.navigateUp()
//                    },
//                    onHashtagClick = { hashtag ->
//                        val encoded = java.net.URLEncoder.encode(hashtag, "UTF-8")
//                        navController.navigate(Routes.hashtag(encoded))
//                    }
//                )
//            }
//        }

        composable(route = Routes.HASHTAG) { backStackEntry ->
            val hashtag = java.net.URLDecoder.decode(
                backStackEntry.arguments?.getString("hashtag") ?: "",
                "UTF-8")

            HashtagScreen(
                hashtag = hashtag,
                onNavigateBack = { navController.navigateUp() },
            )
        }

        }
    }
}
