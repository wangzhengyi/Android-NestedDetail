# Android-NestedDetail

一个基于 Android NestedScrolling 机制实现的嵌套滑动详情页示例项目，展示了如何在 WebView 和 RecyclerView 之间实现流畅的嵌套滑动效果。

## 📱 项目简介

本项目演示了如何使用 Android 的 NestedScrolling 机制来实现类似新闻客户端详情页的滑动效果：
- 顶部 WebView 显示文章内容
- 中间不可滑动的分割区域
- 底部 RecyclerView 显示评论列表
- 三个区域之间可以无缝连贯滑动

## ✨ 功能特性

- **流畅的嵌套滑动**：WebView 和 RecyclerView 之间的无缝滑动体验
- **智能滑动分发**：根据滑动方向和位置智能分发滑动事件
- **速度传递**：支持 Fling 手势的速度在不同 View 之间传递
- **沉浸式状态栏**：支持 Android 5.0+ 的沉浸式状态栏效果
- **AndroidX 兼容**：已升级支持 AndroidX 库

## 🎯 应用场景

- 新闻客户端详情页
- 电商商品详情页
- 社交媒体内容详情页
- 任何需要 WebView + 列表组合的场景

## 🏗️ 技术架构

### 核心组件

1. **NestedScrollingDetailContainer**
   - 继承 ViewGroup，实现 NestedScrollingParent2 接口
   - 作为最外层滑动容器，负责协调子 View 的滑动
   - 处理滑动事件的分发和速度传递

2. **NestedScrollingWebView**
   - 继承 WebView，实现 NestedScrollingChild2 接口
   - 支持与父容器的嵌套滑动协作
   - 处理 WebView 内容的滑动边界检测

3. **RecyclerView**
   - 使用原生 RecyclerView，天然支持 NestedScrolling
   - 通过 tag 标识参与嵌套滑动

### 滑动流程

1. **向上滑动**：WebView 内容 → 外层容器 → RecyclerView
2. **向下滑动**：RecyclerView → 外层容器 → WebView 内容
3. **中间区域**：直接由外层容器处理，根据方向分发给对应子 View

## 🚀 快速开始

### 环境要求

- Android Studio 4.0+
- Android SDK 21+
- Gradle 7.5+

### 克隆项目

```bash
git clone https://github.com/your-username/Android-NestedDetail.git
cd Android-NestedDetail
```

### 运行项目

1. 使用 Android Studio 打开项目
2. 等待 Gradle 同步完成
3. 连接 Android 设备或启动模拟器
4. 点击 Run 按钮或使用快捷键 `Ctrl+R` (Windows/Linux) / `Cmd+R` (macOS)

## 📋 项目结构

```
app/src/main/
├── java/com/wzy/nesteddetail/
│   ├── activity/
│   │   └── MainActivity.java          # 主活动
│   ├── adapter/
│   │   └── RvAdapter.java             # RecyclerView 适配器
│   ├── model/
│   │   └── InfoBean.java              # 数据模型
│   ├── utils/
│   │   └── DimenHelper.java           # 尺寸工具类
│   └── view/
│       ├── NestedScrollingDetailContainer.java  # 嵌套滑动容器
│       └── NestedScrollingWebView.java          # 嵌套滑动 WebView
└── res/
    ├── layout/
    │   ├── activity_main.xml          # 主布局文件
    │   ├── layout_content_layout.xml  # 内容项布局
    │   └── layout_title_layout.xml    # 标题项布局
    └── values/
        ├── colors.xml
        ├── strings.xml
        └── styles.xml
```

## 🔧 核心实现

### 1. 布局结构

```xml
<androidx.constraintlayout.widget.ConstraintLayout>
    <com.wzy.nesteddetail.view.NestedScrollingDetailContainer>
        
        <!-- WebView 区域 -->
        <com.wzy.nesteddetail.view.NestedScrollingWebView
            android:tag="nested_scroll_webview" />
        
        <!-- 中间不可滑动区域 -->
        <TextView android:text="不可滑动的View" />
        
        <!-- RecyclerView 区域 -->
        <androidx.recyclerview.widget.RecyclerView
            android:tag="nested_scroll_recyclerview" />
            
    </com.wzy.nesteddetail.view.NestedScrollingDetailContainer>
    
    <!-- 悬浮工具栏 -->
    <TextView android:text="快速滑动到评论" />
    
</androidx.constraintlayout.widget.ConstraintLayout>
```

### 2. 关键接口实现

- **NestedScrollingParent2**：外层容器实现，处理子 View 的滑动请求
- **NestedScrollingChild2**：WebView 实现，与父容器协作滑动
- **滑动事件分发**：通过 `dispatchNestedPreScroll` 和 `dispatchNestedScroll` 实现

## 📚 相关文章

本项目的详细实现原理和 NestedScrolling 机制介绍，请参考：
[10分钟带你入门NestedScrolling机制](https://segmentfault.com/a/1190000019272870)

## 🛠️ 技术栈

- **开发语言**：Java
- **最小 SDK**：Android 5.0 (API 21)
- **目标 SDK**：Android 14 (API 34)
- **编译 SDK**：Android 14 (API 34)
- **架构组件**：AndroidX
- **构建工具**：Gradle 7.5

## 📄 许可证

本项目采用 MIT 许可证，详情请查看 [LICENSE](LICENSE) 文件。

## 🤝 贡献

欢迎提交 Issue 和 Pull Request 来改进这个项目！

1. Fork 本仓库
2. 创建你的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交你的修改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开一个 Pull Request

## 📞 联系方式

如果你有任何问题或建议，欢迎通过以下方式联系：

- 提交 [Issue](https://github.com/your-username/Android-NestedDetail/issues)
- 发送邮件到：wzyll1314@gmail.com

---

⭐ 如果这个项目对你有帮助，请给它一个 Star！