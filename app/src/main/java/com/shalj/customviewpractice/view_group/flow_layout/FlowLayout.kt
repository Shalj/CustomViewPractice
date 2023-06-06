package com.shalj.customviewpractice.view_group.flow_layout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.shalj.customviewpractice.R
import com.shalj.customviewpractice.size

/**
 * flow layout 流式布局
 * */
class FlowLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    //将onMeasure中的每一行子View用List保存起来，这样的话在onLayout中就不用重新计算了
    private val childrenForLineList = arrayListOf<ArrayList<View>>()

    private var itemHorizontalSpace = 0
    private var itemVerticalSpace = 0

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout)

        itemVerticalSpace = typedArray.getDimensionPixelSize(
            R.styleable.FlowLayout_itemVerticalSpace,
            itemVerticalSpace
        )
        itemHorizontalSpace = typedArray.getDimensionPixelSize(
            R.styleable.FlowLayout_itemHorizontalSpace,
            itemHorizontalSpace
        )

        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //onMeasure一般都会调用2次，每次调用的时候要把之前存储的View清除
        childrenForLineList.clear()
        //自身宽度
        val width = widthMeasureSpec.size
        //初始高度
        var height = paddingTop + paddingBottom
        //每一行的宽度
        var lineWidth = paddingLeft
        //每一行的View集合
        var children = arrayListOf<View>()

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            //如果当前view不可见，跳过计算
            if (child.visibility == View.GONE) continue

            //todo 如果有什么需要，在此处处理child的margin
            val params = child.layoutParams as MarginLayoutParams
            //测量子View的宽高
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            val childWidth = child.measuredWidth
            //计算当前行的子View宽度是否超过了自身的宽度，不超过继续加，超过就换行
            if (lineWidth + childWidth > width - paddingRight) {
                height += child.measuredHeight + itemVerticalSpace
                lineWidth = paddingLeft
                //将一行的子View加入到数组中，清空children开始第二行子View存储
                childrenForLineList.add(children)
                children = arrayListOf()
            }
            //将子View添加到数组中
            children.add(child)
            //叠加宽度
            lineWidth += childWidth + itemHorizontalSpace

            if (i == childCount - 1) {
                childrenForLineList.add(children)
                height += child.measuredHeight
                children = arrayListOf()
            }
        }
        setMeasuredDimension(width, height)
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var left = paddingLeft
        var top = paddingTop
        var right: Int
        var bottom: Int

        childrenForLineList.forEach { children ->
            children.forEach { child ->
                if (child.visibility != View.GONE) {
                    //right 是当前子View的宽度 + left(同一行之前所有的子View和itemHorizontalSpace的和)
                    right = (left + child.measuredWidth)
                        .coerceAtMost(width - paddingRight)
                    //bottom 是当前子view的高度
                    bottom = top + child.measuredHeight
                    child.layout(left, top, right, bottom)

                    //left 是同一行之前所有的子View和itemHorizontalSpace的和
                    left += child.measuredWidth + itemHorizontalSpace
                }
            }
            //当一行遍历结束, left重置, top下移一行
            left = paddingLeft
            val rowHeight = if (children.isEmpty()) 0 else children.first().measuredHeight
            top += rowHeight + itemVerticalSpace
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }
}