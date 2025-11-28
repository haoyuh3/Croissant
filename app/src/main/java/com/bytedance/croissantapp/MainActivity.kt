package com.bytedance.croissantapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bytedance.croissantapp.presentation.components.BottomNavItem
import com.bytedance.croissantapp.presentation.components.BottomNavigationBar
import com.bytedance.croissantapp.presentation.navigation.NavGraph
import com.bytedance.croissantapp.presentation.navigation.Routes
import com.bytedance.croissantapp.ui.theme.CroissantAppTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * 应用主Activity
 * @AndroidEntryPoint 标记这是Hilt的注入点
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CroissantAppTheme {
                MainScreen()
            }
        }
    }
}

/**
 * 主屏幕
 * 包含底部导航和内容区
 */
@Composable
fun MainScreen() {
    // 创建导航控制器
    val navController = rememberNavController()

    // 监听当前路由
    val curBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = curBackStackEntry?.destination?.route

    // 更新当前选中的底部Tab
    var selectedBottomTab by remember { mutableStateOf(BottomNavItem.HOME) }

    // 判断是否应该显示底部导航栏（在详情页时隐藏）
    val showBottomBar = currentRoute?.startsWith("detail/") != true

    // Scaffold 搭建屏幕框架 底部放上BottomNavigationBar
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    selectedItem = selectedBottomTab,
                    onItemSelected = { item ->
                        // 更新选中的Tab
                        selectedBottomTab = item

                        // 导航到对应的页面
                        when (item) {
                            BottomNavItem.HOME -> {
                                navController.navigate(Routes.HOME) {
                                    // 避免返回栈堆积
                                    popUpTo(Routes.HOME) { inclusive = true }
                                }
                            }
                            BottomNavItem.PROFILE -> {
                                navController.navigate(Routes.PROFILE) {
                                    popUpTo(Routes.HOME)
                                }
                            }
                            else -> {
                                // 其他Tab暂不处理
                            }
                        }
                    },
                )
            }
        },
    ) { paddingValues ->
        // 导航填充paddingValues
        NavGraph(
            modifier = Modifier.padding(paddingValues),
            navController = navController,
            startDestination = Routes.HOME,
        )
    }
}
