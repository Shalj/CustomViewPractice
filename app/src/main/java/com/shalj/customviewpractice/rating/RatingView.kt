package com.shalj.customviewpractice.rating

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.widget.LinearLayout
import com.shalj.customviewpractice.R
import com.shalj.customviewpractice.logE
import com.shalj.customviewpractice.mode
import com.shalj.customviewpractice.size
import kotlin.math.roundToInt

/**
 * 评分控件
 * 可自定义属性
 * starSize 星星大小
 * starCount 星星数量
 * totalScore 总分多少
 * */
class RatingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    //星星数量
    private var starCount = 5

    //星星尺寸
    private var starSize = 30

    //星星填满后的颜色
    var starFilledColor = Color.parseColor("#ff8c1a")

    //星星默认颜色
    var starColor = Color.parseColor("#666666")

    //星星间距
    private var starPadding = 0

    //总分数
    private var totalScore = 10

    //每颗星代表的平均分数
    private var averageScore = 0f

    //当前的分数
    var currentScore = 0f
        set(value) {
            field = value.coerceAtMost(totalScore.toFloat()).coerceAtLeast(0f)
            onScoreChangedListener?.onScoreChanged(currentScore)
            changeStarViewState()
        }

    //分数变化监听
    private var onScoreChangedListener: OnScoreChangedListener? = null

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
        initParams(attrs)
        initStars()
    }

    private fun initParams(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatingView)

        starSize = typedArray.getDimensionPixelSize(
            R.styleable.RatingView_ratingStarSize,
            starSize.dp2px().roundToInt()
        )
        starPadding = typedArray.getDimensionPixelSize(R.styleable.RatingView_ratingStarPadding, 0)
        starCount = typedArray.getInteger(R.styleable.RatingView_ratingStarCount, 5)
        starColor = typedArray.getColor(R.styleable.RatingView_ratingStarColor, starColor)
        starFilledColor =
            typedArray.getColor(R.styleable.RatingView_ratingStarFilledColor, starFilledColor)
        totalScore = typedArray.getInteger(R.styleable.RatingView_ratingTotalScore, 10)

        typedArray.recycle()

        averageScore = totalScore / starCount.toFloat()
    }

    private fun initStars() {
        repeat(starCount.size) {
            addView(StarView(context).apply {
                val params = LayoutParams(starSize, starSize)
                params.setMargins(0, 0, if (it == starCount.size - 1) 0 else starPadding, 0)
                layoutParams = params
                starColor = this@RatingView.starColor
                starFilledColor = this@RatingView.starFilledColor
            })
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var width = widthMeasureSpec.size
        //如果宽度是wrap_content的话，宽度 = 星星数量 * (星星尺寸 + (星星数量 - 1) * 星星间距) + paddingLeft + paddingRight
        if (widthMeasureSpec.mode == MeasureSpec.AT_MOST) {
            width =
                starCount * starSize + (starCount - 1) * starPadding + paddingLeft + paddingRight
        }
        logE("RatingView", "width:$width")
        //高度取星星尺寸和LinearLayout的最大值
        val height = height.size.coerceAtLeast(starSize)
        setMeasuredDimension(width, height)
    }

    var lastClickTime = 0L

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                lastClickTime = System.currentTimeMillis()
            }

            MotionEvent.ACTION_UP -> {
                logE(
                    "RatingView",
                    "System.currentTimeMillis() - lastClickTime:${System.currentTimeMillis() - lastClickTime}"
                )
                if (System.currentTimeMillis() - lastClickTime < 200) {
                    val moveX = event.x - paddingLeft
                    //手指当前划动位置的子View下标
                    val index = (moveX / starSize).toInt()
                    if (index > childCount - 1) return false

                    repeat(childCount) {
                        val starView = getChildAt(it) as StarView
                        when {
                            it <= index -> starView.changeTrackPosition(1f)
                            else -> starView.changeTrackPosition(0f)
                        }
                    }
                    currentScore = averageScore * (index + 1)
                    onScoreChangedListener?.onScoreChanged(currentScore)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (System.currentTimeMillis() - lastClickTime < 200) return false

                val moveX = event.x - paddingLeft
                //手指当前划动位置的子View下标
                val index = (moveX / starSize).toInt()
                if (index > childCount - 1) return false
                //如果一个星星被拖拽的位置大于95%，那么就认为这颗星星满了
                val lastFlipStarPercent = (moveX % starSize / starSize.toFloat()).run {
                    if (this > 0.95) 1f else this
                }
                repeat(childCount) {
                    val starView = getChildAt(it) as StarView
                    when {
                        it < index -> starView.changeTrackPosition(1f)
                        it > index -> starView.changeTrackPosition(0f)
                        else -> {
                            starView.changeTrackPosition(lastFlipStarPercent)
                        }
                    }
                }
                currentScore = averageScore * index + averageScore * lastFlipStarPercent
                onScoreChangedListener?.onScoreChanged(currentScore)
            }
        }
        return true
    }

    private fun changeStarViewState() {
        val index = (currentScore / averageScore).toInt()
        val lastPercent = (currentScore % averageScore) / averageScore
        repeat(childCount) {
            val starView = getChildAt(it) as StarView
            when {
                it <= index - 1 -> starView.changeTrackPosition(1f)
                it == index -> starView.changeTrackPosition(lastPercent)
                else -> starView.changeTrackPosition(0f)
            }
        }
    }

    fun setOnScoreChangedListener(onScoreChangedListener: OnScoreChangedListener) {
        this.onScoreChangedListener = onScoreChangedListener
    }

    private fun Int.dp2px() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        resources.displayMetrics
    )

    interface OnScoreChangedListener {
        fun onScoreChanged(score: Float)
    }
}