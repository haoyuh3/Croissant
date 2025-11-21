# CrossiantApp - 技术文档

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
采用 **MVVM** 架构模式：

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