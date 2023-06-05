package com.shalj.customviewpractice.base

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * update 2023.06.05
 *  BaseActivity used to inherited by other activities
 *
 *  @property binding instance of ViewDataBinding
 *  @param layoutId need override it and return your layout id
 * */
abstract class BaseActivity<T : ViewDataBinding>(private val layoutId: Int) : AppCompatActivity() {

    lateinit var binding: T

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        setupStatusBar()
    }

    /**
     * @param isAppearanceLightStatusBars change status bar style
     * @param statusBarColor status bar color
     * @param flagFullScreen
     * @param fitsSystemWindow
     * */
    fun setupStatusBar(
        isAppearanceLightStatusBars: Boolean = true,
        @ColorInt statusBarColor: Int = Color.WHITE,
        flagFullScreen: Boolean = false,
        fitsSystemWindow: Boolean = true
    ) {
        WindowCompat.setDecorFitsSystemWindows(window, fitsSystemWindow)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            if (flagFullScreen) {
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
            controller.isAppearanceLightStatusBars = isAppearanceLightStatusBars
        }
        //change status bar color
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = statusBarColor
    }

    /**
     * @param toolbar Toolbar that you wanna set up
     * @param homeButtonEnabled if show the menu button on the left of the top
     * @param showBackArrow if show the back arrow
     * @param showTitle if show the title
     * */
    fun AppCompatActivity.setupToolbar(
        toolbar: Toolbar?,
        homeButtonEnabled: Boolean = true,
        showBackArrow: Boolean = true,
        showTitle: Boolean = false
    ) {
        setSupportActionBar(null)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            if (!showTitle) title = ""
            setHomeButtonEnabled(homeButtonEnabled)
            setDisplayHomeAsUpEnabled(showBackArrow)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    fun isBindingInitialized() = ::binding.isInitialized
}