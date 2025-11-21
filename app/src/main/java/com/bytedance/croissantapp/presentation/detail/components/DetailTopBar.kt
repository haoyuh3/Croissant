package com.bytedance.croissantapp.presentation.detail.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.bytedance.croissantapp.domain.model.Post

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTopBar(
    post: Post,
    onNavigateBack: () -> Unit,
    onFollowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "返回"
                )
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // 作者头像
                SubcomposeAsyncImage(
                    model = post.author.avatar,
                    contentDescription = "作者头像",
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(8.dp))

                // 作者昵称
                Text(
                    text = post.author.nickname,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

                // 关注按钮
                Button(
                    onClick = onFollowClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (post.author.isFollowed) {
                            Color.LightGray
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    ),
                    modifier = Modifier
                        .height(32.dp)
                        .padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = if (post.author.isFollowed) "已关注" else "关注",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        ),
        modifier = modifier
    )
}