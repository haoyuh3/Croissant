package com.bytedance.croissantapp.testUI

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bytedance.croissantapp.presentation.home.CommunityTabContent
import com.bytedance.croissantapp.presentation.home.DisabledTabContent
import com.bytedance.croissantapp.presentation.home.HomeTabItem
import com.bytedance.croissantapp.presentation.home.components.HomeTabRow

@Preview(name = "Home Screen Preview", showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreenTest(
        onNavigateToDetail = { postId ->
            Log.d("HomeScreenPreview", "Navigate to detail with postId: $postId")
        },
    )
}

@Composable
fun HomeScreenTest(
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
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