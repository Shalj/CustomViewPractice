package com.shalj.customviewpractice.view_group.flow_layout

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup

/**
 * flow layout 流式布局
 * */
class FlowLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

    }
}