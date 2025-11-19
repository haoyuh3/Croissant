package com.bytedance.crossiantapp.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bytedance.crossiantapp.presentation.home.components.HomeTabRow
import org.jetbrains.annotations.ApiStatus

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
) {
    // 当前选中的Tab
    var selectedTab by remember { mutableStateOf(HomeTabItem.getDefault()) }

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
                selectedTab = tab
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
//                HomeTabItem.BEIJING -> DisabledTabContent("北京")
//                HomeTabItem.GROUP_BUY -> DisabledTabContent("团购")
//                HomeTabItem.FOLLOW -> DisabledTabContent("关注")
//                HomeTabItem.RECOMMEND -> DisabledTabContent("推荐")
                HomeTabItem.COMMUNITY -> CommunityTabContent(onNavigateToDetail)
                else -> DisabledTabContent("暂未开发")
            }
        }
    }
}

/**
 * 社区Tab内容（默认Tab）
 * @param onNavigateToDetail 跳转到详情页的回调
 * @param modifier 修饰符
 */
@Composable
private fun CommunityTabContent(
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // TODO: 这里将来会显示社区内容列表
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "社区内容",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "这里将显示社区动态列表",
            fontSize = 14.sp,
            color = Color.Gray,
        )

        Spacer(modifier = Modifier.height(24.dp))

        // @note：跳转到详情页的按钮
        Button(
            onClick = { onNavigateToDetail("test_post_id") },
        ) {
            Text("跳转到详情页示例")
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
