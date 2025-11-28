package com.bytedance.croissantapp.test

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bytedance.croissantapp.presentation.components.VideoPlayer

@Composable
fun VideoScreen() {
    val videoUrl = "https://lf3-static.bytednsdoc.com/obj/eden-cn/219eh7pbyphrnuvk/college_training_camp/item_videos/item_video139.mp4"

    VideoPlayer(
        videoUrl = videoUrl,
        modifier = Modifier.fillMaxSize() // 播放器铺满整个屏幕
    )
}

@Preview
@Composable
fun VideoScreenPreview() {
    VideoScreen()
}
