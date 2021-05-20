package android.helper.test

import android.helper.R
import android.view.MotionEvent
import android.view.View
import com.android.helper.base.BaseTitleActivity
import kotlinx.android.synthetic.main.activity_test_touch.*

class TestTouchActivity : BaseTitleActivity() {

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
    }


}