package com.bytedance.croissantapp.testUI

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.bytedance.croissantapp.presentation.components.BottomNavItem
import com.bytedance.croissantapp.presentation.components.BottomNavigationBar

@Preview(name = "Navigation Bar", showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    var selectedBottomTab by remember { mutableStateOf(BottomNavItem.HOME) }

    Log.d("BottomNavigationBarPreview", "Recomposing with selected tab: ${selectedBottomTab.title}")

    BottomNavigationBar(
        // 2. 将状态变量传递给子组件，UI更新
        selectedItem = selectedBottomTab,
        onItemSelected = { item ->
            // 3. 在回调中更新状态，触发重组
            selectedBottomTab = item
        }
    )

}