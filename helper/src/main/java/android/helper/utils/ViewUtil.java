package android.helper.utils;

import android.view.View;
import android.view.ViewGroup;

public class ViewUtil {

    /**
     * 设置view的margin的数据
     *
     * @param view  view的对象，一般都是viewGrounp
     * @param array 左上右下的顺序
     */
    public static void setMargin(View view, int[] array) {
        if (view == null) {
            return;
        }
        if ((array == null) || (array.length != 4)) {
            return;
        }

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(layoutParams);
            int left = array[0];
            int top = array[1];
            int right = array[2];
            int bottom = array[3];

            marginLayoutParams.setMargins(left, top, right, bottom);
            view.setLayoutParams(marginLayoutParams);
        }
    }

    /**
     * @param view   指定的view
     * @param bottom 需要设置的bottom的高度，单位是dp
     */
    public static void setBottomMargin(View view, int bottom) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            marginLayoutParams.bottomMargin = bottom;
            view.setLayoutParams(marginLayoutParams);
        }
    }

    public static void setTopMargin(View view, int topMargin) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            marginLayoutParams.topMargin = (int) ConvertUtil.toDp(topMargin);
            view.setLayoutParams(marginLayoutParams);
        }
    }

    /**
     * 设置view的状态
     *
     * @param view       view
     * @param visibility 状态
     */
    public static void setVisibility(View view, int visibility) {
        if (view != null) {
            int viewVisibility = view.getVisibility();
            if (visibility != viewVisibility) {
                view.setVisibility(visibility);
            }
        }
    }

}
