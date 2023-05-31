package com.shalj.customviewpractice.rating

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.shalj.customviewpractice.R
import com.shalj.customviewpractice.mode
import com.shalj.customviewpractice.size
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * 评分控件中的单个星星
 * */
class StarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        isAntiAlias = true
    }

    //星星的大小
    private var size = 0

    //当前手指触摸的位置占整个View的百分比（从左至右）
    private var trackPosition = 0f

    //星星填满后的颜色
    var starFilledColor = Color.parseColor("#ff8c1a")
        set(value) {
            field = value
            invalidate()
        }

    //星星默认颜色
    var starColor = Color.parseColor("#666666")
        set(value) {
            field = value
            invalidate()
        }

    init {
        val typedArray = context.obtainStyledAttributes(R.styleable.StarView)

        starColor = typedArray.getColor(R.styleable.StarView_starColor, starColor)
        starFilledColor = typedArray.getColor(R.styleable.StarView_starFilledColor, starFilledColor)

        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //保持宽高一致，如果都是wrap_content的话指定最小宽度
        size =
            if (widthMeasureSpec.mode == MeasureSpec.AT_MOST && heightMeasureSpec.mode == MeasureSpec.AT_MOST) {
                100.dp2px().roundToInt()
            } else {
                widthMeasureSpec.size.coerceAtMost(heightMeasureSpec.size)
            }
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas?) {
        drawLeft(canvas)
        drawRight(canvas)
    }

    private fun drawLeft(canvas: Canvas?) {
        canvas?.save()
        canvas?.clipRect(0f, 0f, trackPosition, height.toFloat())
        paint.color = starFilledColor
        drawStar(canvas)
        canvas?.restore()
    }

    private fun drawRight(canvas: Canvas?) {
        canvas?.save()
        canvas?.clipRect(trackPosition, 0f, size.toFloat(), height.toFloat())
        paint.color = starColor
        drawStar(canvas)
        canvas?.restore()
    }

    private fun drawStar(canvas: Canvas?) {
        paint.style = Paint.Style.FILL

        val x = width / 2
        val y = height / 2
        val outerRadius = x.coerceAtMost(y) - 10
        val innerRadius = (outerRadius * 0.4).toInt()

        val path = Path()
        for (i in 0..4) {
            val angle = (i * 2 * Math.PI / 5).toFloat()
            val x1 = x + outerRadius * sin(angle.toDouble()).toFloat()
            val y1 = y - outerRadius * cos(angle.toDouble()).toFloat()
            val x2 = x + innerRadius * sin(angle + Math.PI / 5).toFloat()
            val y2 = y - innerRadius * cos(angle + Math.PI / 5).toFloat()
            if (i == 0) {
                path.moveTo(x1, y1)
            } else {
                path.lineTo(x1, y1)
            }
            path.lineTo(x2, y2)
        }
        path.close()

        canvas?.drawPath(path, paint)
    }

//    @SuppressLint("ClickableViewAccessibility")
//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        when (event?.action) {
//            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
//                logE("StarView", event.x)
//                middle = event.x
//                invalidate()
//            }
//
//            MotionEvent.ACTION_UP -> {
//
//            }
//        }
//        return true
//    }

    fun changeTrackPosition(percent: Float) {
        trackPosition = size * percent
        invalidate()
    }

    private fun Int.dp2px() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        resources.displayMetrics
    )
}