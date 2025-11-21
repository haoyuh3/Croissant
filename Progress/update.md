# CrossiantApp - 技术实现指南

## 📋 项目概述

这是一个基于字节跳动训练营课题的**图文客户端APP**，模仿可颂APP的核心功能，提供UGC内容社区体验。

### 业务背景
- **产品形态**：双列图文视频混排，以图文体裁为主
---

## 🏗️ 技术架构

### 架构模式
采用 **MVVM** 架构模式：

```
app/src/main/java/com/bytedance/crossiantapp/
├── data/              # 数据层
│ 
│ 
├── domain/           # 业务逻辑层
│   
├── presentation/     # 表现层
│   ├── home/         # 首页
│   ├── detail/       # 详情页
│   ├── profile/      # 个人主页
│   ├── navigation/   # 导航
│   └── components/   # 通用组件
│ 
└── util/             # 工具类
```
![img.png](E:\code_project\kotlinProject\Croissant\Progress\img.png)
---

## 🎯 功能模块详解

### 1. 应用框架

#### 1.1 底部导航栏
- **Tab列表**（从左到右）：首页、朋友、相机(+)、消息、我
- **实现要求**：
  - 只需支持"首页"和"我"的点击
  - 启动后默认在"首页"
  - 使用 `NavigationBarItem`

```kotlin
// presentation/components/BottomNavigationBar.kt
// testUI/NavigationBarTest
// 实现BottomNavItem
enum class BottomNavItem {
    HOME,      // 首页 
    FRIENDS,   // 朋友 
    CAMERA,    // 相机 
    MESSAGE,   // 消息 
    PROFILE    // 我 
}
```
- NavigationBar 遍历 BottomNavItem
- 使用NavigationBarItem库 配置图标文字，点击事件，颜色

#### 1.2 首页Tab
- **Tab列表**（从左到右）：北京、团购、关注、社区、推荐
- **实现要求**：
  - 默认位于"社区"
  - 其他Tab无需支持点击
- Tab Row + 内容区域
- 实现HomeTabRow

#### 1.3 个人首页
- **个人** 头像，关注，粉丝，喜欢
- **实现要求**：
    - 使用composable函数构建UI
    - **Column** 是一个垂直方向的线性布局 包括(ICON + TXT + ROW)
    - **Row** 是一个水平方向的线性布局 包括(关注，粉丝，喜欢)

#### 1.4 导航系统配置

- 理解Navigation Compose
**Navigation Compose** 是Jetpack Compose的导航库：
- 使用rememberNavController() 保持navigator controller
- 实现NavGraph (导航页面)
- 实现NavHost (页面容器)
- composable(route = Routes.HOME)/ Routes.Profile 配置导航路由

#### 问题
- Composable 函数使用 / XML文件配置UI
- Navigation 页面跳转 / Activity Result API 注册/ BUNDLE / 序列化传递
- API 调用和图片加载问题 和 本地存储的选型问题 (SharePreference SQLite )
- 调用数据流？
- 用户下拉 -> viewModel-> Retrofit: 构建Request OkHttp: 发送HTTP GET请求
- 是否有良好的内存管理、性能优化意识（如图片缓存、
  LoadMore优化） 本地存储、异步加载、状态管理实现是否符合规范
- 列表滑动是否卡顿 图片展示黑屏比例是否较低 页面转场、图片滑动是否流畅自然/ 用户体验优化测试
---
#### 双列瀑布流 > 详情页 > 进阶功能

---

## 🎨 详情页实现

### 架构概览

```
presentation/detail/
├── DetailScreen.kt              # 主页面，整合各组件
├── DetailViewModel.kt           # 状态管理，处理业务逻辑
└── components/
    ├── DetailTopBar.kt          # 顶部栏（作者信息+关注）
    ├── DetailContent.kt         # 内容区（图片轮播+标题+正文）
    └── DetailBottomBar.kt       # 底部栏（评论框+交互按钮）
```

### 1. DetailViewModel - 状态管理

采用 **sealed class** 定义三种UI状态：

```kotlin
sealed class DetailUiState {
    object Loading : DetailUiState()                     // 加载中
    data class Success(val post: Post) : DetailUiState() // 加载成功
    data class Error(val message: String) : DetailUiState() // 加载失败
}
```

核心功能：
- `loadPostDetail(postId)` - 加载作品详情，填充本地点赞/关注状态
- `toggleLike()` - 切换点赞，更新本地存储和UI
- `toggleFollow()` - 切换关注，更新本地存储和UI

### 2. DetailScreen - 主页面

使用 `Scaffold` 组织布局：
- `topBar` → DetailTopBar
- `bottomBar` → DetailBottomBar
- `content` → 根据状态显示 Loading/Success/Error

```kotlin
LaunchedEffect(postId) {
    viewModel.loadPostDetail(postId)
}

val uiState by viewModel.uiState.collectAsState()

Scaffold(
    topBar = { DetailTopBar(...) },
    bottomBar = { DetailBottomBar(...) }
) { paddingValues ->
    when (uiState) {
        is Loading -> CircularProgressIndicator()
        is Success -> DetailContent(post)
        is Error -> 错误提示 + 重试按钮
    }
}
```

### 3. DetailTopBar - 顶部栏

包含：返回按钮 | 作者头像 | 作者昵称 | 关注按钮

```kotlin
TopAppBar(
    navigationIcon = { IconButton(返回) },
    title = {
        Row {
            SubcomposeAsyncImage(作者头像)
            Text(作者昵称)
            Button(关注/已关注)
        }
    }
)
```

### 4. DetailContent - 内容区

**ImagePagerSection** - 横滑图片轮播：
- 使用 `HorizontalPager` 实现横滑
- 根据首图计算 `aspectRatio` 保持比例
- 支持图片/视频两种类型 (`ClipType`)
- 多图时底部显示 `LinearProgressIndicator`

**HashtagText** - 话题词高亮：
- 使用 `AnnotatedString` 标记话题位置
- `ClickableText` 实现点击跳转
- 话题词蓝色高亮显示

```kotlin
Column(verticalScroll) {
    ImagePagerSection(clips)      // 图片轮播
    Text(title)                   // 标题
    HashtagText(content, hashtags) // 正文（话题高亮）
    Text(发布时间)
}
```

### 5. DetailBottomBar - 底部栏

```kotlin
Row {
    OutlinedTextField(评论框占位)
    IconButton(点赞) { 红心图标 + 数量 }
    IconButton(评论)
    IconButton(收藏)
    IconButton(分享)
}
```

点赞数格式化：`10000+ → "1.0w"`

### 6. 本地状态持久化

通过 `UserPreferencesRepository` 保存：
- 点赞状态：`getLikeStatus(postId)` / `setLikeStatus()`
- 关注状态：`getFollowStatus(userId)` / `setFollowStatus()`

---

## 👤 个人资料编辑功能

### 架构概览

```
presentation/profile/
├── ProfileScreen.kt      # 个人主页UI（头像上传+编辑对话框）
└── ProfileViewModel.kt   # 状态管理

data/local/
└── UserPreferencesRepository.kt  # 新增用户资料存储方法
```

### 1. 数据层 - UserPreferencesRepository

扩展 MMKV 存储，新增用户资料相关方法：

```kotlin
// 昵称
fun setUserNickname(nickname: String)
fun getUserNickname(): String

// 个人简介
fun setUserBio(bio: String)
fun getUserBio(): String

// 头像（存储本地Uri路径）
fun setUserAvatar(uri: String)
fun getUserAvatar(): String?
```

### 2. ProfileViewModel - 状态管理

```kotlin
data class ProfileUiState(
    val nickname: String = "用户昵称",
    val bio: String = "这里是个人简介",
    val avatarUri: String? = null
)

class ProfileViewModel {
    val uiState: StateFlow<ProfileUiState>

    fun updateNickname(nickname: String)  // 更新昵称
    fun updateBio(bio: String)            // 更新简介
    fun updateAvatar(uri: String)         // 更新头像
}
```

### 3. ProfileScreen - UI实现

**头像上传**：使用 `ActivityResultContracts.GetContent` 打开相册

```kotlin
val imagePickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
) { uri: Uri? ->
    uri?.let { viewModel.updateAvatar(it.toString()) }
}

// 点击头像触发选择
Box(modifier = Modifier.clickable {
    imagePickerLauncher.launch("image/*")
}) {
    SubcomposeAsyncImage(model = uiState.avatarUri, ...)
}
```

**编辑资料对话框**：

```kotlin
EditProfileDialog(
    currentNickname = uiState.nickname,
    currentBio = uiState.bio,
    onSave = { nickname, bio ->
        viewModel.updateNickname(nickname)
        viewModel.updateBio(bio)
    }
)
```

### 4. 功能说明

| 功能 | 实现方式 |
|------|---------|
| 头像上传 | 点击头像 → GetContent 选择图片 → 保存Uri到MMKV |
| 昵称编辑 | 弹出Dialog → OutlinedTextField → 保存到MMKV |
| 简介编辑 | 弹出Dialog → OutlinedTextField → 保存到MMKV |
| 数据持久化 | MMKV 本地存储，应用重启后保留 |

---

## 📦 交付物清单

1. ✅ **源码Git地址**
2. ✅ **APK安装包**
3. ✅ **演示录屏**
4. ✅ **技术方案文档**
   - 实现方案/框架介绍
   - 完成功能列表
   - 个人思考

---

## 资源链接

- Figma设计稿：https://www.figma.com/design/REGsGFaHnc7rjyvHfkeOy4/...
- API接口：https://college-training-camp.bytedance.com/feed/
- Jetpack Compose文档：https://developer.android.com/jetpack/compose
- Fresco文档：https://frescolib.org/
- ExoPlayer文档：https://exoplayer.dev/