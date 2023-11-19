package com.android.helper.utils;

import android.text.TextUtils;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.common.utils.LogUtil;
import com.android.helper.interfaces.listener.CallBackListener;

import java.util.List;

/**
 * fragment的工具类
 */
public class FragmentUtil {

    private boolean isHide;// 是否自动隐藏
    private FragmentManager mManager;

    public FragmentUtil(@NonNull FragmentActivity activity) {
        if (activity != null) {
            mManager = activity.getSupportFragmentManager();
        }
    }

    /**
     * @param manager     如果是tabLayout 这种的话，需要使用{@link Fragment#getParentFragmentManager()} ，如果是判断fragment内部的fragment是否可见，
     *                    就需要使用{@link Fragment#getChildFragmentManager()}
     * @param tagFragment 指定的fragment的对象
     * @return 检测指定的fragment是否可见
     */
    public static boolean checkFragmentVisibilityForParent(FragmentManager manager, Fragment tagFragment) {
        boolean isVisibility = false;
        if (tagFragment != null && manager != null) {
            List<Fragment> fragments = manager.getFragments();
            // 只有fragment 子fragment不为空，且数据中包含了指定的fragment的时候，才去检查
            if (fragments.size() > 0) {
                // 这里之所以不去直接返回，是考虑到，只要出现了这种情况，必然还会有其他的页面，为了让其他页面也得到数据的回调，所有设置了遍历
                for (int i = 0; i < fragments.size(); i++) {
                    Fragment fragment = fragments.get(i);
                    if (fragment == tagFragment) {
                        isVisibility = true;
                        break;
                    }
                }
            }
        }
        LogUtil.e("⭐️⭐️⭐️ --->：当前检测的view是否可见：" + isVisibility);
        return isVisibility;
    }

    /**
     * @param hide 是否要隐藏掉其他的fragment，true:隐藏，false:不隐藏，默认是fragment
     * @return 是否要隐藏掉其他的fragment，适用于show 和 hide 的类型
     */
    public FragmentUtil autoHide(boolean hide) {
        this.isHide = hide;
        return this;
    }

    /**
     * @param id       指定替换位置的id
     * @param fragment 需要添加的fragment
     * @param tag      添加时候的tag
     * @param listener 添加fragment的回调
     * @return 添加一个fragment到指定的布局上，并返回添加的结果
     */
    public FragmentUtil add(@IdRes int id, @NonNull Fragment fragment, @NonNull String tag, CallBackListener<Object> listener) {
        boolean success = false;
        try {
            // 如果fragment为空，则停止后续的所有操作
            if (fragment == null) {
                throw new NullPointerException("fragment 不能为空");
            }
            if (mManager != null) {
                FragmentTransaction ft = mManager.beginTransaction();
                // 添加到管理器中
                if (!fragment.isAdded()) {
                    ft.add(id, fragment, tag);
                }
                // 隐藏之前所有的fragment
                if (isHide) {
                    List<Fragment> fragments = mManager.getFragments();
                    if (fragment != null && fragments.size() > 0) {
                        for (Fragment fr : fragments) {
                            if (fr != fragment) {
                                ft.hide(fr);
                            }
                        }
                    }
                }
                // 展示当前的view
                if (fragment.isHidden()) {
                    ft.show(fragment);
                }
                ft.commitAllowingStateLoss();
                success = true;
            }

        } catch (Exception e) {
            success = false;
        }
        if (listener != null) {
            listener.onBack(success, tag, null);
        }
        return this;
    }

    /**
     * @param id       指定替换位置的id
     * @param fragment 需要添加的fragment
     * @param tag      添加时候的tag
     * @param listener 添加fragment的回调
     * @return 添加一个fragment到指定的布局上，并返回添加的结果
     */
    public FragmentUtil replace(@IdRes int id, @NonNull Fragment fragment, @NonNull String tag, CallBackListener<Object> listener) {
        boolean success = false;
        if (mManager != null) {
            try {
                // 如果fragment为空，则停止后续的所有操作
                if (fragment == null) {
                    throw new NullPointerException("fragment 不能为空");
                }
                mManager.beginTransaction()
                        .replace(id, fragment, tag)
                        .commitAllowingStateLoss();
                success = true;
            } catch (Exception e) {
                success = false;
            }
        }
        if (listener != null) {
            listener.onBack(success, tag, null);
        }
        return this;
    }

    /**
     * @param tag 指定的tag
     * @return 根据tag，获取添加的fragment
     */
    public Fragment getFragmentForTag(@NonNull String tag) {
        if (mManager != null) {
            if (!TextUtils.isEmpty(tag)) {
                return mManager.findFragmentByTag(tag);
            }
        }
        return null;
    }

    /**
     * @param fragment 指定的fragment
     * @return 隐藏指定的fragment
     */
    public FragmentUtil hide(@NonNull Fragment fragment) {
        try {
            // 如果fragment为空，则停止后续的所有操作
            if (fragment == null) {
                throw new NullPointerException("fragment 不能为空");
            }
            if (mManager != null) {
                mManager.beginTransaction()
                        .hide(fragment)
                        .commitAllowingStateLoss();
            }
        } catch (Exception ignored) {
        }
        return this;
    }
}
