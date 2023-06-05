package com.shalj.customviewpractice.view.loading

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.animation.doOnRepeat

class CustomLoadingView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {

    private val defaultAnimatorDuration = 350L

    private lateinit var mShapeView: ShapeView
    private lateinit var mIndicatorView: IndicatorView
    private lateinit var mTextView: AppCompatTextView

    //上升动画 和 缩放动画 的集合
    private lateinit var animatorSet: AnimatorSet

    init {
        initViews()
        initAnimators()
    }

    private fun initViews() {
        orientation = VERTICAL
        gravity = Gravity.CENTER

        mShapeView = ShapeView(context)
        mShapeView.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        addView(mShapeView)

        mIndicatorView = IndicatorView(context)
        mIndicatorView.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        addView(mIndicatorView)

        mTextView = AppCompatTextView(context)
        mTextView.textSize = 14f
        mTextView.setTextColor(Color.LTGRAY)
        mTextView.text = "努力加载中..."
        mTextView.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            setMargins(0, 40, 0, 0)
        }
        addView(mTextView)
    }

    private fun initAnimators() {
        animatorSet = AnimatorSet().apply {
            duration = defaultAnimatorDuration
            interpolator = DecelerateInterpolator()
        }

        val upDownAnimator = ObjectAnimator.ofFloat(mShapeView, "translationY", 1f, -300f).apply {
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }
        val indicatorScaleAnimator = ObjectAnimator.ofFloat(mIndicatorView, "scaleX", 1f, 0.2f).apply {
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }

        var count = 0
        upDownAnimator.doOnRepeat {
            count++
            if (count % 2 == 0) {
                mShapeView.changeShape()
                getShapeViewAnimator().start()
            }
        }
        animatorSet.playTogether(upDownAnimator, indicatorScaleAnimator, getShapeViewAnimator())
        animatorSet.start()
    }

    private fun getShapeViewAnimator(): ObjectAnimator {
        return when (mShapeView.shape) {
            ShapeView.Shape.Rect -> ObjectAnimator.ofFloat(mShapeView, "rotation", 0f, -120f)
            else -> ObjectAnimator.ofFloat(mShapeView, "rotation", 0f, 180f)
        }.apply {
            duration = defaultAnimatorDuration
            interpolator = DecelerateInterpolator()
        }
    }
}