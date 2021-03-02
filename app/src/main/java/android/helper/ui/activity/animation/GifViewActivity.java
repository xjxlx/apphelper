package android.helper.ui.activity.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import android.helper.R;
import android.helper.base.BaseTitleActivity;
import android.helper.utils.LogUtil;
import android.helper.utils.ScreenUtil;
import android.helper.utils.ToastUtil;

/**
 * 自定义gifView
 */
public class GifViewActivity extends BaseTitleActivity {

    private ImageView mIvBg;
    private ImageView mIvAnimation;
    private boolean isStart;
    private ObjectAnimator animator;

    @Override
    protected int getTitleLayout() {
        return R.layout.activity_gif_view;
    }

    @Override
    protected void initView() {
        super.initView();
        mIvBg = findViewById(R.id.iv_bg);
        mIvAnimation = findViewById(R.id.iv_animation);
    }

    @Override
    protected void initData() {
        super.initData();
        setTitleContent("自定义GIF动画");

        setAnimation(0, 917);

        ViewTreeObserver viewTreeObserver = mIvAnimation.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mIvAnimation.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int width = mIvAnimation.getWidth();

                int screenWidth = ScreenUtil.getScreenWidth(mContext);
                int toX = screenWidth - width;
                LogUtil.e("width:" + toX);
            }
        });
    }

    int count = 0;

    public void setAnimation(int start, int end) {

        animator = ObjectAnimator.ofFloat(mIvAnimation, "translationX", start, end);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(3 * 1000);
        animator.setRepeatMode(ObjectAnimator.REVERSE);
        animator.setRepeatCount(10);
        animator.addUpdateListener(animation -> LogUtil.e("animation------->" + animation.getAnimatedValue()));
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                count += 1;
                if (!isStart) {
                    mIvAnimation.setRotationY(-180);
                    isStart = true;
                } else {
                    mIvAnimation.setRotationY(0);
                    isStart = false;
                }
                if (count == 3) {
                    setAnimationTransparent();
                }

            }
        });

        animator.start();
    }

    private void setAnimationTransparent() {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator animatorAlpha = ObjectAnimator.ofFloat(mIvAnimation, "alpha", 1, 0);
        set.playTogether(animatorAlpha);
        set.setDuration(3 * 1000);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ToastUtil.show("动画结合执行结束！");
                if (animator != null) {
                    animator.cancel();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        set.start();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (animator != null) {
            animator.cancel();
        }
    }

}
