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
     * @param textId
     * @param value
     */
    public void setTextView(int textId, String value) {
        if (itemView != null) {
            TextView textView = itemView.findViewById(textId);
            TextViewUtil.setText(textView, value);
        }
    }

    /**
     * @param imageId
     * @return 获取imageView对象
     */
    public ImageView getImageView(int imageId) {
        if (itemView != null) {
            ImageView imageView = itemView.findViewById(imageId);
            if (imageView != null) {
                return imageView;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * @param textId
     * @return 获取imageView对象
     */
    public TextView getTextView(int textId) {
        if (itemView != null) {
            TextView textView = itemView.findViewById(textId);
            if (textView != null) {
                return textView;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * @param viewId 对象id
     * @return 获取指定类型的view
     */
    public <T extends View> T getView(int viewId) {
        if (itemView != null) {
            return (T) itemView.findViewById(viewId);
        } else {
            return null;
        }
    }

    /**
     * @param viewId 对象id
     * @return 获取指定类型的ViewGrounp
     */
    public <T extends ViewGroup> T getViewGrounp(int viewId) {
        if (itemView != null) {
            return (T) itemView.findViewById(viewId);
        } else {
            return null;
        }
    }

}