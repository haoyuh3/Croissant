package com.bytedance.croissantapp.presentation.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bytedance.croissantapp.domain.model.ClipType
import com.bytedance.croissantapp.domain.model.Post
import com.bytedance.croissantapp.presentation.components.VideoPlayer
import androidx.compose.foundation.background

/**
 * 作品卡片组件
 *
 * @param post 作品数据
 * @param onClick 点击卡片回调
 * @param onLikeClick 点击点赞按钮回调
 * @param modifier 修饰符
 */
@Composable
fun PostCard(
    post: Post,
    onClick: () -> Unit,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 1. 封面图片/视频（如果有）
            val coverClip = post.clips.firstOrNull()

            if (coverClip != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(coverClip.displayAspectRatio)
                ) {
                    when (coverClip.type) {
                        ClipType.IMAGE -> {
                            // 原有的图片加载逻辑
                            AsyncImage(
                                model = coverClip.url,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        ClipType.VIDEO -> {
                            // 新增：视频封面 + 播放按钮
                            println("cover clip type: ${coverClip.type}")
                            println("cover clip title: ${coverClip.url}")
                            VideoPlayer(
                                videoUrl = coverClip.url,
                                autoPlay = false,  // 首页不自动播放
                                showControls = false,  // 不显示控制条
                                modifier = Modifier.fillMaxSize()
                            )

                            // 播放图标遮罩
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "播放视频",
                                    tint = Color.White,
                                    modifier = Modifier.size(64.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 2. 标题/内容区域
            // 优先使用标题
            val displayText = post.title.ifEmpty { post.content }
            if (displayText.isNotEmpty()) {
                Text(
                    text = displayText,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = if (coverClip == null) 4 else 2,  // 纯文字帖子显示更多行
                    overflow = TextOverflow.Ellipsis, // 使用..截断
                    color = Color.Black,
                    modifier = Modifier.padding(12.dp, 12.dp, 12.dp, 0.dp)
                )
            }

            // 3. 底栏：作者信息 + 点赞按钮
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp, 8.dp, 12.dp, 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                    // 作者信息
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        // 头像
                        AsyncImage(
                            model = post.author.avatar,
                            contentDescription = "作者头像",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // 昵称
                        Text(
                            text = post.author.nickname,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // 点赞按钮
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable(onClick = onLikeClick)
                    ) {
                        Icon(
                            imageVector = if (post.isLiked) {
                                Icons.Filled.Favorite
                            } else {
                                Icons.Outlined.FavoriteBorder
                            },
                            contentDescription = if (post.isLiked) "已点赞" else "点赞",
                            tint = if (post.isLiked) Color.Red else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = formatCount(post.likeCount),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
            }
        }
    }

}

/**
 * 格式化数量显示
 * 1000+ → 1k
 * 10000+ → 1w
 */
private fun formatCount(count: Int): String {
    return when {
        count >= 10000 -> "${count / 10000}w"
        count >= 1000 -> "${count / 1000}k"
        else -> count.toString()
    }
}