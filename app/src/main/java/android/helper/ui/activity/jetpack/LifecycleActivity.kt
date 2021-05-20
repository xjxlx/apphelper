package android.helper.ui.activity.jetpack

import android.helper.R
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.android.helper.base.BaseTitleActivity
import com.android.helper.utils.LogUtil

/**
 * lifecycle:【laɪf'saɪkəl】
 * owner:【ˈəʊnə(r)】---欧拿
 * 目标：
 *      测试lifecycle的整体使用流程
 * 逻辑：
 *      1：明确lifecycle的原理：
 *          1：lifecycle是用来感知activity和fragment的生命周期的
 *          2：lifecycle:主要是使用了两个类：LifecycleOwner（被观察者）和 LifecycleObserver（观察者）
 *          3：
 */
class LifecycleActivity : BaseTitleActivity() {

    override fun getTitleLayout(): Int {
        return R.layout.activity_lifecycle
    }

    override fun initView() {
        super.initView()
        setTitleContent("Lifecycle的测试")
    }

    override fun initData() {
        super.initData()

        lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                val name = event.name

                LogUtil.e("name:" + name)
            }
        })
    }

}