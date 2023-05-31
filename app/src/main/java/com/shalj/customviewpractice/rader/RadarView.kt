package com.shalj.customviewpractice.rader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.SweepGradient
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.shalj.customviewpractice.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import kotlin.math.roundToInt


class RadarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var size = 0
    private val defaultSize = 100

    //画6个圈的半径
    private val circleProportion = arrayOf(1 / 13f, 2 / 13f, 3 / 13f, 4 / 13f, 5 / 13f, 6 / 13f)

    //画圈的画笔
    private val paint = Paint()

    //扫描的画笔
    private val scanPaint = Paint()
    private val scanDegree = 1
    private val scanMatrix = Matrix()
    private lateinit var scanShader: Shader

    //中间的头像
//    private var centerBitmap: WeakReference<Bitmap>
    private var centerBitmap: Bitmap?
    private val centerRect = Rect()

    init {
        paint.apply {
            isAntiAlias = true
            color = Color.LTGRAY
            style = Paint.Style.STROKE
            strokeWidth = 1.dp2px()
        }
        scanPaint.apply {
            isAntiAlias = true
            style = Paint.Style.FILL_AND_STROKE
        }
        val drawable = ContextCompat.getDrawable(context, R.drawable.me)
//        centerBitmap = WeakReference(drawable?.toBitmap()?.rounded())
        centerBitmap = drawable?.toBitmap()?.rounded()
    }

    private fun startScan() {
        (context as AppCompatActivity).lifecycleScope.launch {
            while (true) {
                delay(45)
                scanMatrix.postRotate(scanDegree.toFloat(), size / 2f, size / 2f)
                invalidate()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        //需要保证是一个正方形
        size = if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            defaultSize.dp2px().roundToInt()
        } else {
            width.coerceAtMost(height)
        }

        setMeasuredDimension(size, size)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        scanShader = SweepGradient(
            size / 2f,
            size / 2f,
            intArrayOf(
                Color.TRANSPARENT,
                Color.parseColor("#84B5CA")
            ),
            floatArrayOf(0f, 1f)
        )

        startScan()
    }

    override fun onDraw(canvas: Canvas?) {
        val centerX = size / 2f
        val centerY = size / 2f

        drawCircles(canvas, centerX, centerY)
        drawScanSweep(canvas, centerX, centerY)
        drawAvatar(canvas, centerX, centerY)
    }


    //最底层6个圈
    private fun drawCircles(canvas: Canvas?, centerX: Float, centerY: Float) {
        paint.style = Paint.Style.STROKE
        paint.color = Color.LTGRAY
        repeat(circleProportion.size) {
            canvas?.drawCircle(centerX, centerY, size * circleProportion[it], paint)
        }
    }

    //中间层扫描
    private fun drawScanSweep(canvas: Canvas?, centerX: Float, centerY: Float) {
        canvas?.save()
        scanShader.setLocalMatrix(scanMatrix)
        scanPaint.shader = scanShader
        canvas?.concat(scanMatrix)
        canvas?.drawCircle(centerX, centerY, size * circleProportion[4], scanPaint)

        canvas?.restore()
    }

    private fun drawAvatar(canvas: Canvas?, centerX: Float, centerY: Float) {
        centerBitmap?.let {
            val radius = size * circleProportion[1] / 2f
            centerRect.set(
                (centerX - radius).toInt(),
                (centerY - radius).toInt(),
                (centerX + radius).toInt(),
                (centerY + radius).toInt()
            )
//            canvas?.drawBitmap(it, null, centerRect, null)
            canvas?.drawBitmap(it, null, centerRect, null)
        }
    }

    private fun Int.dp2px() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        resources.displayMetrics
    )

    private fun Drawable.toBitmap(): Bitmap? {
        if (this is BitmapDrawable) {
            return bitmap
        }
        val bitmap =
            Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        setBounds(0, 0, canvas.width, canvas.height)
        draw(canvas)
        return bitmap
    }

    private fun Bitmap.rounded(): Bitmap? {
        val size = width.coerceAtMost(height)
        val newBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val paint = Paint().apply {
            isAntiAlias = true
            color = Color.YELLOW
        }
        val canvas = Canvas(newBitmap)
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(this, -800f, -1600f, paint)
        return newBitmap
    }
}