package com.bytedance.croissantapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bytedance.croissantapp.presentation.home.HomeScreen
import com.bytedance.croissantapp.presentation.profile.ProfileScreen

/**
 * 导航路由常量
 */
object Routes {
    const val HOME = "home"
    const val PROFILE = "profile"
}

/**
 * 应用的导航图
 * 用于导航到不同的内容
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
                onNavigateToDetail = {},
            )
        }

        // 个人主页
        composable(route = Routes.PROFILE) {
            ProfileScreen()
        }

    }
}
