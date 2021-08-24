package com.android.helper.base;

import android.app.Activity;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * ListView类型的基类，适用于ListView 和 GridView
 * <p>
 * 使用说明：
 * 1: 必须写一个静态的ViewHolder
 * 2：重写getView（）方法
 * </p>
 *
 * @param <T> 数据的类型
 */
public abstract class BaseListAdapter<T> extends BaseAdapter {

    protected Activity mContext;
    protected ArrayList<T> mList;

    public BaseListAdapter(Activity activity, ArrayList<T> list) {
        this.mContext = activity;
        this.mList = list;
    }

    public BaseListAdapter(Activity activity) {
        this.mContext = activity;
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        if (mList != null && mList.size() > 0) {
            return mList.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 重新设置数据
     *
     * @param list 数据源
     */
    public void setList(ArrayList<T> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

}
