package com.wzy.nesteddetail.ui.main;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wzy.nesteddetail.R;
import com.wzy.nesteddetail.data.model.InfoBean;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter {
    private List<InfoBean> mData;
    private LayoutInflater mInflater;

    public MainAdapter(Context context, List<InfoBean> datas) {
        this.mData = datas;
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case InfoBean.TYPE_ITEM:
                return new ContentHolder(mInflater.inflate(R.layout.item_content, viewGroup, false));
            case InfoBean.TYPE_TITLE:
                return new TitleHolder(mInflater.inflate(R.layout.item_title, viewGroup, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        InfoBean infoBean = mData.get(i);
        int viewType = getItemViewType(i);

        switch (viewType) {
            case InfoBean.TYPE_ITEM:
                ContentHolder contentHolder = (ContentHolder) viewHolder;
                contentHolder.tvTitle.setText(infoBean.title);
                contentHolder.tvContent.setText(infoBean.content);
                break;
            case InfoBean.TYPE_TITLE:
                TitleHolder titleHolder = (TitleHolder) viewHolder;
                titleHolder.tvTitle.setText(infoBean.title);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).type;
    }

    public static class TitleHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;

        TitleHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
        }
    }

    public static class ContentHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvContent;

        ContentHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvContent = itemView.findViewById(R.id.tv_content);
        }
    }
}
