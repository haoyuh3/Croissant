package com.bytedance.croissantapp.presentation.detail.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bytedance.croissantapp.domain.model.Post

@Composable
fun DetailBottomBar(
    post: Post,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 快捷评论框
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("说点什么...", style = MaterialTheme.typography.bodySmall) },
                modifier = Modifier.weight(1f),
                enabled = false,  // 暂不支持评论功能
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = Color.LightGray,
                    disabledPlaceholderColor = Color.Gray,
                    disabledTextColor = Color.Black
                ),
                shape = RoundedCornerShape(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // 点赞按钮
            IconButton(onClick = onLikeClick) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (post.isLiked) {
                            Icons.Default.Favorite
                        } else {
                            Icons.Default.FavoriteBorder
                        },
                        contentDescription = "点赞",
                        tint = if (post.isLiked) Color.Red else Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatCount(post.likeCount),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (post.isLiked) Color.Red else Color.Gray
                    )
                }
            }

            // 评论按钮（占位）
            IconButton(onClick = { /* TODO */ }) {
                Icon(
                    imageVector = Icons.Default.MailOutline,
                    contentDescription = "评论",
                    tint = Color.Gray
                )
            }

            // 收藏按钮（占位）
            IconButton(onClick = { /* TODO */ }) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "收藏",
                    tint = Color.Gray
                )
            }

            // 分享按钮
            IconButton(onClick = {
                // TODO: 实现分享功能
                println("分享作品: ${post.postId}")
            }) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "分享",
                    tint = Color.Gray
                )
            }
        }
    }
}

/**
 * 格式化数字显示（如点赞数）
 * 10000以上显示为"1.0w"
 */
fun formatCount(count: Int): String {
    return when {
        count >= 10000 -> String.format("%.1fw", count / 10000.0)
        else -> count.toString()
    }
}
