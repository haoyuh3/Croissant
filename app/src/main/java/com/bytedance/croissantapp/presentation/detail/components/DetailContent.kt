package com.bytedance.croissantapp.presentation.detail.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.bytedance.croissantapp.domain.model.Post
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.buildAnnotatedString
import com.bytedance.croissantapp.domain.model.Clip
import com.bytedance.croissantapp.domain.model.Hashtag
import coil.compose.SubcomposeAsyncImage
import com.bytedance.croissantapp.domain.model.ClipType
import com.bytedance.croissantapp.util.DateUtil

@Composable
fun DetailContent(
    post: Post,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // 横滑图片容器（已包含进度指示器）
        ImagePagerSection(clips = post.clips)

        Spacer(modifier = Modifier.height(16.dp))

        // 标题
        if (post.title.isNotEmpty()) {
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // 正文（带话题词高亮）
        HashtagText(
            content = post.content,
            hashtags = post.hashtags,
            onHashtagClick = { hashtag ->
                // TODO: 跳转到话题页
                println("点击话题: $hashtag")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 发布时间
        Text(
            text = DateUtil.formatPublishDate(post.createTime),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        // 底部留白，避免被BottomBar遮挡
        Spacer(modifier = Modifier.height(80.dp))
    }
}


@Composable
fun ImagePagerSection(
    clips: List<Clip>,
    modifier: Modifier = Modifier
) {
    if (clips.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { clips.size })

    // 计算容器宽高比（使用首图的比例）
    val firstClip = clips.first()
    val aspectRatio = firstClip.displayAspectRatio

    Column(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(aspectRatio)
        ) { page ->
            val clip = clips[page]

            when (clip.type) {
                ClipType.IMAGE -> {
                    // 图片展示
                    SubcomposeAsyncImage(
                        model = clip.url,
                        contentDescription = "作品图片 ${page + 1}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        loading = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        },
                        error = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("加载失败", color = Color.Gray)
                            }
                        }
                    )
                }
                ClipType.VIDEO -> {
                    // 视频展示（进阶功能）
                    VideoPlayerView(clip = clip)
                }
            }
        }

        // 进度指示器
        if (clips.size > 1) {
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (pagerState.currentPage + 1).toFloat() / clips.size },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
            )
        }
    }
}


@Composable
fun HashtagText(
    content: String,
    hashtags: List<Hashtag>,
    onHashtagClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // 构建带标注的字符串
    val annotatedString = buildAnnotatedString {
        var lastIndex = 0

        // 按照hashtag位置排序
        val sortedHashtags = hashtags.sortedBy { it.start }

        sortedHashtags.forEach { hashtag ->
            // 添加话题词之前的普通文本
            if (lastIndex < hashtag.start) {
                append(content.substring(lastIndex, hashtag.start))
            }

            // 添加话题词（高亮显示）
            val hashtagText = content.substring(hashtag.start, hashtag.end)
            pushStringAnnotation(
                tag = "HASHTAG",
                annotation = hashtagText
            )
            withStyle(
                style = SpanStyle(
                    color = Color(0xFF1E90FF),  // 蓝色
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(hashtagText)
            }
            pop()

            lastIndex = hashtag.end
        }

        // 添加最后的普通文本
        if (lastIndex < content.length) {
            append(content.substring(lastIndex))
        }
    }

    // 可点击的文本
    ClickableText(
        text = annotatedString,
        style = MaterialTheme.typography.bodyMedium.copy(
            color = Color.Black
        ),
        onClick = { offset ->
            // 检查点击位置是否在话题词上
            annotatedString.getStringAnnotations(
                tag = "HASHTAG",
                start = offset,
                end = offset
            ).firstOrNull()?.let { annotation ->
                onHashtagClick(annotation.item)
            }
        },
        modifier = modifier
    )
}

/**
 * 视频播放器占位组件（进阶功能）
 * TODO: 使用ExoPlayer实现视频播放
 */
@Composable
fun VideoPlayerView(
    clip: Clip,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "视频",
                modifier = Modifier.size(64.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "视频播放功能开发中",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}
