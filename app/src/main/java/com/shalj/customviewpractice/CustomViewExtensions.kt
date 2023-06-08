package com.shalj.customviewpractice

import android.graphics.Outline
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewOutlineProvider
import androidx.databinding.BindingAdapter

/**
 * 自定义View常用的一些方法，用扩展封装起来
 * */

//获取宽/高的模式
val Int.mode
    get() = MeasureSpec.getMode(this)

//获取宽/高的具体尺寸
val Int.size
    get() = MeasureSpec.getSize(this)

fun logE(key: String, message: Any) {
    Log.e(key, message.toString())
}

/**
 * 给view设置圆角
 * */
fun View.setCorner(dp: Int) {
    val corner = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        resources.displayMetrics
    )
    clipToOutline = true
    outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline?) {
            outline?.setRoundRect(0, 0, view.width, view.height, corner)
        }
    }
}