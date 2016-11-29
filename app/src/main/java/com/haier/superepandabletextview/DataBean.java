package com.haier.superepandabletextview;

/**
 * Created by Harry.Kong on 2016/11/26.
 */
public class DataBean {

    String text;
    boolean isCollapsed=true;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCollapsed() {
        return isCollapsed;
    }

    public void setCollapsed(boolean collapsed) {
        isCollapsed = collapsed;
    }
}
