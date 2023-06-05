package com.shalj.customviewpractice.view_group.flow_layout

import android.os.Bundle
import com.shalj.customviewpractice.R
import com.shalj.customviewpractice.base.BaseActivity
import com.shalj.customviewpractice.databinding.ActivityFlowLayoutBinding

class FlowLayoutActivity : BaseActivity<ActivityFlowLayoutBinding>(R.layout.activity_flow_layout) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupStatusBar(fitsSystemWindow = false)
    }
}