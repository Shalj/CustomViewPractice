package com.shalj.customviewpractice.view.bubble_side_bar

import android.os.Bundle
import android.view.View
import com.shalj.customviewpractice.R
import com.shalj.customviewpractice.base.BaseActivity
import com.shalj.customviewpractice.databinding.ActivityBubbleSideBarBinding
import com.shalj.customviewpractice.logE

class BubbleSideBarActivity :
    BaseActivity<ActivityBubbleSideBarBinding>(R.layout.activity_bubble_side_bar) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBubbleBar()
    }
    private fun initBubbleBar() {
        binding.bubbleSizeBarLeft.apply {
            onLetterSelectedListener = object : BubbleSideBar.OnLetterSelectedListener {
                override fun onLetterSelected(index: Int, value: String, touching: Boolean) {
                    onLetterChanged(index, value, touching)
                }
            }
        }
        binding.bubbleSizeBarRight.apply {
            onLetterSelectedListener = object : BubbleSideBar.OnLetterSelectedListener {
                override fun onLetterSelected(index: Int, value: String, touching: Boolean) {
                    onLetterChanged(index, value, touching)
                }
            }
        }
    }

    private fun onLetterChanged(index: Int, value: String, touching: Boolean) {
        logE("BubbleSideBarActivity", "index:$index, value:$value")
        binding.tvBubble.apply {
            visibility = if (touching) View.VISIBLE else View.GONE
            text = value
        }
    }
}