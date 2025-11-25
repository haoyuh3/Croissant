package com.bytedance.croissantapp.presentation.components

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

/**
 * 视频播放器组件
 *
 * @param videoUrl 视频 URL
 * @param modifier 修饰符
 * @param autoPlay 是否自动播放
 * @param showControls 是否显示控制条
 * @param onPlaybackStateChanged 播放状态改变回调
 */
@Composable
fun VideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true,
    showControls: Boolean = true,
    onPlaybackStateChanged: ((isPlaying: Boolean) -> Unit)? = null
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }

    // 创建 ExoPlayer 实例
    val exoPlayer = remember(videoUrl) {
        ExoPlayer.Builder(context).build().apply {
            // 设置媒体源
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)

            // 播放器配置
            playWhenReady = autoPlay
            repeatMode = Player.REPEAT_MODE_OFF

            // 添加状态监听器
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    isLoading = playbackState == Player.STATE_BUFFERING
                    hasError = playbackState == Player.STATE_IDLE
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    onPlaybackStateChanged?.invoke(isPlaying)
                }
            })

            // 准备播放
            prepare()
        }
    }

    // 生命周期管理
    DisposableEffect(videoUrl) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(modifier = modifier) {
        // 视频播放器视图
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = showControls
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // 加载指示器
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }

        // 错误提示
        if (hasError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.Text(
                    text = "视频加载失败",
                    color = Color.White
                )
            }
        }
    }
}