package com.android.helper.widget.pagelistview;

import android.app.Activity;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * @author : 流星
 * @CreateDate: 2022/1/5-4:03 下午
 * @Description:
 */
public abstract class PageListAdapter<T> extends BaseAdapter {
    private final Activity mActivity;// 上下文
    protected List<T> mList;// 指定的数据集合

    public PageListAdapter(Activity activity, List<T> list) {
        mActivity = activity;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public T getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
