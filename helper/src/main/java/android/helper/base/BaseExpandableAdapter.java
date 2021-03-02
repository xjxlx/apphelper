package android.helper.base;

import android.app.Activity;
import android.widget.BaseExpandableListAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * ExpandableListView 的 adapter的基类
 * <p>
 * 1:因为父类下数据，不见得就一定是子类的对象，所以，子类暂时不封装，交由每个实现类去自己处理
 * </p>
 *
 * @param <P> 父类的数据类型
 */
public abstract class BaseExpandableAdapter<P> extends BaseExpandableListAdapter {

    protected Activity mContext;
    protected List<P> mList;  // 父类的集合

    public BaseExpandableAdapter(@NotNull Activity activity) {
        this.mContext = activity;
    }

    public BaseExpandableAdapter(@NotNull Activity activity, @NotNull List<P> list) {
        this.mContext = activity;
        this.mList = list;
    }

    /**
     * 设置数据源
     *
     * @param list 指定的数据源
     */
    public BaseExpandableAdapter(@NotNull List<P> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    // 获取分组的个数
    @Override
    public int getGroupCount() {
        return mList != null ? mList.size() : 0;
    }

    // 孩子在指定的组中计数
//    @Override
//    public int getChildrenCount(int groupPosition) {
//        if (mParentList != null) {
//            C c = (C) mParentList.get(groupPosition);
//            if (r != null) {
//                return r.size();
//            }
//        }
//        return 0;
//    }

    // 获取指定的分组数据
    @Override
    public Object getGroup(int groupPosition) {
        return mList != null ? mList.get(groupPosition) : null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    // 分组和子选项是否持有稳定的ID, 就是说底层数据的改变会不会影响到它们。
    @Override
    public boolean hasStableIds() {
        return true;
    }

    //  指定位置上的子元素是否可选中
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /**
     * @param list 设置数据源
     */
    public void setList(List<P> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

}
