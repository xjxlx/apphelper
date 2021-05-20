package android.helper.widget.hm;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.helper.utils.LogUtil;

public class ViewGroupTouch extends RelativeLayout {
    public final String Tag = getClass().getSimpleName();

    public ViewGroupTouch(@NonNull Context context) {
        super(context);
    }

    public ViewGroupTouch(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                LogUtil.e(Tag, "dispatchTouchEvent--->down");
                break;

            case MotionEvent.ACTION_MOVE:
                LogUtil.e(Tag, "dispatchTouchEvent--->move");
                break;

            case MotionEvent.ACTION_UP:
                LogUtil.e(Tag, "dispatchTouchEvent--->up");
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                LogUtil.e(Tag, "onTouchEvent--->down");
                break;

            case MotionEvent.ACTION_MOVE:
                LogUtil.e(Tag, "onTouchEvent--->move");
                break;

            case MotionEvent.ACTION_UP:
                LogUtil.e(Tag, "onTouchEvent--->up");
                break;
        }
        return super.onTouchEvent(event);
    }
}
