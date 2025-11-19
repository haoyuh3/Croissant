package com.bytedance.crossiantapp.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bytedance.crossiantapp.presentation.home.HomeTabItem

/**
 * 首页顶部Tab栏
 *
 * @param selectedTab 当前选中的Tab
 * @param onTabSelected 点击Tab的回调
 * @param modifier 修饰符
 */
@Composable
fun HomeTabRow(
    selectedTab: HomeTabItem,
    onTabSelected: (HomeTabItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween, // 两端对齐
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 左侧的Tab列表
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HomeTabItem.entries.forEach { tab ->
                HomeTabButton(
                    tab = tab,
                    isSelected = selectedTab == tab,
                    onClick = {
                        if (tab.isEnabled) {
                            onTabSelected(tab)
                        }
                    },
                )
            }
        }

        // 右侧的搜索图标
        IconButton(
            onClick = { /* 搜索功能暂不实现 */ },
            enabled = false,
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "搜索",
                tint = Color.Gray,
            )
        }
    }
}

/**
 * 单个Tab按钮
 */
@Composable
private fun HomeTabButton(
    tab: HomeTabItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Tab文字
        TextButton(
            onClick = onClick,
            enabled = tab.isEnabled,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        ) {
            Text(
                text = tab.title,
                style = MaterialTheme.typography.titleMedium,
                color = if (isSelected) Color.Black else Color.Gray,
                fontWeight =
                    if (isSelected) {
                        androidx.compose.ui.text.font.FontWeight.Bold
                    } else {
                        androidx.compose.ui.text.font.FontWeight.Normal
                    },
            )
        }

        // 选中时的下划线
        if (isSelected) {
            Box(
                modifier =
                    Modifier
                        .width(24.dp)
                        .height(3.dp)
                        .background(Color.Black),
            )
        } else {
            Spacer(modifier = Modifier.height(3.dp))
        }
    }
}
