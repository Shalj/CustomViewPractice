package com.shalj.customviewpractice

import android.util.Log
import android.view.View.MeasureSpec

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