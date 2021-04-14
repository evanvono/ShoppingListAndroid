package com.evanvonoehsen.shoppinglist

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_splash_screen.*


class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        val spinAnim = AnimationUtils.loadAnimation(this, R.anim.rotate)
        val zoomRevealAnim = AnimationUtils.loadAnimation(this, R.anim.zoom_reveal_anim)
        val intent = Intent(this, ScrollingActivity::class.java)

        val animListener = object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                Handler().postDelayed({
                    startActivity(intent)
                    finish()
                }, 1000)
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        }

        zoomRevealAnim.setAnimationListener(animListener)

        imageView.startAnimation(spinAnim)
        textView2.startAnimation(zoomRevealAnim)
    }
}