package com.android.helper.utils;

import android.widget.ExpandableListView;

/**
 * ExpandableListView 的工具类
 */
public class ExpandableUtil {

    /**
     * @param view 隐藏左侧组的开关按钮
     */
    public static void hideGroupIndicator(ExpandableListView view) {
        if (view != null) {
            view.setGroupIndicator(null);
        }
    }

    /**
     * 打开自身，关闭其他
     */
    public static void openSelfCloseOther(ExpandableListView view) {
        view.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            // 如果不是在打开的状态下
            if (!parent.isGroupExpanded(groupPosition)) {
                // 关闭其他的组
                for (int i = 0; i < view.getCount(); i++) {
                    if (i != groupPosition) {
                        view.collapseGroup(i);
                    } else {
                        // 打开自己
                        parent.expandGroup(groupPosition, false);

                        // 滑动到当前group的最订单
                        int h1 = parent.getHeight();
                        int h2 = parent.getHeight();
                        parent.smoothScrollToPositionFromTop(groupPosition, h1 / 2 - h2 / 2, 50);
                    }
                }
            }
            return true;
        });
    }

    /**
     * 设置默认打开的组，使用的时候必须先设置adapter的数据
     *
     * @param view          ExpandableListView的对象
     * @param groupPosition 需要打开的组
     */
    public static void openCurrent(ExpandableListView view, int groupPosition) {
        if ((view != null) && (groupPosition >= 0)) {
            view.expandGroup(groupPosition);
        }
    }

}
