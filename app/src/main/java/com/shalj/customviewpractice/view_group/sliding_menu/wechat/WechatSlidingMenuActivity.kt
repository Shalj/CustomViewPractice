package com.shalj.customviewpractice.view_group.sliding_menu.wechat

import android.os.Bundle
import android.view.View
import com.shalj.customviewpractice.R
import com.shalj.customviewpractice.base.BaseActivity
import com.shalj.customviewpractice.databinding.ActivitySlidingMenuBinding

class WechatSlidingMenuActivity :
    BaseActivity<ActivitySlidingMenuBinding>(R.layout.activity_sliding_menu) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    fun openMenu(view: View) {
        binding.slidingMenu.openMenu()
    }

    fun closeMenu(view: View) {
        binding.slidingMenu.closeMenu()
    }
}