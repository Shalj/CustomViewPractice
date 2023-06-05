package com.shalj.customviewpractice.view.bubble_side_bar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import com.shalj.customviewpractice.R
import com.shalj.customviewpractice.logE
import com.shalj.customviewpractice.size
import kotlin.math.roundToInt

/**
 * 通讯录或者地区选择右边的索引
 * */
class BubbleSideBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var data: Array<String> = arrayOf(
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
        "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    )
        set(value) {
            field = value
            invalidate()
        }

    private val paint = Paint()

    //字体大小
    private var textSize = 14f

    //字体颜色
    private var textColor = Color.BLACK

    //测量字体高度的容器
    private var textBounds = Rect()

    //选中的字体颜色
    private var selectedTextColor = Color.BLUE

    //字体间的间距
    private var textVerticalPadding = 10

    //当前选中的位置
    private var currentSelectedIndex = -1

    //选则的回调
    var onLetterSelectedListener: OnLetterSelectedListener? = null

    //手指触摸之后，弹起的圆弧半径
    var bubbleRadius = 20

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BubbleSideBar)

        textSize = typedArray.getDimensionPixelSize(
            R.styleable.BubbleSideBar_textSize, textSize.sp2px().roundToInt()
        ).toFloat()

        textVerticalPadding = typedArray.getDimensionPixelSize(
            R.styleable.BubbleSideBar_textVerticalPadding, textVerticalPadding.dp2px().roundToInt()
        )
        textColor = typedArray.getColor(R.styleable.BubbleSideBar_textColor, textColor)
        selectedTextColor =
            typedArray.getColor(R.styleable.BubbleSideBar_selectedTextColor, selectedTextColor)

        typedArray.recycle()

        paint.apply {
            isAntiAlias = true
            textSize = this@BubbleSideBar.textSize
            color = textColor
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        var width = 0
        var height = 0
        data.forEach {
            //计算一下最宽的那个字母或者汉字
            //如果xml中指定了具体尺寸，和xml中指定的尺寸相比较，取比较大的那个
            val textWidth = paint.measureText(it)
            width = width.coerceAtLeast(textWidth.roundToInt())

            //计算所有字母或汉字的高度和
            paint.getTextBounds(it, 0, it.length, textBounds)
            height += textBounds.height()
        }
        width = width.coerceAtMost(widthMeasureSpec.size)
        //宽度再算上左右间距
        width += paddingLeft + paddingRight
        //高度 + 上下间距 + 字母间距
        height += paddingTop + paddingBottom + textVerticalPadding * (data.size - 1)
        //取高度的较小的值
        height = height.coerceAtMost(heightMeasureSpec.size)
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        repeat(data.size) {
            //需要绘制的文字
            val text = data[it]
            //绘制文字的x轴中点
            val centerX = (width - paint.measureText(text)) / 2
            //获取文字的相关参数
            val metricsInt = paint.fontMetricsInt
            paint.getTextBounds(text, 0, text.length, textBounds)
            //计算出文字的baseline
            val dy = (metricsInt.bottom - metricsInt.top) / 2 - metricsInt.bottom
            //每个字母的高度（包含了onMeasure测量的时候，字母间的间距）
            val itemHeight = (height - paddingTop - paddingBottom) / data.size.toFloat()
            //当前绘制文字的y轴坐标
            val centerY = itemHeight * it + itemHeight / 2 + dy + paddingTop
            paint.color = if (it == currentSelectedIndex) selectedTextColor else textColor
            canvas?.drawText(text, centerX, centerY, paint)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val moveY = event.y - paddingTop
                val itemHeight = (height - paddingTop - paddingBottom) / data.size.toFloat()
                val index = (moveY / itemHeight).toInt()
                if (index < 0 || index >= data.size) return true
                logE("BubbleSideBar", "moveY:$moveY, itemHeight:$itemHeight, index:$index")

                if (currentSelectedIndex != index) {
                    currentSelectedIndex = index
                    invalidate()
                    onLetterSelectedListener?.onLetterSelected(index, data[index], true)
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                onLetterSelectedListener?.onLetterSelected(
                    currentSelectedIndex,
                    data[currentSelectedIndex],
                    false
                )
                currentSelectedIndex = -1
                invalidate()
            }
        }
        return true
    }

    interface OnLetterSelectedListener {
        fun onLetterSelected(index: Int, value: String, touching: Boolean)
    }

    private fun Float.sp2px() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP, this, resources.displayMetrics
    )

    fun Int.dp2px() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), resources.displayMetrics
    )
}