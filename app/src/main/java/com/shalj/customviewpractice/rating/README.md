# 无级调控 RatingView

这是一个简单的评分自定义view，相对于系统的RatingView，其优势在于可以精确到0.0001分（虽说并没有这个必要）。
但感觉比较好玩，并且能巩固一下自己相关知识，于是它就诞生了。

效果图：
![效果图](https://github.com/Shalj/CustomViewPractice/blob/main/screenshots/rating.gif)

实现思路：
- 自定义StarView，这个StarView支持裁剪变色，首先确定一个中间分隔线(trackPosition)。Rect(0, 0, trackPosition, height)区域画着色部分，Rect(trackPosition, 0, width, height)区域画默认颜色部分。代码如下：
```kotlin
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
```
- 自定义RatingView，继承自LinearLayout，添加starCount个StarView，重写onTouchEvent，将当前触摸的位置传递给StarView。
```kotlin
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                lastClickTime = System.currentTimeMillis()
            }

            MotionEvent.ACTION_UP -> {
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
```