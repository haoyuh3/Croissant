# Croissant App - 技术文档

## 目录

- [项目概述](#项目概述)
- [实现方案/框架介绍](#实现方案框架介绍)
- [个人思考](#个人思考)

---

## 项目概述

### 业务定位

Croissant是一个基于字节跳动训练营课题的**UGC内容社区客户端应用**，模仿可颂APP的核心功能，为用户提供图文内容的浏览、互动和分享体验。

**核心特点：**
- **产品定位**：UGC内容社区，满足用户经验决策、圈层交流需求
- **产品形态**：双列图文视频混排，以图文体裁为主
- **技术栈**：(Kotlin + Java) + Jetpack Compose实现的现代化Android应用

**竞争优势：**
- 双列瀑布流布局：屏效比高，一屏展示多个作品，便于内容筛选
- 图文体裁为主：信息密度高、可控感强、创作门槛低
- 离线缓存支持：无网络环境下仍可浏览已缓存内容

**视频Demo**
- 主要功能：
- https://github.com/user-attachments/assets/e8fd0102-bf46-4b12-adaf-72f1acbeaac5
- 补充视频 视频下滑功能|头像|话题跳转： 
- https://github.com/user-attachments/assets/8c93f548-4681-4696-b34f-530e97624407
---

## 实现方案/框架介绍

### 功能完成度总览

根据训练营课题要求，以下是各功能模块的实现情况：

#### 基础功能完成度

| 功能模块 | 子模块      | 完成状态 | 实现位置 | 备注 |
|---------|----------|---------|----------|------|
| **应用框架** | 底部导航栏    | 已完成 | `BottomNavigationBar.kt` | 支持"首页"和"我"切换 |
| | 首页Tab    | 已完成 | `HomeTabItem.kt` | 默认"社区"Tab，其他Tab占位 |
| **双列瀑布流** | 双列框架     | 已完成 | `HomeScreen.kt:153-195` | LazyVerticalStaggeredGrid实现 |
| | 下拉刷新     | 已完成 | `HomeScreen.kt` | PullRefresh实现 |
| | 上滑LoadMore | 已完成 | `HomeViewModel.kt` | 触底自动加载 |
| | 首刷失败空态   | 已完成 | `HomeScreen.kt` | Error状态处理 |
| | Loading提示 | 已完成 | `HomeScreen.kt` | Loading状态展示 |
| | 作品卡片     | 已完成 | `PostCard.kt` | 封面+标题+作者+点赞 |
| | 封面裁切规则   | 已完成 | `PostCard.kt` | 3:4 ~ 4:3宽高比限制 |
| | 标题展示     | 已完成 | `PostCard.kt` | 最长2行，超出截断 |
| | 点赞交互     | 已完成 | `PostCard.kt` | 本地持久化存储 |
| **详情页** | 顶部作者区    | 已完成 | `DetailTopBar.kt` | 返回+作者信息+关注按钮 |
| | 关注功能     | 已完成 | `DetailViewModel.kt:97-123` | SP+Room双写 |
| | 底部交互区    | 已完成 | `DetailBottomBar.kt` | 评论框+点赞+评论+收藏+分享 |
| | 点赞交互     | 已完成 | `DetailViewModel.kt:76-95` | SP持久化 |
| | 分享交互     | 部分完成 | `DetailBottomBar.kt` | 仅打印日志 |
| | 横滑容器     | 已完成 | `DetailContent.kt:86-134` | HorizontalPager实现 |
| | 容器宽高比    | 已完成 | `DetailContent.kt:99` | 使用首图比例，3:4~16:9限制 |
| | 图片切换     | 已完成 | `DetailContent.kt` | 支持手动横滑 |
| | 加载态/失败态  | 已完成 | `DetailContent.kt:112-126` | SubcomposeAsyncImage实现 |
| | 进度条      | 已完成 | `DetailContent.kt:140-149` | 条状进度，跟随滑动 |
| | 标题区      | 已完成 | `DetailContent.kt:48-54` | 完整展示不截断 |
| | 正文区      | 已完成 | `DetailContent.kt:58-64` | 完整展示不截断 |
| | 话题词高亮    | 已完成 | `DetailContent.kt:154-217` | ClickableText实现 |
| | 话题词点击    | 已完成 | `HashtagScreen.kt` | 跳转话题页面 |
| | 发布日期     | 已完成 | `DetailContent.kt:69-73` + `DateUtil.kt` | 相对时间格式化 |
| **个人主页** | 个人信息展示   | 已完成 | `ProfileScreen.kt` | 头像+昵称+简介 |
| | 统计数据     | 已完成 | `ProfileScreen.kt:92-123` | 关注/粉丝/获赞（占位数据） |
| | 头像编辑     | 已完成 | `ProfileScreen.kt:150-169` | 图片选择器 |
| | 昵称/简介编辑  | 已完成 | `ProfileScreen.kt:254-333` | Dialog编辑 |

#### 进阶功能完成度

| 功能模块 | 子模块 | 完成状态 | 实现位置 | 备注 |
|---------|--------|---------|----------|------|
| **页面转场** | 转场动画 | 实现进场退场动画 | - | 点击进场/退场、侧滑跟手 |
| **详情页** | 背景音乐 | ❌ 未实现 | - | 静音按钮、播控逻辑、状态记忆 |
| | 自动轮播 | ❌ 未实现 | - | 图片自动切换 |
| | 视频片段 |   已完成 | `VideoPlayer.kt` | ExoPlayer实现 |

#### 自由探索完成度

| 思考方向 | 完成状态 | 实现位置                    | 说明                       |
|---------|---------|-------------------------|--------------------------|
| **体验优化** | 已完成 | 多处优化                    | -                        |
| 离线/弱网体验 | 已完成 | `FeedRepositoryImpl.kt` | 离线优先策略，Room缓存，LoadMore报错 |
| 图片加载优化 | 已完成 | 全局                      | Coil异步加载+缓存              |
| 列表滑动优化 | 已完成 | `HomeScreen.kt`         | LazyList Key优化           |
| 视频播放支持 | 已完成 | `VideoPlayer.kt`        | Media3 ExoPlayer         |
| 话题页面 | 已完成 | `HashtagScreen.kt`      | 话题词点击跳转                  |
|视频竖刷组件| 部分完成|                         | 只有纯视频post支持竖直下滑切换帖子     |

---

### 1. 整体架构设计

#### 1.1 架构模式：MVVM + Clean Architecture

本项目采用**MVVM（Model-View-ViewModel）**架构模式，实现高内聚、低耦合的代码结构。
```
app/src/main/java/com/bytedance/crossiantapp/
├── data/              # 数据层
|
├── domain/           # 业务逻辑层
│   ├── model/        # 领域模型
│   ├── repository/   # Repository接口
│   └── usecase/      # 用例
├── presentation/     # 表现层
│   ├── home/         # 首页（双列瀑布流）
│   ├── detail/       # 详情页
│   ├── profile/      # 个人主页
│   ├── navigation/   # 导航
│   └── components/   # 通用组件
├── di/               # 依赖注入
└── util/             # 工具类
```

```
┌─────────────────────────────────────────────────────────-┐
│                    Presentation Layer                    |
│  (UI/ViewModel - Jetpack Compose + StateFlow)            |
│  ┌───────────┐  ┌──────────┐  ┌──────────────┐           |
│  │HomeScreen │  │DetailScn │  │ProfileScreen │           |
│  └─────┬─────┘  └────┬─────┘  └──────┬───────┘           |
│        │             │                │                  │
│  ┌─────▼─────┐  ┌───▼──────┐  ┌─────▼────────┐           |
│  │HomeVM     │  │DetailVM  │  │ProfileVM     │           |
│  └─────┬─────┘  └────┬─────┘  └──────┬───────┘           |
└────────┼─────────────┼────────────────┼─────────────────-|
         │             │                │
┌────────▼─────────────▼────────────────▼─────────────────┐
│                     Domain Layer                        | 
│  (Business Logic - Pure Kotlin)                         |
│  ┌──────────────┐  ┌────────────────────────┐           │
│  │  UseCases    │  │  Repository Interface  │           │
│  ├──────────────┤  ├────────────────────────┤           │
│  │GetFeedUseCase│  │  FeedRepository        │           │
│  │GetDetailUse..│  │  - getFeed()           │           │
│  └──────┬───────┘  └────────┬───────────────┘           │
└─────────┼──────────────────┼──────────────────────────--|
          │                  │
┌─────────▼──────────────────▼──────────────────────────┐
│                     Data Layer                        │
│  (Data Sources - Network + Local Storage)             │
│  ┌─────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │FeedRepoImpl │  │  FeedApi     │  │ PostDao      │  │
│  └─────┬───────┘  │  (Retrofit)  │  │ (Room DB)    │  │
│        │          └──────┬───────┘  └──────┬───────┘  │
│        │                 │                  │         │
│  ┌─────▼─────────────────▼──────────────────▼───────┐ │
│  │  Data Mapping: DTO ↔ Domain Model ↔ Entity       │ │
│  └──────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────┘
```

**各层职责：**

| 层级 | 职责 | 关键组件 | 依赖关系 |
|------|------|----------|----------|
| **Presentation** | UI渲染、用户交互、状态管理 | Composable函数、ViewModel、StateFlow | 依赖Domain层 |
| **Domain** | 业务逻辑、数据协调、用例编排 | UseCase、Repository接口、领域模型 |
| **Data** | 数据获取、持久化、网络请求 | API、DAO、Repository实现、Mapper | 实现Domain接口 |
---

#### 1.2 核心技术栈

| 技术领域 | 框架/库 | 版本 | 用途说明                |
|---------|---------|------|---------------------|
| **构建工具** | Gradle | 8.13.1 | 项目构建和依赖管理           |
| **编程语言** | Kotlin | 2.0.21 | 主语言，Kotlin, Java实现  |
| **UI框架** | Jetpack Compose | 2024.09.00 | 声明式UI，Material3设计   |
| **依赖注入** | Hilt | 2.48 | 依赖管理和对象提供           |
| **网络请求** | Retrofit | 2.11.0 | RESTful API调用       |
| **JSON解析** | Gson | 2.10.1 | 数据序列化/反序列化          |
| **本地数据库** | Room | 2.6.1 | SQLite抽象层，离线缓存      |
| **图片加载** | Coil | 2.5.0 | Compose专用图片加载库      |
| **异步编程** | Coroutines | 1.7.3 | 协程处理异步操作            |
| **生命周期** | Lifecycle | 2.8.7 | ViewModel、LiveData等 |
| **导航** | Navigation Compose | 2.8.5 | 单Activity多屏幕导航      |
| **媒体播放** | Media3 (ExoPlayer) | 1.2.0 | 视频播放支持              |

---

### 2. 数据流架构

#### 2.1 单向数据流（Unidirectional Data Flow）

本项目严格遵循**单向数据流**原则，确保数据流动的可预测性和可追溯性：

```
┌───────────────────────────────────────────────────────┐
│                    USER INTERACTION                   
│         (点击、滑动、输入等用户操作)                       
└───────────────────┬───────────────────────────────────┘
                    │
                    ▼
┌───────────────────────────────────────────────────────┐
│                   VIEW (Composable)                   
│- viewModel.loadFeed()                                 
│- viewModel.toggleLike(postId)                         
└───────────────────┬───────────────────────────────────┘
                    │
                    ▼
┌───────────────────────────────────────────────────────┐
│                      VIEWMODEL                        │
│   - 调用UseCase执行业务逻辑                              
│   - 更新MutableStateFlow                               
│   - _uiState.value = newState                         │
└───────────────────┬───────────────────────────────────┘
                    │
                    ▼
┌──────────────────────────────────────────────────|
│                 USE CASE                         
│- 协调Repository获取数据 业务处理                     
└───────────────────┬──────────────────────────────|
                    │
                    ▼
┌───────────────────────────────────────────────────────┐
│                      REPOSITORY                       
│- 决策数据源（Network优先 → Cache降级）                    
│- 数据转换（DTO → Domain Model）                         
└───────────────────┬───────────────────────────────────┘
                    │
        ┌───────────┴───────────┐
        ▼                       ▼
┌──────────────┐        ┌──────────────┐
│   NETWORK    │        │ LOCAL CACHE  │
│   (Retrofit) │        │ (Room + SP)  │
└──────┬───────┘        └──────┬───────┘
       │                       │
       └───────────┬───────────┘
                   ▼
┌───────────────────────────────────────────────────────┐
│                   DATA MAPPING                        │
│   - PostDto → Post (Domain Model)                     │
│   - Post → PostEntity (Database Entity)               │
└───────────────────┬───────────────────────────────────┘
                    │
                    ▼
┌───────────────────────────────────────────────────────┐
│               COMPOSE RECOMPOSITION                   
│   - collectAsState()监听StateFlow                      
│   - 状态变化触发UI重组                                   
│   - 自动更新界面显示                                     
└───────────────────────────────────────────────────────┘
```
---

#### 2.2 数据加载
本地状态填充逻辑：使用share preference填充点赞关注数据
HomeScreen和DetailScreen 直接传递Post对象渲染UI
本应用实现了基础的离线优先策略,确保在无网络环境下仍能提供基本功能：

**缓存策略：**
HomeViewModel 初次加载时将帖子存入数据库。 当无网络打开时优先从数据库中获取帖子。

| 数据类型 | 主存储 | 辅助存储 | 同步策略 |
|---------|--------|----------|----------|
| **Feed列表** | Room Database | - | 每次API成功后替换 |
| **点赞状态** | SharedPreferences | - | 用户操作立即写入 |
| **关注状态** | SharedPreferences | Room (FollowedUser表) | SP立即写入，Room异步同步 |
| **点赞数** | SharedPreferences | - | 本地计算维护 |

#### 2.3 数据层功能

| 功能项 | 技术要点                        |
|--------|-----------------------------|
| **Room数据库** | posts表 + followed_users表    |
| **SharedPreferences** | 点赞/关注状态 + 用户资料              |
| **DTO映射** | PostDto ↔ Post ↔ PostEntity |

PostDto: 序列化Api调用结果 - FeedResponse.kt
Post: 领域模型 - Post.kt
PostEntity: 数据库实体 - PostEntity.kt

---

### 3. UI架构设计
#### UI组件库
| 组件名                     | 功能描述              | 复用场景 | 文件位置 |
|-------------------------|-------------------|----------|----------|
| **PostCard**            | 帖子卡片（封面+标题+作者+点赞） | 首页Feed流 | `PostCard.kt` |
| **BottomNavigationBar** | 底部导航栏（5个Tab）      | MainActivity | `BottomNavigationBar.kt` |
| **HomeTabRow**          | 顶部Tab栏（5个Tab）     | 首页 | `HomeTabItem.kt` |
| **DetailTopBar**        | 详情页顶栏（返回+作者+关注）   | 详情页 | `DetailTopBar.kt` |
| **DetailContent**       | 详情页内容             | 详情页 | `DetailContent.kt` |
| **DetailBottomBar**     | 详情页底栏（评论框+操作按钮）   | 详情页 | `DetailBottomBar.kt` |
| **VideoPlayer**         | 视频播放器组件           | 详情页、Feed流 | `VideoPlayer.kt` |
|**HomeScreen**           | 首页主界面               | MainActivity | `HomeScreen.kt` |
|**DetailScreen**         | 详情页主界面               | MainActivity | `DetailScreen.kt` |
|**ProfileScreen**        | 个人中心               | MainActivity | `ProfileScreen.kt` |
---

#### 3.1 MainActivity
| 功能项                           | 技术要点         | 文件位置                     |
|-------------------------------|--------------|--------------------------|
| **bottomBar**                 | 底部导航栏（5个Tab） | `BottomNavigationBar.kt` |
| **NavGraph**                  | 根据底部路由渲染UI画面 | `NavGraph.kt`            |
---
#### 3.2 首页Tab
- **Tab列表**（从左到右）：北京、团购、关注、社区、推荐
- **实现要求**：
    - 默认位于"社区"
    - 其他Tab无需支持点击
- Tab Row + 内容区域
- 实现HomeTabRow
---
#### 3.3 家页面
**UI层级**：
```
Column
├── HomeTabRow (顶部Tab栏)
├── HorizontalDivider (分割线)
└── Box (Tab内容区域)
    └── CommunityTabContent (社区Tab - 瀑布流)
```
| 功能项    | 技术要点 | 文件位置 |
|--------|----------|-------|
| **HomeTabRow** | 顶部Tab栏（5个Tab）| `HomeTabItem.kt` |
| **CommunityTabContent** | 社区Tab内容（默认Tab） | `HomeScreen.kt` |
| **lazyVerticalStaggeredGrid** | 双列瀑布流 | `HomeScreen.kt` |
| **PostCard** | 帖子卡片（封面+标题+作者+点赞） | `PostCard.kt` |


##### PostCard组件实现
- **文件**: `presentation/home/components/PostCard.kt`
- 使用card库
- 1. 封面图片/视频
- 2. 标题/内容区域
- 3. 底栏：作者信息 + 点赞按钮
---
![postCard.png](https://github.com/haoyuh3/CrossiantApp/blob/d9b6ecf0be38c5d9e7b8cd02087976d2bc76e6b2/Progress/PostCard.png)
---

##### 双列瀑布流实现
- LazyVerticalStaggeredGrid配置 (presentation/home/HomeScreen.kt:153-195)
- 使用LazyVerticalStaggeredGrid组件
---
##### VideoPlayer实现
- 使用ExoPlayer组件
- 自动创建与释放: 使用 remember 来创建和保留 ExoPlayer 实例，确保在 videoUrl 变化时重新创建播放器，
- Composable 离开屏幕时调用 exoPlayer.release()，有效地防止了内存泄漏
- 加载状态: 在视频缓冲（Player.STATE_BUFFERING）时，会显示一个带半透明黑色背景的 CircularProgressIndicator
- 错误处理: 当播放器进入空闲状态时，会显示一个黑屏和“视频加载失败”的文本提示，提供了清晰的错误反馈
  ![VideoPlayer.png](https://github.com/haoyuh3/CrossiantApp/blob/83cb9633a8aba968dd2914b395e2e9c2981a524d/Progress/VideoPlayer.png)
---

##### HomeViewModel
- **1. 首屏加载**
**流程**：
1. 设置加载状态 `InitLoading`
2. 调用 `GetFeedUseCase` 获取20条数据
3. 使用 `fillWithLocalState()` 填充本地状态（点赞、关注）
4. 更新 `_posts` 和 `_uiState`
5. 根据数据是否为空设置 `Empty` 或 `Success` 状态

- **2. PullRefresh 功能实现：** (presentation/home/HomeViewModel.kt:93-114)
- HomeScreen捕捉手势变化，异步改变状态
- HomeViewModel执行刷新逻辑
---
- **3. LoadMore 功能实现：** (presentation/home/HomeViewModel.kt)
- 双列瀑布流下方放置一个加载更多组件。当compose重组加载更多组件，UI状态更新
- HomeViewModel调用loadMore逻辑

---
#### 3.4 帖子详情模块

| 功能项 | 技术要点                            | 文件位置 |
|--------|---------------------------------|----------|
| 详情页导航 | Navigation Compose + postId参数传递 | `NavGraph.kt` |
| 数据加载 | HomeScreen传递对象                  | `DetailViewModel.kt:41-74` |
| 横滑图片轮播 | `HorizontalPager` + 进度指示器       | `DetailContent.kt:86-134` |
| 宽高比自适应 | 读取`Clip.displayAspectRatio`动态设置 | `DetailContent.kt:99` |
| 点赞交互 | 即时UI更新 + SharedPreferences持久化   | `DetailViewModel.kt:76-95` |
| 关注功能 | UI更新 + SP + Room双写              | `DetailViewModel.kt:97-123` |
| 作者信息展示 | 头像 + 昵称 + 关注按钮                  | `DetailTopBar.kt:22-82` |
| 底部操作栏 | 评论框(禁用) + 点赞/评论/收藏/分享按钮         | `DetailBottomBar.kt:23-119` |
---
##### 整体布局结构

**外层容器**
- 使用Column作为主容器，设置fillMaxSize填充可用空间
- 添加verticalScroll修饰符，使整个内容区域可上下滚动
- 滚动状态由rememberScrollState()管理

**布局层次**（从上到下）：
1. **横滑媒体区域**：ImagePagerSection组件显示图片/视频轮播
2. **内容区域**：包裹在带水平padding的Column中
    - 标题区域：显示post.title
    - 正文区域：HashtagText组件显示带话题高亮的内容
    - 时间区域：显示格式化的发布时间
3. **底部留白**：避免内容被BottomBar遮挡
   ![detail.image](https://github.com/haoyuh3/CrossiantApp/blob/b428012a7e6a35b8993e9b32c2105aeb4c8a896e/Progress/DetailScreen.png)
---

#### 3.5个人中心模块

| 功能项 | 技术要点 | 文件位置 |
|--------|----------|----------|
| 个人信息展示 | 头像 + 昵称 + 简介 | `ProfileScreen.kt:34-124` |
| 统计数据展示 | 关注/粉丝/获赞数（占位数据） | `ProfileScreen.kt:92-123` |
| 头像编辑 | 图片选择器 + URI权限持久化 | `ProfileScreen.kt:150-169` |
| 昵称/简介编辑 | Dialog编辑 + SharedPreferences存储 | `ProfileScreen.kt:254-333` |
| 编辑图标 | 点击弹出编辑对话框 | `ProfileScreen.kt:127-147` |

##### 个人首页
- **个人** 头像，关注，粉丝，喜欢
- **实现要求**：
    - **Column** 是一个垂直方向的线性布局 包括(ICON + TXT + ROW)
    - **Row** 是一个水平方向的线性布局 包括(关注，粉丝，喜欢)
---
![profile.image](https://github.com/haoyuh3/CrossiantApp/blob/98f68030e7099a4af952949c4c9bec27a9216fd5/Progress/profile.png)
---
#### 3.6 关注列表
- 头像 + 名字 + 关注按钮
- 使用Room数据库存储关注列表
---
![followList.image](https://github.com/haoyuh3/CrossiantApp/blob/fe812653d798f470796456c9f0e442d5db2740e2/Progress/followList.png)
---
#### 3.7 动画和页面跳转

| 视频滑动刷新 | 主页面切换  |
| :---: | :---: |
| <video src="https://github.com/user-attachments/assets/3dcb5c04-2ff5-4e8a-9dd2-9a8506ddcb6e" controls width="300"></video> | <video src="https://github.com/user-attachments/assets/c0e6225f-f9c5-4d14-8eff-d37d7d7b4364" controls width="300"></video> |
---
| 无网络刷新 | 网络刷新 |
| :---: | :---: |
| <video src="https://github.com/user-attachments/assets/3aae2919-41cd-4058-95a6-a8757ff9bd31" controls width="300"></video> | <video src="https://github.com/user-attachments/assets/9aed9c89-522a-45df-b603-874f17f681f6" controls width="300"></video> |
---
#### 3.8 性能优化技术

**1. LazyList的Key优化**
```kotlin
LazyVerticalStaggeredGrid {
    items(
        items = posts,
        key = { post -> post.postId }  // 稳定的唯一标识符
    ) { post ->
        PostCard(post)
    }
}
```
**效果：**
- 列表项位置变化时，Compose能识别同一对象，避免重建
- 减少不必要的重组，提升滚动性能

**2. 异步图片加载**
```kotlin
// 使用Coil的SubcomposeAsyncImage实现渐进式加载
SubcomposeAsyncImage(
    model = imageUrl,
    loading = { CircularProgressIndicator() },    // 加载态
    error = { Text("加载失败") },                  // 错误态
    contentScale = ContentScale.Crop
)

```
**3. 返回主页面图片渲染**
**困难：**
- 详情页返回时, 点击返回按钮跳转后执行动画, 过一段时间图片才加载
- 原因图片需要提前渲染
```kotlin
onNavigateBack = {
    // 返回前设置刷新信号（在动画开始前立即触发）
    navController.previousBackStackEntry
        ?.savedStateHandle
        ?.set("refresh_from_detail", true)

    navController.navigateUp()
}
```

### 4. 数据层设计

#### 4.1 数据库设计（Room）

**Schema设计：**

```sql
-- posts表（帖子主表）
CREATE TABLE posts (
    post_id TEXT PRIMARY KEY NOT NULL,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    hashtags_json TEXT NOT NULL,      -- JSON序列化的List<Hashtag>
    create_time INTEGER,
    author_json TEXT NOT NULL,         -- JSON序列化的Author对象
    clips_json TEXT NOT NULL,          -- JSON序列化的List<Clip>
    music_json TEXT,                   
    like_count INTEGER DEFAULT 0,
    is_liked INTEGER DEFAULT 0         
);

-- followed_users表（关注用户表）
CREATE TABLE followed_users (
    userId TEXT PRIMARY KEY NOT NULL,
    nickname TEXT NOT NULL,
    avatar TEXT NOT NULL,
    followedTime INTEGER DEFAULT (strftime('%s', 'now'))
);
```
**DAO接口设计： app/src/main/java/com/bytedance/croissantapp/data/local/dao/PostDao.java**

---

#### 4.2 网络层设计（Retrofit）

**API接口定义：**

```kotlin
interface FeedApi {
    @GET("feed/")
    suspend fun getFeed(
        @Query("count") count: Int,
        @Query("accept_video_clip") acceptVideoClip: Boolean = false
    ): FeedResponse
}
```
**网络配置（AppModule.kt）：**

---

#### 4.3 数据转换层（Mapper）

**转换链路：**
```
Network DTO ←→ Domain Model ←→ Database Entity
  (PostDto)      (Post)          (PostEntity)
```
**设计原则：**
- Domain Model是纯业务模型，不包含任何序列化注解
- DTO负责网络传输，使用`@SerializedName`适配后端字段
- Entity负责数据库存储

---

### 5. 导航系统

#### Navigation Compose架构
- 本项目使用**Navigation Compose**实现单Activity多屏幕架构：

#### 底部导航栏实现： BottomNavigationBar.kt
- **Tab列表**（从左到右）：首页、朋友、相机(+)、消息、我
- **实现要求**：
    - 只需支持"首页"和"我"的点击
    - 启动后默认在"首页"
    - 使用 `NavigationBarItem`
    -
#### 导航系统配置
- 理解Navigation Compose
  **Navigation Compose** 是Jetpack Compose的导航库：
- 使用rememberNavController() 保持navigator controller
- 实现NavGraph (导航页面)
- 实现NavHost (页面容器)
- composable(route = Routes.HOME)/ Routes.Profile 配置导航路由
---
### 6. 依赖注入（Hilt）AppModule.kt
### 7. 工具类

| 工具类 | 功能 | 文件位置 |
|--------|------|----------|
| **DateUtil** | 时间格式化（相对时间："2小时前"） | `util/DateUtil.kt` |
| **LikeCntUtil** | 数字格式化（10000 → "1.0w"） | `util/LikeCntUtil.kt` |
| **ImageUtil** | 图片处理工具 | `util/ImageUtil.kt` |
| **Constants** | 应用常量定义 | `util/Constants.kt` |


## 个人思考

### 1. 架构设计的收获
---
#### 1.1 MVVM在Compose中的适配与MVVM状态更新

传统MVVM使用LiveData + XML，迁移到Compose后的变化：

| 对比项 | 传统MVVM | Compose MVVM |
|--------|---------|--------------|
| **状态容器** | LiveData | StateFlow / State |
| **UI更新触发** | observe(lifecycle) { } | collectAsState() |
| **双向绑定** | DataBinding | |
| **生命周期感知** | LiveData自动处理 | collectAsStateWithLifecycle() |
---
#### 1.2 熟悉多种UI组件

- LazyVerticalStaggeredGrid
- EXOPlayer
- HorizontalPager
- Card
- NavigationBarItem
---

### 2. 技术选型的反思

#### 2.1 Jetpack Compose的深度应用

**选型理由：**
- **声明式UI**减少80%的样板代码（相比XML + findViewById）
- **预览功能**：`@Preview`注解实现组件级调试
- **回调函数**： 使用回调函数便于触发状态改变， 按钮回调
---


#### 2.2 离线优先策略的局限

**存在的问题：**

- 1 **无网缺少重试机制**
    - 从无网络到有网络多次重试提升用户体验
- 2.**缺少缓存过期策略（TTL）**
    - 用户可能看到一周前的旧数据
    - 解决方案：添加`cached_at`字段，超过1小时强制刷新
---

### 3. 当前架构的不足与改进方案

#### 3.1 关键问题：用户操作未同步到服务器

**后果：**
- 用户换设备后所有点赞记录丢失
- 作者看不到点赞数增加
- 无法实现跨端同步

#### 3.2 缺少数据一致性保证
**根本原因：**
- 点赞数同时存在于SharedPreferences和API响应
- 没有冲突解决策略
---

#### 3.3 首刷体验改进
- 考虑到api调用时间， 首屏数据可以调用缓存或者新数据衔接到缓存数据后面

#### 3.4 详情页面添加自动轮播功能
- 展示首张照片后，一定时间内切换下一张

#### 3.5 缺少视频封面

### 4. 项目总结与未来展望

#### 4.1 技术能力提升

通过Croissant项目的开发，系统性地掌握了：

| 技术领域 | 具体收获 | 应用场景 |
|---------|---------|----------|
| **Jetpack Compose** | 声明式UI、状态管理、性能优化 | 现代Android开发必备 |
| **Clean Architecture** | 分层设计、依赖倒置、测试驱动 | 中大型项目架构 |
| **Kotlin Coroutines** | 异步编程、结构化并发、Flow | 网络请求、数据库操作 |
| **Hilt依赖注入** | 模块化、可测试性、对象生命周期管理 |  |
| **Room数据库** | ORM映射、数据持久化、迁移管理 | 离线优先应用 |

---

#### 4.2 待实现功能规划

**目标**
- [ ] **音乐播放功能**
- [ ] **图片轮播功能**
- [ ] **视屏流竖滑**
    - 目前实现限制竖滑(只能浏览只包含一个视频的Post)
- [ ] **评论系统**
    - 评论列表展示（RecyclerView → LazyColumn）
    - 回复功能
    - @提及用户
- [ ] **无网/弱网络**
    - 重试刷新
    - 首屏数据加载优化， 防止过久API加载影响用户体验
---

#### 4.3 反思与感悟

**遇到的挑战：**
1. **Compose学习曲线**：初期对重组机制理解不足，导致性能问题
2. **状态管理混乱**：一度同时使用LiveData、StateFlow、State，后统一为StateFlow
3. **数据存储选型**： 部分数据一开始使用sharepreference存储，后续使用Room开发，存在部分数据不一致问题
4. **无网络ViewModel状态管理**: 一开始状态设计简单, 忽略网络中途恢复逻辑 后续重新设计状态转移函数
5. **客户端开发**注重用户点击率, UI, 无网络弱网络设计, 双列图片视频流目的都是为了增加用户点击量优化用户体验


## 附录

### A. 项目运行环境

| 项目 | 要求 |
|------|------|
| **最低Android版本** | API 24 (Android 7.0) |
| **目标Android版本** | API 36 (Android 14+) |
| **Kotlin版本** | 2.0.21 |
| **Gradle版本** | 8.13.1 |
| **JDK版本** | JDK 11 |

### B. 核心依赖清单

```kotlin
dependencies {
    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Coil
    implementation("io.coil-kt:coil-compose:2.5.0")
}
```

### C. 参考资源

- [Jetpack Compose官方文档](https://developer.android.com/jetpack/compose)
- [Android架构指南](https://developer.android.com/topic/architecture)
- [Kotlin Coroutines官方指南](https://kotlinlang.org/docs/coroutines-guide.html)
- [Room数据库文档](https://developer.android.com/training/data-storage/room)

---

**文档版本**：v1.0
**最后更新**：2025-12-01
**作者**：黄浩宇
**联系方式**：[项目仓库](https://github.com/haoyuh3/CrossiantApp)
