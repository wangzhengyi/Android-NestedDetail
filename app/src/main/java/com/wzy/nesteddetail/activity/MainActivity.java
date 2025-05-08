package com.wzy.nesteddetail.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.wzy.nesteddetail.adapter.RvAdapter;
import com.wzy.nesteddetail.databinding.ActivityMainBinding;
import com.wzy.nesteddetail.model.InfoBean;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

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
        mainBinding.vToolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainBinding.nestedContainer.scrollToTarget(mainBinding.rvList);
            }
        });
    }

    private void initWebView() {
        mainBinding.webContainer.getSettings().setJavaScriptEnabled(true);
        mainBinding.webContainer.setWebViewClient(new WebViewClient());
        mainBinding.webContainer.setWebChromeClient(new WebChromeClient());
        mainBinding.webContainer.loadUrl("https://github.com/wangzhengyi/Android-NestedDetail");
        if (false) {
            // 测试JS通知内容高度回调
            mainBinding.webContainer.post(new Runnable() {
                @Override
                public void run() {
                    int contentHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
                    mainBinding.webContainer.setJsCallWebViewContentHeight(contentHeight);
                }
            });
        }
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mainBinding.rvList.setLayoutManager(layoutManager);
        List<InfoBean> data = getCommentData();
        RvAdapter rvAdapter = new RvAdapter(this, data);
        mainBinding.rvList.setAdapter(rvAdapter);
    }

    private List<InfoBean> getCommentData() {
        List<InfoBean> commentList = new ArrayList<>();
        InfoBean titleBean = new InfoBean();
        titleBean.type = InfoBean.TYPE_TITLE;
        titleBean.title = "评论列表";
        commentList.add(titleBean);
        for (int i = 0; i < 40; i++) {
            InfoBean contentBean = new InfoBean();
            contentBean.type = InfoBean.TYPE_ITEM;
            contentBean.title = "评论标题" + i;
            contentBean.content = "评论内容" + i;
            commentList.add(contentBean);
        }
        return commentList;
    }
}
