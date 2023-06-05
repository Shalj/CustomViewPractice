package com.shalj.customviewpractice

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.shalj.customviewpractice.view.rating.RatingView
import java.math.RoundingMode

class RatingActivity : AppCompatActivity() {

    lateinit var ratingView: RatingView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)

        ratingView = findViewById<RatingView>(R.id.rating)
        val textView = findViewById<TextView>(R.id.tv_score)
        ratingView.apply {
            setOnScoreChangedListener(object : RatingView.OnScoreChangedListener {
                override fun onScoreChanged(score: Float) {
                    logE("RatingActivity", score)
                    val formattedScore =
                        score.toBigDecimal().setScale(1, RoundingMode.HALF_UP).toString()
                    textView.text = formattedScore
                }
            })
            post { currentScore = 8f }
        }
    }

    fun minus(view: View) {
        ratingView.currentScore--
    }

    fun plus(view: View) {
        ratingView.currentScore++
    }
}