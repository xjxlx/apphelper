package android.helper.utils;

import android.helper.interfaces.listener.CallBackListener;
import android.text.TextUtils;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * Fragment的工具类
 */
public class FragmentUtil {

    private static final String TAG = "FragmentUtil";

    private static FragmentUtil fragmentUtil;
    private Fragment mBeforeFragment;// 上一个fragment
    private final FragmentManager manager;

    private FragmentUtil(@NonNull FragmentActivity fragmentActivity) {
        manager = fragmentActivity.getSupportFragmentManager();
    }

    public static FragmentUtil getInstance(FragmentActivity fragmentActivity) {
        if (fragmentActivity != null) {
            if (fragmentUtil == null) {
                fragmentUtil = new FragmentUtil(fragmentActivity);
            }
        }
        return fragmentUtil;
    }

    /**
     * @param containerViewId 父类的容器id
     * @param fragment        目标的fragment，用于展示
     * @param tag             目标fragment的tag
     */
    public void add(@IdRes int containerViewId, Fragment fragment, @Nullable String tag, CallBackListener<Object> listener) {
        boolean isSuccess = false;
        try {
            // 如果fragment为空，则停止后续的所有操作
            if (fragment == null) {
                throw new NullPointerException("fragment 不能为空");
            }

            if (manager != null) {
                FragmentTransaction transaction = manager.beginTransaction();

                if (mBeforeFragment != null) {
                    if (mBeforeFragment != fragment) {
                        if (mBeforeFragment.isVisible()) {
                            transaction.hide(mBeforeFragment); // fragment如果不相同，则隐藏上一个的fragment
                        }
                    }
                }

                if (fragment.isAdded()) { // 如果fragment已经添加过了
                    // 展示目标的fragment
                    if (!fragment.isVisible()) {
                        transaction.show(fragment);
                    }
                } else { // fragment没有添加过
                    // 判断是否使用tag
                    if (TextUtils.isEmpty(tag)) {
                        // 使用tag的情况
                        transaction.add(containerViewId, fragment);
                    } else {
                        // 不使用tag的情况
                        transaction.add(containerViewId, fragment, tag);
                    }
                }

                transaction.commitAllowingStateLoss();

                mBeforeFragment = fragment;
                LogUtil.e(TAG, "fragment添加成功：" + fragment.toString());
                isSuccess = true;
            }
        } catch (Exception e) {
            isSuccess = false;
            LogUtil.e(TAG, "fragment添加失败 --->Error:" + e.getMessage());
        }

        if (listener != null) {
            listener.onBack(isSuccess, tag, "");
        }
    }

    /**
     * @param containerViewId fragment的容器
     * @param fragment        目标的fragment，用于展示
     */
    public void add(@IdRes int containerViewId, @NonNull Fragment fragment, CallBackListener<Object> listener) {
        add(containerViewId, fragment, "", listener);
    }

    /**
     * @param fragment 隐藏一个fragment
     */
    public void hide(@NonNull Fragment fragment, CallBackListener<Object> listener) {
        boolean isSuccess = false;
        try {
            if (fragment.isVisible()) {
                if (manager != null) {
                    FragmentTransaction transaction =
                            manager
                                    .beginTransaction()
                                    .hide(fragment);

                    transaction.commitAllowingStateLoss();
                    isSuccess = true;
                }
            }
        } catch (Exception e) {
            isSuccess = false;
        }

        if (listener != null) {
            listener.onBack(isSuccess, "", "");
        }
    }

}
