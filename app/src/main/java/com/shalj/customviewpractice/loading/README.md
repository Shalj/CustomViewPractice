# 老款58同城loading

想法来源于[红橙Darren](https://www.jianshu.com/u/35083fcb7747)大佬，感兴趣可以点击查看[原文章](https://www.jianshu.com/p/e4de28b4d8ac)。
我在原来的基础上，优化了一下动画上下反复的思路，由两个```Animator```（一上一下）变成了一个```Animator```，无限循环，上下反复。

![效果图](https://github.com/Shalj/CustomViewPractice/blob/main/screenshots/loading.gif)
```
private fun initAnimators() {
        animatorSet = AnimatorSet().apply {
            duration = defaultAnimatorDuration
            interpolator = DecelerateInterpolator()
        }

        val upDownAnimator = ObjectAnimator.ofFloat(
            mShapeView,
            "translationY",
            1f, -300f
        ).apply {
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }
        val indicatorScaleAnimator = ObjectAnimator.ofFloat(
            mIndicatorView,
            "scaleX",
            1f, 0.2f
        ).apply {
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
```