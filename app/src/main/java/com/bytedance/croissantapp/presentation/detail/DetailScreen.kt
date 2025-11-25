package com.bytedance.croissantapp.presentation.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bytedance.croissantapp.presentation.detail.components.DetailTopBar
import com.bytedance.croissantapp.presentation.detail.components.DetailBottomBar
import com.bytedance.croissantapp.presentation.detail.components.DetailContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    postId: String,
    onNavigateBack: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel(),
) {
    // 加载作品详情
    LaunchedEffect(postId) {
        viewModel.loadPostDetail(postId)
    }

    // 观察UI状态
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            when (val state = uiState) {
                is DetailUiState.Success -> {
                    DetailTopBar(
                        post = state.post,
                        onNavigateBack = onNavigateBack,
                        onFollowClick = { viewModel.toggleFollow() }
                    )
                }
                else -> {
                    // 加载中或错误时显示简单的TopBar
                    TopAppBar(
                        title = { Text("作品详情") },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.Default.ArrowBack, "返回")
                            }
                        }
                    )
                }
            }
        },
        bottomBar = {
            if (uiState is DetailUiState.Success) {
                DetailBottomBar(
                    post = (uiState as DetailUiState.Success).post,
                    onLikeClick = { viewModel.toggleLike() }
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        // 内容区域
        when (val state = uiState) {
            is DetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is DetailUiState.Success -> {
                DetailContent(
                    post = state.post,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is DetailUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("加载失败: ${state.message}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadPostDetail(postId) }) {
                            Text("重试")
                        }
                    }
                }
            }
        }
    }
}