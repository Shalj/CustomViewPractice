package com.shalj.customviewpractice.loading

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class ShapeView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private val defaultSize = 100

    //画笔
    private val paint = Paint()
    private var colors = arrayOf(Color.RED, Color.GREEN, Color.BLUE)

    //当前绘制的形状
    var shape = Shape.Rect

    enum class Shape { Rect, Circle, Triangle }

    init {
        paint.apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        var width = MeasureSpec.getSize(widthMeasureSpec)
        var height = MeasureSpec.getSize(heightMeasureSpec)

        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            width = defaultSize
            height = defaultSize
        } else {
            width = if (width > height) height else width
            height = width
        }

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        when (shape) {
            Shape.Rect -> drawRect(canvas)
            Shape.Circle -> drawCircle(canvas)
            Shape.Triangle -> drawTriangle(canvas)
        }

    }

    private fun drawRect(canvas: Canvas?) {
        paint.color = colors[0]
        canvas?.drawRect(
            paddingLeft.toFloat() + 10,
            paddingTop.toFloat() + 10,
            width - paddingRight.toFloat() - 10,
            height - paddingBottom.toFloat() - 10,
            paint
        )
    }

    private fun drawCircle(canvas: Canvas?) {
        paint.color = colors[1]
        val radiusX = (width - paddingLeft - paddingTop) / 2
        val radiusY = (height - paddingTop - paddingBottom) / 2
        val radius = if (radiusX > radiusY) radiusY else radiusX
        canvas?.drawCircle(radiusX.toFloat(), radiusY.toFloat(), radius.toFloat(), paint)
    }

    private fun drawTriangle(canvas: Canvas?) {
        paint.color = colors[2]
        val radiusX = (width - paddingLeft - paddingTop) / 2
        val radiusY = (height - paddingTop - paddingBottom) / 2
        val radius = if (radiusX > radiusY) radiusY else radiusX
        val path = Path().apply {
            moveTo(radiusX.toFloat(), radiusY - radius.toFloat())
            lineTo(radiusX + radius * cos(Math.toRadians(30.0)).toFloat(), radiusY + radius * sin(Math.toRadians(30.0)).toFloat())
            lineTo(radiusX - radius * cos(Math.toRadians(30.0)).toFloat(), radiusY + radius * sin(Math.toRadians(30.0)).toFloat())
            close()
        }
        canvas?.drawPath(path, paint)
    }

    fun setColors(colors: Array<Int>) {
        this.colors = colors
        invalidate()
    }

    fun changeShape() {
        shape = when (shape) {
            Shape.Rect -> Shape.Circle
            Shape.Circle -> Shape.Triangle
            else -> Shape.Rect
        }
        invalidate()
    }
}