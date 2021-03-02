package android.helper.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import android.helper.interfaces.listener.OnItemClickListener;
import android.helper.utils.LogUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 随机生成一个View
 */
public class RandomLayout extends ViewGroup {

    private List<String> mRandomDataList; // 随机布局的内容数据
    private TextView mTextView = new TextView(getContext()); // textView的模板
    private List<TextView> mRandomViewList;// view的集合
    private final List<TextView> mTemporaryViewList = new ArrayList<>();// 临时存放生成view的集合

    private Random mRandom; // 随机数
    private int mLeft, mTop, mRight, mBottom;// 整个view真实可用的区域
    private int mLoopCount;// 默认如果超过500次就开始重新轮训
    private boolean mLoopFlag;// 循环的标记
    private int mRadioWidth;
    private int mRadioHeight;
    private boolean mIsRatioView;// 是要旋转view
    private OnItemClickListener<String> mClickListener; // 事件的回调
    private int measuredWidth;
    private int measuredHeight;

    public RandomLayout(Context context) {
        super(context);
        init(context, null);
    }

    public RandomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mRandom = new Random();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        measuredWidth = getMeasuredWidth();
        measuredHeight = getMeasuredHeight();
    }

    public void setDataList(List<String> list) {
        if (list != null && list.size() > 0) {
            // 重新设置循环的次数
            mLoopCount = 0;
            // 设置数据的集合
            this.mRandomDataList = list;
            // 重新设置循环标记
            mLoopFlag = false;
            // 清空所有的view
            this.removeAllViews();
            // 清空临时的view集合
            this.mTemporaryViewList.clear();

            // 存放view的集合
            if (this.mRandomViewList == null) {
                this.mRandomViewList = new ArrayList<>();
            } else {
                mRandomViewList.clear();
            }

            // 把view临时存放到一个集合中去
            for (String content : list) {
                TextView textView = getTextView();
                textView.setText(content);
                mTemporaryViewList.add(textView);
            }
            invalidate();
        }
    }

    /**
     * @return 获取一个标准的textView模板，可以对textView进行各种设置
     */
    public void setTextView(@NotNull TextView templateTextView) {
        this.mTextView = templateTextView;
    }

    public TextView getTextView() {
        TextView textView = new TextView(getContext());
        textView.setTextSize(mTextView.getTextSize());
        textView.setTextColor(mTextView.getTextColors());
        textView.setGravity(mTextView.getGravity());
        textView.setTypeface(mTextView.getTypeface());
        return textView;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        LogUtil.e(" --- > onLayout  <----");
        this.mLeft = l + getPaddingLeft();
        this.mTop = t + getPaddingTop();
        this.mRight = (r - getPaddingRight());
        this.mBottom = (b - getPaddingBottom());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        LogUtil.e(" --- > onDraw  <----");

        post(() -> {
            LogUtil.e("整个布局的位置：left:" + mLeft + "  top: " + mTop + "  right:" + mRight + "  bottom:" + mBottom);

            for (int i = 0; i < mTemporaryViewList.size(); i++) {
                TextView child = mTemporaryViewList.get(i);

                // 先测量子View的大小
                int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.AT_MOST);//为子View准备测量的参数
                int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.AT_MOST);
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

                // 子View测量之后的宽和高
                int childW = child.getMeasuredWidth();
                int childH = child.getMeasuredHeight();

                mRadioWidth = 20;
                mRadioHeight = 20;

                while ((!mRandomViewList.contains(child)) && (!mLoopFlag)) {
                    ++mLoopCount;
                    if (mLoopCount > 500) {
                        mLoopFlag = true;
                    }
                    // 随机一个新的坐标系
                    int[] viewLocal = getViewLocal(childW, childH);
                    int newX = viewLocal[0]; // x轴坐标
                    int newY = viewLocal[1];

                    // view 交集的结果
                    boolean overlap = isOverlap(newX, newY, childW + mRadioWidth, childH + mRadioHeight);
                    LogUtil.e("新产生的view是否会和其他的view相交叉：" + overlap);
                    // 随机数的X坐标 + view的宽，是否小于 整体布局的右侧边距
                    boolean lessRight = (newX + childW + mRadioWidth) < (mRight + mRadioWidth); // 此处多加了一个最小的宽度，避免两个view左右挨得太近
                    // 随机数的Y轴坐标 + view的高度 ，是否小于布局的底部
                    boolean lessBottom = (newY + childH + mRadioHeight) < (mBottom + mRadioHeight);// 此处多加了一个最小的高度，避免两个view上下挨得太近
                    /*
                     * 只有同时满足三个条件才去添加view
                     * 1：新生成的随机view 不能和之前的view 交集
                     * 2：新生成的view右侧不能大于布局的最右侧
                     * 3：新生成的view底部不能大于布局的最下方
                     */
                    if ((!overlap) && (lessRight) && (lessBottom)) {
                        // 添加一个view，
                        if (child.getParent() != null) {
                            removeView(child);
                        }
                        addView(child);
                        // 标记
                        mRandomViewList.add(child);

                        // 改变位置
                        child.layout(newX, newY, (newX + childW + mRadioWidth), (newY + childH + mRadioHeight));
                        LogUtil.e("成功添加了view ！");

                        // 设置view的旋转
                        if (mIsRatioView) {
                            int randomAngle = getAngleValue(5, -5);
                            LogUtil.e("随机的角度为：" + randomAngle);
                            child.setRotation(randomAngle);
                        }

                        // 设置点击事件
                        child.setOnClickListener(v -> {
                            if (mClickListener != null) {
                                mClickListener.onItemClick(child, 0, child.getText().toString());
                            }
                        });

                    } else {
                        LogUtil.e("交叉：" + overlap + "  lessRight：" + lessRight + "  lessBottom：" + lessBottom);
                    }
                }
                if (mLoopFlag) {
                    // 重新开始计算所有
                    LogUtil.e("---> 重新开始执行流程！");
                    setDataList(mRandomDataList);
                }
            }
        });
    }

    /**
     * @return 获取一个范围内的随机数组 xy【0】 获取 X轴的随机数  xy【1】 获取Y轴随机数
     */
    private int[] getViewLocal(int width, int height) {
        return createXY(width, height);
    }

    /**
     * @param newX   随机的X轴
     * @param newY   随机的Y轴
     * @param width  view的宽
     * @param height view的高
     * @return 判断新生成的view，是否和之前生成的view 相交，如果相交就返回true，否则就返回false
     */
    private boolean isOverlap(int newX, int newY, int width, int height) {
        boolean isOverlap = false;
        int childCount = getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                TextView child = (TextView) getChildAt(i);
                int left = child.getLeft();
                int right = child.getRight();
                int top = child.getTop();
                int bottom = child.getBottom();

                // 之前添加的view
                Rect rect = new Rect(left, top, right, bottom);

                // 随机生成的view
                Rect rect1 = new Rect((newX + mRadioWidth), (newY + mRadioHeight), (newX + width + mRadioWidth), (newY + height + mRadioHeight));
                // 交集的结果
                isOverlap = Rect.intersects(rect, rect1);

                if (isOverlap) {
                    isOverlap = true;
                    break; // 停止整个轮训
                }
            }
        } else {
            isOverlap = false;
        }
        return isOverlap;

    }

    /**
     * @param min 最小值
     * @param max 最大值
     * @return 生成指定范围内的随机数
     */
    private int getRandomPosition(int min, int max) {
        if (max < 0) {
            max = max * -1;
        }
        return mRandom.nextInt(max) % (max - min + 1) + min;
    }

    /**
     * 是否要随机旋转view
     */
    public void setRandomRotatingView(boolean isRotatingView) {
        this.mIsRatioView = isRotatingView;
    }

    private int getAngleValue(int max, int min) {
        return mRandom.nextInt(max - min + 1) + min;
    }

    /**
     * 点击事件的回调方法
     */
    public void setRandomClickListener(OnItemClickListener<String> clickListener) {
        this.mClickListener = clickListener;
    }

    /**
     * 根据传入的宽和高返回一个随机的坐标!
     */
    private int[] createXY(int width, int height) {
        int[] xyRet = new int[]{0, 0};
        // 注意，要减去内部填充!!!
        int i = measuredWidth - (width + getPaddingLeft() + getPaddingRight());
        if (i > 0) {
            xyRet[0] = mRandom.nextInt(i);
        } else {
            xyRet[0] = mRandom.nextInt(width);
        }

        int i1 = measuredHeight - (height + getPaddingBottom() + getPaddingTop());
        if (i1 > 0) {
            xyRet[1] = mRandom.nextInt(i1);
        } else {
            xyRet[1] = mRandom.nextInt(height);
        }
        return xyRet;
    }

}
