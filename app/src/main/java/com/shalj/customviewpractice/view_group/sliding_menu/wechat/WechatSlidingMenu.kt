package com.shalj.customviewpractice.view_group.sliding_menu.wechat

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import com.shalj.customviewpractice.R
import com.shalj.customviewpractice.logE
import kotlin.math.abs
import kotlin.math.roundToInt

class WechatSlidingMenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr) {

    private lateinit var menuView: View
    private lateinit var contentView: View
    private lateinit var shadowView: View

    //菜单是否打开
    var isMenuOpen = false

    //是否要拦截onTouch事件处理
    private var isIntercepting = false

    //菜单宽度
    private var menuWidth = 0

    //屏幕宽度
    private val screenWidth
        get() = resources.displayMetrics.widthPixels

    //处理快速滑动的手势操作类
    private val gestureDetector = GestureDetector(context, MyGestureListener())

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.WechatSlidingMenu)

        val contentRemainWidth = typedArray.getDimensionPixelSize(
            R.styleable.WechatSlidingMenu_contentRemainWidth,
            100f.dp2px().roundToInt()
        )
        menuWidth = screenWidth - contentRemainWidth
        typedArray.recycle()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        if (changed) {
            scrollTo(menuWidth, 0)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        val container = getChildAt(0) as ViewGroup

        val containerChildCount = container.childCount
        if (containerChildCount != 2) throw IllegalStateException("SlidingMenu的根布局请使用LinearLayout，并且放且只能放两个子View，第一个为Menu，第二个为Content")

        menuView = container.getChildAt(0)
        //把contentView取出来
        contentView = container.getChildAt(1)
        container.removeView(contentView)
        //添加阴影
        val frameLayout = FrameLayout(context)
        frameLayout.addView(contentView)
        shadowView = View(context).apply {
            setBackgroundColor(Color.BLACK)
        }
        frameLayout.addView(shadowView)
        //再把有阴影的布局重新塞回去
        container.addView(frameLayout)

        menuView.changeWidth(menuWidth)

        contentView.changeWidth(screenWidth)

    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)

        menuView.translationX = l * .05f

        val scale = 1 - l / menuWidth.toFloat()
        shadowView.alpha = if (scale >= 0.5) scale - 0.5f else 0f
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        isIntercepting = false
        val currentX = ev?.x ?: 0f
        //如果菜单是打开的状态，点击菜单外的区域关闭菜单并拦截点击事件
        if (isMenuOpen && currentX > menuWidth) {
            isIntercepting = true
            closeMenu()
            return true
        }
        return super.onInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        //符合条件不处理onTouchEvent
        if (isIntercepting) return true

        //如果是快速滑动的话，就把事件消耗掉了，就不用往下调用了
        if (gestureDetector.onTouchEvent(ev)) {
            return true
        }

        when (ev.action) {
            MotionEvent.ACTION_UP -> {
                val currentX = scrollX
                val scale = 1 - currentX / menuWidth.toFloat()
                if (scale > 0.5) {
                    openMenu()
                } else {
                    closeMenu()
                }
                return false
            }
        }
        return super.onTouchEvent(ev)
    }

    fun openMenu() {
        smoothScrollTo(0, 0)
        isMenuOpen = true
    }

    fun closeMenu() {
        smoothScrollTo(menuWidth, 0)
        isMenuOpen = false
    }

    inner class MyGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            logE("MyGestureListener", "x:$velocityX, y:$velocityY")

            if (abs(velocityX) > abs(velocityY)) {
                if (velocityX > 0 && !isMenuOpen) {
                    openMenu()
                    return true
                }

                if (velocityX < 0 && isMenuOpen) {
                    closeMenu()
                    return true
                }
            }

            return super.onFling(e1, e2, velocityX, velocityY)
        }
    }

    private fun View.changeWidth(width: Int) = this.apply {

        val params = layoutParams
        params.width = width
        layoutParams = params
    }

    private fun Float.dp2px() =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, resources.displayMetrics)
}