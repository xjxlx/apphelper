package android.helper.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.helper.base.BaseVH;

/**
 * RecycleView 的工具类
 */
public class RecycleUtil {

    @SuppressLint("StaticFieldLeak")
    private static RecycleUtil util;
    private final Context mContext;
    private final RecyclerView view;

    private RecycleUtil(Context mContext, RecyclerView view) {
        this.mContext = mContext;
        this.view = view;
    }

    public static RecycleUtil getInstance(Context context, RecyclerView view) {
        util = new RecycleUtil(context, view);
        return util;
    }

    /**
     * @param itemDecoration 分割线
     * @return 设置分割线
     */
    public RecycleUtil setDivider(RecyclerView.ItemDecoration itemDecoration) {
        if ((view != null) && (itemDecoration != null)) {
            view.addItemDecoration(itemDecoration);
        }
        return util;
    }

    /**
     * @return 设置竖向的线性布局
     */
    public RecycleUtil setVertical() {
        LinearLayoutManager manager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        if (view != null) {
            view.setLayoutManager(manager);
        }
        return util;
    }

    /**
     * @param rowCount 需要显示的行数
     * @return 设置recycleView 为多行显示
     */
    public RecycleUtil setGridLayout(int rowCount, @RecyclerView.Orientation int orientation) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, rowCount,orientation,false);
        if (view != null) {
            view.setLayoutManager(gridLayoutManager);
        }
        return util;
    }

    /**
     * @return 设置横向的线性布局
     */
    public RecycleUtil setHorizontal() {
        LinearLayoutManager manager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        if (view != null) {
            view.setLayoutManager(manager);
        }
        return util;
    }

    /**
     * 设置Adaptrer
     *
     * @param adapter 需要给recycleView设置的adapter
     */
    public void setAdapter(RecyclerView.Adapter<?> adapter) {
        if (adapter != null && view != null) {
            view.setAdapter(adapter);
        }
    }

    /**
     * @return 解决数据加载完成后, 没有停留在顶部的问题
     */
    public RecycleUtil setFocusable() {
        //解决数据加载完成后, 没有停留在顶部的问题
        if (view != null) {
            view.setFocusable(false);
        }
        return util;
    }

    public RecycleUtil setDataHeight() {
        //解决数据加载不完的问题
        if (view != null) {
            view.setNestedScrollingEnabled(false);
            view.setHasFixedSize(true);
        }
        return util;
    }

    /**
     * 销毁内存
     */
    public static void clear() {
        if (util != null) {
            util = null;
        }
    }
}
