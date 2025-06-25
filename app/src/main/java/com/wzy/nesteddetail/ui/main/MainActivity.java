package com.wzy.nesteddetail.ui.main;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.wzy.nesteddetail.R;
import com.wzy.nesteddetail.constants.AppConstants;
import com.wzy.nesteddetail.data.model.InfoBean;
import com.wzy.nesteddetail.ui.widget.NestedScrollingDetailContainer;
import com.wzy.nesteddetail.ui.widget.NestedScrollingWebView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private NestedScrollingDetailContainer nestedContainer;
    private NestedScrollingWebView webContainer;
    private RecyclerView rvList;
    private TextView vToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // 初始化视图
        nestedContainer = findViewById(R.id.nested_container);
        webContainer = findViewById(R.id.web_container);
        rvList = findViewById(R.id.rv_list);
        vToolBar = findViewById(R.id.v_tool_bar);
        
        initImmersed();
        initView();
    }

    private void initImmersed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(option);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            ActionBar actionBar = getSupportActionBar();
            actionBar.hide();
        }
    }

    private void initView() {
        initWebView();
        initRecyclerView();
        initToolBarView();
    }

    private void initToolBarView() {
        vToolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nestedContainer.scrollToTarget(rvList);
            }
        });
    }

    private void initWebView() {
        webContainer.getSettings().setJavaScriptEnabled(true);
        webContainer.setWebViewClient(new WebViewClient());
        webContainer.setWebChromeClient(new WebChromeClient());
        webContainer.loadUrl(AppConstants.WebView.DEFAULT_URL);
        if (false) {
            // 测试JS通知内容高度回调
            webContainer.post(new Runnable() {
                @Override
                public void run() {
                    int contentHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
                    webContainer.setJsCallWebViewContentHeight(contentHeight);
                }
            });
        }
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvList.setLayoutManager(layoutManager);
        List<InfoBean> data = getCommentData();
        MainAdapter rvAdapter = new MainAdapter(this, data);
        rvList.setAdapter(rvAdapter);
    }

    private List<InfoBean> getCommentData() {
        List<InfoBean> commentList = new ArrayList<>();
        InfoBean titleBean = new InfoBean();
        titleBean.type = InfoBean.TYPE_TITLE;
        titleBean.title = AppConstants.Data.COMMENT_LIST_TITLE;
        commentList.add(titleBean);
        for (int i = 0; i < AppConstants.Data.DEFAULT_COMMENT_COUNT; i++) {
            InfoBean contentBean = new InfoBean();
            contentBean.type = InfoBean.TYPE_ITEM;
            contentBean.title = AppConstants.Data.COMMENT_TITLE_PREFIX + i;
            contentBean.content = AppConstants.Data.COMMENT_CONTENT_PREFIX + i;
            commentList.add(contentBean);
        }
        return commentList;
    }
}
