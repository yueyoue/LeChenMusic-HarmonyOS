# LeChen Music - HarmonyOS (KMP)

乐尘音乐 - Navidrome 客户端（鸿蒙 + 安卓 双端支持）

## 项目简介

基于 **Kotlin Multiplatform (KMP)** 架构，一套核心逻辑同时支持 Android 和 HarmonyOS NEXT。

## 项目结构

```
LeChenMusic-HarmonyOS/
├── shared/                    # 共享模块 (Kotlin)
│   └── src/commonMain/
│       ├── api/               # Navidrome/Subsonic API 客户端
│       ├── model/             # 数据模型
│       ├── repository/        # 数据仓库
│       └── util/              # 工具类
│
├── androidApp/                # Android 端 (Jetpack Compose + ExoPlayer)
│   └── src/main/
│       ├── java/              # UI 屏幕、播放器服务
│       └── res/               # 资源文件
│
└── harmonyApp/                # 鸿蒙端 (ArkTS)
    └── entry/src/main/ets/
        ├── common/            # API 客户端 (ArkTS 版)
        └── pages/             # 页面
```

## 功能

### 首页
- 最新专辑
- 每日推荐（随机推荐）
- 最近播放
- 随机专辑
- 歌单
- 电台

### 播放页
- 大封面显示（左滑相似歌曲，右滑歌词）
- 播放控制（随机/顺序/循环）
- 收藏功能
- 添加到歌单
- 定时停止（15分钟/30分钟/1小时/2小时）

### 搜索
- 搜索歌曲、歌手、专辑

### 设置
- 音乐缓存管理（2GB/4GB/8GB/16GB）
- 存储空间管理
- 服务器信息
- 退出登录

## 编译

### Android
```bash
./gradlew :androidApp:assembleDebug
```
生成 APK: `androidApp/build/outputs/apk/debug/`

### HarmonyOS
使用 DevEco Studio 打开 `harmonyApp/` 目录，编译生成 HAP。

## 技术栈

### 共享层
- Kotlin
- Ktor (HTTP 客户端)
- Kotlinx Serialization (JSON)
- Kotlinx Coroutines

### Android 端
- Jetpack Compose (UI)
- Material 3
- ExoPlayer / Media3 (音频播放)
- Coil (图片加载)
- Navigation Compose

### 鸿蒙端
- ArkTS
- ArkUI
- @ohos.network.http
- @kit.MediaKit (AVPlayer)

## 服务器

本项目支持所有兼容 Subsonic API 的音乐服务器，包括：
- Navidrome
- Airsonic
- Subsonic
- Gonic
