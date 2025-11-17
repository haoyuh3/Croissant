package com.bytedance.crossiantapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bytedance.crossiantapp.ui.theme.CrossiantAppTheme
import androidx.compose.runtime.*
import com.bytedance.crossiantapp.presentation.components.BottomNavItem
import com.bytedance.crossiantapp.presentation.components.BottomNavigationBar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CrossiantAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CrossiantAppTheme {
        Greeting("Android")
    }
}

@Composable
fun MainScreen() {
    // 使用remember保存选中的Tab状态
    var selectedTab by remember { mutableStateOf(BottomNavItem.HOME) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = selectedTab,
                onItemSelected = { item ->
                    selectedTab = item
                }
            )
        }
    ) { paddingValues ->
        // 根据选中的Tab显示不同的内容
        when (selectedTab) {
//            BottomNavItem.HOME -> HomeScreen(
//                modifier = Modifier.padding(paddingValues)
//            )
//            BottomNavItem.PROFILE -> ProfileScreen(
//                modifier = Modifier.padding(paddingValues)
//            )
            else -> {
                // 其他Tab暂时不实现
            }
        }
    }
}