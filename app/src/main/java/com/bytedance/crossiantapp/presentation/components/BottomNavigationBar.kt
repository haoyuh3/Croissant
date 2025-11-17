package com.bytedance.crossiantapp.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector


import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Shared UI components will be defined here
 */
enum class BottomNavItem(
    val route: String,          // 路由路径
    val title: String,          // 显示标题
    val icon: ImageVector,      // 图标
    val isEnabled: Boolean = true  // 是否可点击
) {
    HOME(
        route = "home",
        title = "首页",
        icon = Icons.Default.Home,
        isEnabled = true
    ),
    FRIENDS(
        route = "friends",
        title = "朋友",
        icon = Icons.Default.Face,
        isEnabled = false  // 占位符，不可点击
    ),
    CAMERA(
        route = "camera",
        title = "",  // 相机按钮通常不显示文字
        icon = Icons.Default.AddCircle,
        isEnabled = false  // 占位符，不可点击
    ),
    MESSAGE(
        route = "message",
        title = "消息",
        icon = Icons.Default.MailOutline,
        isEnabled = false  // 占位符，不可点击
    ),
    PROFILE(
        route = "profile",
        title = "我",
        icon = Icons.Default.Person,
        isEnabled = true
    )
}


/**
 * 底部导航栏组件
 *
 * @param selectedItem 当前选中的Tab
 * @param onItemSelected 点击Tab时的回调函数
 * @param modifier 修饰符
 */
@Composable
fun BottomNavigationBar(
    selectedItem: BottomNavItem,
    onItemSelected: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = Color.White,  // 背景色
        tonalElevation = 8.dp  // 阴影高度
    ) {
        // 遍历所有的底部导航项
        BottomNavItem.entries.forEach { item ->
            NavigationBarItem(
                // 是否选中
                selected = selectedItem == item,

                // 点击事件
                onClick = {
                    // 只有isEnabled=true的才能点击
                    if (item.isEnabled) {
                        onItemSelected(item)
                    }
                },

                // 图标
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        // 相机按钮可以设置大一点
                        modifier = if (item == BottomNavItem.CAMERA) {
                            Modifier.size(32.dp)
                        } else {
                            Modifier
                        }
                    )
                },

                // 标签文字
                label = {
                    if (item.title.isNotEmpty()) {
                        Text(text = item.title)
                    }
                },

                // 是否启用（控制是否可点击）
                enabled = item.isEnabled,

                // 颜色配置
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,  // 选中时图标颜色
                    unselectedIconColor = Color.Gray, // 未选中时图标颜色
                    selectedTextColor = Color.Black,  // 选中时文字颜色
                    unselectedTextColor = Color.Gray, // 未选中时文字颜色
                    indicatorColor = Color.Transparent // 选中指示器颜色（透明）
                )
            )
        }
    }
}