package com.shalj.customviewpractice.view_group.sliding_menu.qq

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.customview.widget.ViewDragHelper

/**
 * 仿QQ侧滑菜单
 * 参考QQ版本：iOS v8.9.58.612
 *
 * @property menuView 菜单 (默认取第一个子View)
 * @property contentView 打开菜单时被覆盖的显示内容（默认取第二个子View）
 * */
class QQSlidingMenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var menuView: View
    private lateinit var contentView: View
    private lateinit var shadowView: View

    private val dragHelper = ViewDragHelper.create(this, object : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return true
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            super.onViewReleased(releasedChild, xvel, yvel)
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return left
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return top
        }
    })

    override fun onFinishInflate() {
        super.onFinishInflate()

        if (childCount != 2) {
            throw IllegalStateException("QQSlidingMenu should have 2 views, the first for the Menu and the second for the content")
        }
        menuView = getChildAt(0)

        contentView = getChildAt(1)

//        //给contentView设置添加阴影布局，总共分3步
//        //1.移除目标View
//        removeView(contentView)
//        //2.在目标View外套一层FrameLayout，并设置一个带背景色的sibling view
//        val frameLayout = FrameLayout(context)
//        frameLayout.addView(contentView)
//        shadowView = View(context).apply {
//            setBackgroundColor(Color.BLACK)
//        }
//        frameLayout.addView(shadowView)
//        //3.把带有目标View的FrameLayout通过addView的方式添加回QQSlidingView
//        addView(frameLayout)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        dragHelper.processTouchEvent(event)
        return true
    }
}