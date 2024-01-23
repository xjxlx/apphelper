package com.android.helper.utils;

import android.view.View;
import android.view.ViewGroup;

import com.android.common.utils.ConvertUtil;

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
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
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
            marginLayoutParams.topMargin = ConvertUtil.dpi(view.getContext(), topMargin);
            view.setLayoutParams(marginLayoutParams);
        }
    }

    public static int getMarginEnd(View view) {
        if (view != null) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
                return marginLayoutParams.getMarginEnd();
            }
        }
        return 0;
    }

    public static int getMarginStart(View view) {
        if (view != null) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
                return marginLayoutParams.getMarginStart();
            }
        }
        return 0;
    }

    public static void setLeftMargin(View view, int topMargin) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            marginLayoutParams.leftMargin = ConvertUtil.dpi(view.getContext(), topMargin);
            view.setLayoutParams(marginLayoutParams);
        }
    }

    public static void setRightMargin(View view, int topMargin) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            marginLayoutParams.rightMargin = ConvertUtil.dpi(view.getContext(), topMargin);
            view.setLayoutParams(marginLayoutParams);
        }
    }

    public static void setMarginStart(View view, int marginStart) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            marginLayoutParams.setMarginStart(ConvertUtil.dpi(view.getContext(), marginStart));
            view.setLayoutParams(marginLayoutParams);
        }
    }

    public static void setMarginEnd(View view, int marginEnd) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            marginLayoutParams.setMarginEnd(ConvertUtil.dpi(view.getContext(), marginEnd));
            view.setLayoutParams(marginLayoutParams);
        }
    }

    public static void setMarginLeftPx(View view, int marginLeft) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            marginLayoutParams.leftMargin = marginLeft;
            view.setLayoutParams(marginLayoutParams);
        }
    }

    public static void setMarginTopPx(View view, int marginTop) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            marginLayoutParams.topMargin = marginTop;
            view.setLayoutParams(marginLayoutParams);
        }
    }

    public static void setMarginRightPx(View view, int marginRight) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            marginLayoutParams.rightMargin = marginRight;
            view.setLayoutParams(marginLayoutParams);
        }
    }

    public static void setMarginBottomPx(View view, int marginBottom) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            marginLayoutParams.bottomMargin = marginBottom;
            view.setLayoutParams(marginLayoutParams);
        }
    }

    public static void setMarginStartPx(View view, int marginStart) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            marginLayoutParams.setMarginStart(marginStart);
            view.setLayoutParams(marginLayoutParams);
        }
    }

    public static void setMarginEndPx(View view, int marginEnd) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            marginLayoutParams.setMarginEnd(marginEnd);
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

    /**
     * @param view    指定view
     * @param visible true:可见，false:不可见
     */
    public static void setViewVisible(View view, boolean visible) {
        if (view != null) {
            int visibility = view.getVisibility();
            if (visible) {
                if (visibility != View.VISIBLE) {
                    view.setVisibility(View.VISIBLE);
                }
            } else {
                if (visibility != View.GONE) {
                    view.setVisibility(View.GONE);
                }
            }
        }
    }

    public static int getMarginLeft(View view) {
        if (view != null) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
                return marginLayoutParams.leftMargin;
            }
        }
        return 0;
    }

    public static int getMarginRight(View view) {
        if (view != null) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
                return marginLayoutParams.rightMargin;
            }
        }
        return 0;
    }

    public static int getMarginTop(View view) {
        if (view != null) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
                return marginLayoutParams.topMargin;
            }
        }
        return 0;
    }

    public static int getMarginBottom(View view) {
        if (view != null) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
                return marginLayoutParams.bottomMargin;
            }
        }
        return 0;
    }

    public static void getLocationOnScreen(View view, LocationCallBackListener locationCallBackListener) {
        if (view != null) {
            view.post(() -> {
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                if (locationCallBackListener != null) {
                    locationCallBackListener.onLocation(location);
                }
            });
        }
    }

    public interface LocationCallBackListener {
        void onLocation(int[] location);
    }
}
