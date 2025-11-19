package com.bytedance.crossiantapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bytedance.crossiantapp.presentation.components.BottomNavItem
import com.bytedance.crossiantapp.presentation.home.HomeScreen
import com.bytedance.crossiantapp.presentation.profile.ProfileScreen

/**
 * 导航路由常量
 */
object Routes {
    const val HOME = "home"
    const val PROFILE = "profile"
    const val DETAIL = "detail/{postId}" // 详情页，带参数

    // 生成详情页路由的辅助函数
    fun detail(postId: String) = "detail/$postId"
}

/**
 * 应用的导航图
 *
 * @param navController 导航控制器
 * @param startDestination 起始目的地
 */
@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = Routes.HOME,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        // 首页
        composable(route = Routes.HOME) {
            HomeScreen(
                onNavigateToDetail = { postId ->
                    // 跳转到详情页
                    navController.navigate(Routes.detail(postId))
                },
            )
        }

        // 个人主页
        composable(route = Routes.PROFILE) {
            ProfileScreen()
        }

        // 详情页（后续实现）
        composable(route = Routes.DETAIL) { backStackEntry ->
            // 获取路由参数
            val postId = backStackEntry.arguments?.getString("postId")

            // TODO: 实现详情页
            // DetailScreen(postId = postId ?: "")
        }
    }
}
