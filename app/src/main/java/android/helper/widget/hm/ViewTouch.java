package android.helper.widget.hm;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.android.helper.utils.LogUtil;

public class ViewTouch extends View {

    public final String Tag = getClass().getSimpleName();

    public ViewTouch(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(200, 200);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                LogUtil.e(Tag, "down");
                break;

            case MotionEvent.ACTION_MOVE:
                LogUtil.e(Tag, "move");
                break;

            case MotionEvent.ACTION_UP:
                LogUtil.e(Tag, "up");
                break;
        }
        return super.onTouchEvent(event);
    }
}
