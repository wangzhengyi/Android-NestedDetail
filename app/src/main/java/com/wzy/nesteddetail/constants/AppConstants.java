package com.wzy.nesteddetail.constants;

/**
 * 应用常量类
 * 统一管理应用中使用的常量
 */
public class AppConstants {
    
    /**
     * WebView相关常量
     */
    public static class WebView {
        // 默认加载的URL
        public static final String DEFAULT_URL = "https://github.com/wangzhengyi/Android-NestedDetail";
        // JavaScript接口名称
        public static final String JS_INTERFACE_NAME = "AndroidInterface";
    }
    
    /**
     * UI相关常量
     */
    public static class UI {
        // 默认动画持续时间
        public static final int DEFAULT_ANIMATION_DURATION = 300;
        // 滑动阈值
        public static final int SCROLL_THRESHOLD = 50;
    }
    
    /**
     * 数据相关常量
     */
    public static class Data {
        // 默认评论数量
        public static final int DEFAULT_COMMENT_COUNT = 40;
        // 评论标题前缀
        public static final String COMMENT_TITLE_PREFIX = "评论标题";
        // 评论内容前缀
        public static final String COMMENT_CONTENT_PREFIX = "评论内容";
        // 评论列表标题
        public static final String COMMENT_LIST_TITLE = "评论列表";
    }
    
    /**
     * 标签常量
     */
    public static class Tags {
        // NestedScrolling WebView标签
        public static final String NESTED_SCROLL_WEBVIEW = "nested_scroll_webview";
        // NestedScrolling RecyclerView标签
        public static final String NESTED_SCROLL_RECYCLERVIEW = "nested_scroll_recyclerview";
    }
}