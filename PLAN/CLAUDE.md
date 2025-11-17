# CrossiantApp - 技术实现指南

## 📋 项目概述

这是一个基于字节跳动训练营课题的**图文客户端APP**，模仿可颂APP的核心功能，提供UGC内容社区体验。

### 业务背景
- **产品定位**：UGC内容社区，满足用户经验决策、圈层交流需求
- **产品形态**：双列图文视频混排，以图文体裁为主
- **核心优势**：
  - 双列布局：屏效比高，一屏展示多个作品，便于内容筛选
  - 图文体裁：信息密度高、可控感强、创作门槛低

---

## 🏗️ 技术架构

### 架构模式
采用 **Clean Architecture + MVVM** 架构模式：

```
app/src/main/java/com/bytedance/crossiantapp/
├── data/              # 数据层
│   ├── local/        # 本地存储 (MMKV)
│   ├── remote/       # 网络请求 (Retrofit)
│   ├── model/        # 数据模型 (DTO)
│   └── repository/   # Repository实现
├── domain/           # 业务逻辑层
│   ├── model/        # 领域模型 (Entity)
│   ├── repository/   # Repository接口
│   └── usecase/      # 用例
├── presentation/     # 表现层
│   ├── home/         # 首页（双列瀑布流）
│   ├── detail/       # 详情页
│   ├── profile/      # 个人主页
│   ├── navigation/   # 导航
│   └── components/   # 通用组件
├── di/               # 依赖注入 (Hilt)
└── util/             # 工具类
```

### 技术栈

| 类别 | 技术选型 | 用途 |
|------|---------|------|
| **UI框架** | Jetpack Compose | 声明式UI开发 |
| **架构组件** | ViewModel, LiveData/StateFlow | MVVM架构 |
| **依赖注入** | Hilt | 依赖管理 |
| **网络请求** | Retrofit + OkHttp | RESTful API |
| **JSON解析** | Gson | 数据序列化 |
| **图片加载** | Fresco | 图片缓存和显示 |
| **本地存储** | MMKV | 轻量级KV存储 |
| **视频播放** | ExoPlayer | 视频片段播放 |
| **音频播放** | MediaPlayer | 背景音乐 |
| **分页加载** | Paging 3 | 列表分页 |
| **路由** | ARouter | 页面导航 |
| **协程** | Kotlin Coroutines | 异步处理 |

---

## 🎯 功能模块详解

### 1. 应用框架

#### 1.1 底部导航栏
- **Tab列表**（从左到右）：首页、朋友、相机(+)、消息、我
- **实现要求**：
  - 只需支持"首页"和"我"的点击
  - 其他Tab可以是占位符
  - 启动后默认在"首页"
  - 使用 `NavigationBar` + `NavigationBarItem`

**参考代码结构**：
```kotlin
// presentation/components/BottomNavigationBar.kt
@Composable
fun BottomNavigationBar(
    selectedTab: BottomNavItem,
    onTabSelected: (BottomNavItem) -> Unit
)

enum class BottomNavItem {
    HOME,      // 首页 ✓
    FRIENDS,   // 朋友 (占位)
    CAMERA,    // 相机 (占位)
    MESSAGE,   // 消息 (占位)
    PROFILE    // 我 ✓
}
```

#### 1.2 首页Tab
- **Tab列表**（从左到右）：北京、团购、关注、社区、推荐、搜索Icon
- **实现要求**：
  - 默认位于"社区"
  - 其他Tab无需支持点击
  - 使用 `TabRow` + `Tab`

---

### 2. 双列瀑布流（首页）

#### 2.1 布局实现

**核心组件**：`LazyVerticalStaggeredGrid`

```kotlin
LazyVerticalStaggeredGrid(
    columns = StaggeredGridCells.Fixed(2),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalItemSpacing = 8.dp
) {
    items(posts) { post ->
        PostCard(post)
    }
}
```

#### 2.2 作品卡片（PostCard）

**必需元素**：
1. **作品封面**
   - 裁切规则：宽高比限制在 3:4 ~ 4:3
   - 使用Fresco的SimpleDraweeView或Coil

2. **作品标题**
   - 优先展示标题，无标题时展示正文
   - 最长2行，超出用"..."截断
   - 使用 `maxLines = 2`, `overflow = TextOverflow.Ellipsis`

3. **底栏**
   - 作者头像 + 昵称
   - 点赞Icon + 数量
   - 点赞支持状态切换，使用MMKV持久化存储

**卡片交互**：
- 点击卡片 → 跳转详情页
- 点击点赞 → 切换点赞状态（本地存储）

#### 2.3 列表功能

**下拉刷新**：
```kotlin
val pullRefreshState = rememberPullRefreshState(
    refreshing = isRefreshing,
    onRefresh = { viewModel.refresh() }
)
```

**上滑加载更多**：
- 使用Paging 3实现
- 监听滚动到底部，自动加载

**Loading状态**：
- 首刷Loading：全屏Loading指示器
- 加载更多Loading：底部小型加载器
- 刷新Loading：下拉刷新指示器

**空态页面**：
- 首刷请求失败时显示
- 提供重试按钮

---

### 3. 详情页

#### 3.1 页面结构

```
┌─────────────────────────┐
│ 顶部作者区（常驻）        │ ← Sticky Header
├─────────────────────────┤
│                         │
│   横滑容器（图片/视频）   │ ← HorizontalPager
│                         │
├─────────────────────────┤
│ 进度条                   │
│ 标题区                   │
│ 正文区                   │
│ 发布日期                 │
│                         │
│                         │
│ ... (可滚动内容)         │
│                         │
├─────────────────────────┤
│ 底部交互区（常驻）        │ ← Sticky Footer
└─────────────────────────┘
```

#### 3.2 顶部作者区（常驻）

**元素**：
- 返回按钮（<）
- 作者头像 + 昵称
- 关注按钮

**关注按钮**：
- 支持点击切换状态
- 使用MMKV持久化存储

#### 3.3 横滑容器（HorizontalPager）

**布局规则**：
- 使用首图宽高比作为容器比例
- 宽高比限制：3:4 ~ 16:9
- 非首图按首图比例裁切，确保图片内容充满容器

**功能要求**：
- 支持手动横滑切换
- 支持图片级别的加载态、失败态
- 使用 `HorizontalPager` + `PagerState`

**图片加载**：
```kotlin
@Composable
fun ImageSlide(clip: Clip) {
    Image(
        painter = rememberAsyncImagePainter(clip.url),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(calculateAspectRatio(clip))
    )
}
```

#### 3.4 进度条

**展示规则**：
- 单图不展示
- 多图展示条状进度条
- 跟随图片切换横向滚动
- 无需支持拖动快速预览

#### 3.5 标题区和正文区

**标题**：完整展示不截断

**正文**：
- 完整展示不截断
- 话题词高亮显示（如 #快来参与）
- 话题词支持点击（跳转到简洁的话题页）

**话题词解析**：
```kotlin
// 根据API返回的hashtag数组解析
data class Hashtag(
    val start: Int,  // 起始下标（闭区间）
    val end: Int     // 终止下标（开区间）
)
```

#### 3.6 发布日期

**日期文案规则**：
- 24h内：显示具体时间，如"HH:mm"、"昨天 HH:mm"
- 7天内：显示为"x天前"
- 其余：显示为"MM-dd"

**实现示例**：
```kotlin
fun formatPublishTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp * 1000

    return when {
        diff < 24 * 60 * 60 * 1000 -> {
            // 24小时内
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp * 1000))
        }
        diff < 2 * 24 * 60 * 60 * 1000 -> {
            // 昨天
            "昨天 " + SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp * 1000))
        }
        diff < 7 * 24 * 60 * 60 * 1000 -> {
            // 7天内
            "${diff / (24 * 60 * 60 * 1000)}天前"
        }
        else -> {
            // 其他
            SimpleDateFormat("MM-dd", Locale.getDefault()).format(Date(timestamp * 1000))
        }
    }
}
```

#### 3.7 底部交互区（常驻）

**元素**：
- 快捷评论框
- 点赞按钮 ✓
- 评论按钮
- 收藏按钮
- 分享按钮 ✓

**实现要求**：
- 点赞和分享需要支持交互
- 其余可以不支持

---

### 4. 进阶功能

#### 4.1 页面转场动画

**进场动画（打开详情页）**：
- 点击的卡片封面逐渐放大至详情页横滑容器区域
- 详情页横滑容器外区域渐显

**退场动画（返回双列）**：
- 详情页横滑容器图片逐渐缩小至双列卡片封面区域
- 详情页横滑容器外区域渐隐

**侧滑退场**：
- 手指拖动：详情页跟随移动并缩小，双列页面由暗转明
- 松手后：完成缩小动画

**实现方案**：
- 使用 `SharedElement` Transition
- 或使用 `AnimatedContent` + 自定义动画

#### 4.2 背景音乐

**静音按钮**：
- 位置：横滑容器右下角
- 功能：控制音乐播放器是否静音

**播控逻辑**：
- 打开详情页时，静音态由记忆决定（初始为非静音）
- 关闭页面、打开其他页面、退后台时暂停播放
- 返回详情页、回前台时恢复播放

**状态记忆**：
- APP生命周期内生效
- 打开不同作品详情页继承上次状态
- APP冷启动后重置

**技术实现**：
```kotlin
// 使用MediaPlayer或ExoPlayer
class MusicPlayer {
    private val mediaPlayer = MediaPlayer()
    var isMuted: Boolean by mutableStateOf(false)

    fun play(url: String) {
        mediaPlayer.apply {
            reset()
            setDataSource(url)
            prepare()
            start()
        }
    }

    fun setMute(mute: Boolean) {
        isMuted = mute
        mediaPlayer.setVolume(if (mute) 0f else 1f, if (mute) 0f else 1f)
    }
}
```

#### 4.3 自动轮播

**功能**：
- 复用静音按钮控制轮播状态
- 图片展示N秒后自动切换至下一张
- 切换至最后一张后，再次切换回第一张，循环播放
- 自动轮播时支持手动打断

**实现**：
```kotlin
LaunchedEffect(pagerState.currentPage, isAutoPlay) {
    if (isAutoPlay) {
        delay(3000) // 3秒自动切换
        val nextPage = (pagerState.currentPage + 1) % pageCount
        pagerState.animateScrollToPage(nextPage)
    }
}
```

#### 4.4 视频片段播放

**功能**：
- 横滑容器支持在任意位置插入视频片段
- 根据服务端下发的片段数据展示
- 支持分片段视频播放
- 播控场景下，视频片段完播后自动切换至下一片段

**实现**：
```kotlin
when (clip.type) {
    0 -> ImageSlide(clip)  // 图片
    1 -> VideoSlide(clip)  // 视频
}
```

---

## 🔌 API接口文档

### 基础信息

- **接口地址**：`https://college-training-camp.bytedance.com/feed/`
- **请求方法**：GET
- **请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| count | Int | 是 | 请求作品数量 |
| accept_video_clip | Bool | 否 | 是否支持下发视频片段（进阶功能需传true） |

### 响应格式

```json
{
  "status_code": 0,           // 状态码（0：成功，其它：失败）
  "has_more": 1,              // 是否还有更多作品（0或1）
  "post_list": [{             // 作品列表
    "post_id": "100080",      // 作品ID
    "title": "图文时代",       // 作品标题
    "content": "客户端训练营 #快来参与",  // 作品正文
    "hashtag": [{             // 话题词位置信息
      "start": 7,             // 起始下标（闭区间）
      "end": 12               // 终止下标（开区间）
    }],
    "create_time": 1756289925,  // 作品创建时间戳
    "author": {               // 作者信息
      "user_id": "12306",     // 用户ID
      "nickname": "营业中",    // 用户昵称
      "avatar": "https://..."  // 头像链接
    },
    "clips": [{               // 图片/视频片段数据
      "type": 0,              // 片段类型（0：图片，1：视频）
      "width": 1440,          // 宽度
      "height": 2438,         // 高度
      "url": "https://..."    // 资源链接
    }],
    "music": {                // 音乐信息
      "volume": 100,          // 音量
      "seek_time": 0,         // 起始播放位置（ms）
      "url": "https://..."    // 音乐链接
    }
  }]
}
```

### 数据模型设计

**Domain层模型**：
```kotlin
// domain/model/Post.kt
data class Post(
    val postId: String,
    val title: String,
    val content: String,
    val hashtags: List<Hashtag>,
    val createTime: Long,
    val author: Author,
    val clips: List<Clip>,
    val music: Music?
)

data class Author(
    val userId: String,
    val nickname: String,
    val avatar: String
)

data class Clip(
    val type: ClipType,  // IMAGE, VIDEO
    val width: Int,
    val height: Int,
    val url: String
)

data class Music(
    val volume: Int,
    val seekTime: Long,
    val url: String
)

data class Hashtag(
    val start: Int,
    val end: Int
)
```

**Data层模型（Response DTO）**：
```kotlin
// data/model/FeedResponse.kt
data class FeedResponse(
    @SerializedName("status_code")
    val statusCode: Int,
    @SerializedName("has_more")
    val hasMore: Int,
    @SerializedName("post_list")
    val postList: List<PostDto>?
)

data class PostDto(
    @SerializedName("post_id")
    val postId: String,
    val title: String,
    val content: String,
    val hashtag: List<HashtagDto>,
    @SerializedName("create_time")
    val createTime: Long,
    val author: AuthorDto,
    val clips: List<ClipDto>,
    val music: MusicDto?
)

// Mapper: DTO -> Domain
fun PostDto.toDomain(): Post = Post(...)
```

---

## 💾 本地存储方案

使用 **MMKV** 存储以下数据：

### 存储项

| Key | 类型 | 说明 |
|-----|------|------|
| `like_status_{post_id}` | Boolean | 某作品的点赞状态 |
| `follow_status_{user_id}` | Boolean | 某用户的关注状态 |
| `music_mute_status` | Boolean | 音乐静音状态（APP生命周期内） |

### 使用示例

```kotlin
// util/PreferencesManager.kt
class PreferencesManager {
    private val mmkv = MMKV.defaultMMKV()

    fun setLikeStatus(postId: String, isLiked: Boolean) {
        mmkv.encode("like_status_$postId", isLiked)
    }

    fun getLikeStatus(postId: String): Boolean {
        return mmkv.decodeBool("like_status_$postId", false)
    }

    fun setFollowStatus(userId: String, isFollowed: Boolean) {
        mmkv.encode("follow_status_$userId", isFollowed)
    }

    fun getFollowStatus(userId: String): Boolean {
        return mmkv.decodeBool("follow_status_$userId", false)
    }
}
```

---

## 🎨 UI设计规范

### 设计稿
- **Figma链接**：https://www.figma.com/design/REGsGFaHnc7rjyvHfkeOy4/...
- **要求**：严格按照设计稿标注实现，包括间距、颜色、字体大小等

### 关键尺寸
- 双列间距：8dp
- 卡片圆角：12dp
- 底部导航高度：56dp
- 顶部Tab高度：48dp

### 颜色主题
参考 `ui/theme/Color.kt`，确保颜色符合Material Design规范

---

## 🧪 测试要点

### 功能测试
- [ ] 底部导航切换正常
- [ ] 双列瀑布流加载显示正常
- [ ] 下拉刷新和上滑加载更多
- [ ] 点击卡片跳转详情页
- [ ] 详情页横滑切换图片
- [ ] 点赞、关注状态切换和持久化
- [ ] 话题词点击跳转

### 性能测试
- [ ] 列表快速滑动不卡顿
- [ ] 图片加载不阻塞UI
- [ ] 无明显内存泄漏
- [ ] 无明显发热

### 兼容性测试
- [ ] 不同屏幕尺寸适配
- [ ] Android 7.0+ 系统兼容
- [ ] 网络异常处理（无网、弱网）

---

## 🚀 开发建议

### 开发顺序
1. 搭建基础框架（导航、主题）
2. 实现数据层（API、Repository）
3. 实现双列瀑布流列表
4. 实现详情页基础功能
5. 添加交互功能（点赞、关注）
6. 实现进阶功能（转场动画、音乐等）
7. 性能优化和测试

### 注意事项
1. **图片裁切**：严格按照宽高比限制裁切
2. **状态管理**：使用StateFlow或LiveData管理UI状态
3. **错误处理**：网络请求失败、图片加载失败等需要有相应UI提示
4. **内存管理**：注意图片缓存，避免OOM
5. **代码规范**：保持良好的代码结构和命名规范

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

## 📞 资源链接

- Figma设计稿：https://www.figma.com/design/REGsGFaHnc7rjyvHfkeOy4/...
- API接口：https://college-training-camp.bytedance.com/feed/
- Jetpack Compose文档：https://developer.android.com/jetpack/compose
- Fresco文档：https://frescolib.org/
- ExoPlayer文档：https://exoplayer.dev/