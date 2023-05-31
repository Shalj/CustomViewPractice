package com.shalj.customviewpractice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun goLoading(view: View) {
        startActivity(buildIntent<LoadingActivity>(this))
    }

    fun goRadar(view: View) {
        startActivity(buildIntent<RadarActivity>(this))
    }
}

const val EXTRA = "bundle"
inline fun <reified T : AppCompatActivity> buildIntent(context: Context, bundleBlock: Bundle.() -> Unit = { }): Intent {
    return Intent(context, T::class.java).apply {
        putExtra(EXTRA, Bundle().apply { bundleBlock() })
    }
}