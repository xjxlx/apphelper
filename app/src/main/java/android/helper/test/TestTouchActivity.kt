package android.helper.test

import android.helper.R
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.MotionEvent
import android.view.View
import com.android.helper.base.BaseTitleActivity
import com.android.helper.utils.LogUtil
import com.android.helper.utils.ResourceUtil
import kotlinx.android.synthetic.main.activity_test_touch.*

class TestTouchActivity : BaseTitleActivity() {

    val Tag = javaClass.simpleName

    override fun getTitleLayout(): Int {
        return R.layout.activity_test_touch
    }

    override fun initView() {
        super.initView()
        setTitleContent("测试事件分发")
    }

    override fun initListener() {
        super.initListener()

        rl_root.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                return false;
            }
        })

        tv_text.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                return false;
            }
        })

        val thread = object : Thread() {
            override fun run() {
                super.run()
                Looper.prepare()
                Looper.loop()

                val mHandler = object : Handler() {
                    override fun handleMessage(msg: Message?) {
                        super.handleMessage(msg)
                        tv_text.setBackgroundColor(ResourceUtil.getColor(R.color.blue_1))
                    }
                };

                mHandler.sendEmptyMessage(111)

            }
        }
        thread.start()

    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> LogUtil.e(Tag, "dispatchTouchEvent--->down")
            MotionEvent.ACTION_MOVE -> LogUtil.e(Tag, "dispatchTouchEvent--->move")
            MotionEvent.ACTION_UP -> LogUtil.e(Tag, "dispatchTouchEvent--->up")
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> LogUtil.e(Tag, "onTouchEvent--->down")
            MotionEvent.ACTION_MOVE -> LogUtil.e(Tag, "onTouchEvent--->move")
            MotionEvent.ACTION_UP -> LogUtil.e(Tag, "onTouchEvent--->up")
        }
        return super.onTouchEvent(event)
    }

}