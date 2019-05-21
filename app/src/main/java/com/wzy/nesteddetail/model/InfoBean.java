package com.wzy.nesteddetail.model;

public class InfoBean {
    public static final int TYPE_TITLE = 1;
    public static final int TYPE_ITEM = 2;

    public int type;
    public String title;
    public String content;

    @Override
    public String toString() {
        return "InfoBean{" +
                "type=" + type +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
