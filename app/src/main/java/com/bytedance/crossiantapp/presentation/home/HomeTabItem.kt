package com.bytedance.crossiantapp.presentation.home

/**
 * Home screen UI
 */
enum class HomeTabItem(
    val title: String,
    val isDefault: Boolean,
    val isEnabled: Boolean,
) {
    BEIJING("北京", isDefault = false, isEnabled = false),
    GROUP_BUY("团购", isDefault = false, isEnabled = false),
    FOLLOW("关注", isDefault = false, isEnabled = false),
    COMMUNITY("社区", isDefault = true, isEnabled = true), // 默认选中
    RECOMMEND("推荐", isDefault = false, isEnabled = false),
    ;

    companion object {
        fun getDefault() = entries.first { it.isDefault }
    }
}
