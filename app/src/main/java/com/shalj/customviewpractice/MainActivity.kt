package com.shalj.customviewpractice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.shalj.customviewpractice.view.bubble_side_bar.BubbleSideBarActivity
import com.shalj.customviewpractice.view.loading.LoadingActivity
import com.shalj.customviewpractice.view.rader.RadarActivity
import com.shalj.customviewpractice.view.rating.RatingActivity
import com.shalj.customviewpractice.view_group.flow_layout.FlowLayoutActivity
import com.shalj.customviewpractice.view_group.sliding_menu.qq.QQSlidingMenuActivity
import com.shalj.customviewpractice.view_group.sliding_menu.wechat.WechatSlidingMenuActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun goLoading(view: View) {
        startActivity(buildIntent<LoadingActivity>(this))
    }

    fun goRadar(view: View) {
        startActivity(buildIntent<RadarActivity>(this))
    }

    fun goRating(view: View) {
        startActivity(buildIntent<RatingActivity>(this))
    }

    fun goBubbleSideBar(view: View) {
        startActivity(buildIntent<BubbleSideBarActivity>(this))
    }

    fun goFlowLayout(view: View) {
        startActivity(buildIntent<FlowLayoutActivity>(this))
    }

    fun goSlidingMenu(view: View) {
        startActivity(buildIntent<WechatSlidingMenuActivity>(this))
    }

    fun goQQSlidingMenu(view: View) {
        startActivity(buildIntent<QQSlidingMenuActivity>(this))
    }
}

const val EXTRA = "bundle"
inline fun <reified T : AppCompatActivity> buildIntent(
    context: Context,
    bundleBlock: Bundle.() -> Unit = { }
): Intent {
    return Intent(context, T::class.java).apply {
        putExtra(EXTRA, Bundle().apply { bundleBlock() })
    }
}