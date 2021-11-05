package com.android.helper.base;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.helper.utils.TextViewUtil;

/**
 * 基类的 VohewHolder
 */
public class BaseVH extends RecyclerView.ViewHolder {

    public BaseVH(@NonNull View itemView) {
        super(itemView);
    }

    /**
     * 设置TextView的文字内容
     *
     * @param textId textView的对象
     * @param value  具体展示的内容
     */
    public void setTextView(int textId, String value) {
        TextView textView = getTextView(textId);
        TextViewUtil.setText(textView, value);
    }

    /**
     * @return 获取imageView对象
     */
    public ImageView getImageView(int imageId) {
        View view = itemView.findViewById(imageId);
        if (view instanceof ImageView) {
            return (ImageView) view;
        } else {
            return null;
        }
    }

    /**
     * @return 获取imageView对象
     */
    public TextView getTextView(int textId) {
        View view = itemView.findViewById(textId);
        if (view instanceof TextView) {
            return (TextView) view;
        } else {
            return null;
        }
    }

    /**
     * @param viewId 对象id
     * @return 获取指定类型的view
     */
    public <T extends View> T getView(int viewId) {
        return (T) itemView.findViewById(viewId);
    }

    /**
     * @param viewId 对象id
     * @return 获取指定类型的ViewGrounp
     */
    public <T extends ViewGroup> T getViewGroup(int viewId) {
        return (T) itemView.findViewById(viewId);
    }
}