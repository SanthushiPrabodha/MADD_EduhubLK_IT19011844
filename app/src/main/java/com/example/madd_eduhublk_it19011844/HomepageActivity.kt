//IT19011844-Hemachandra M.G.S.P.- Assignment Component
package com.example.madd_eduhublk_it19011844

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_homepage_activity.*

class HomepageActivity : AppCompatActivity() {

    private lateinit var animationDrawable: AnimationDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage_activity)

        animationDrawable = home_main.background as AnimationDrawable

        window.decorView.setBackgroundColor(R.drawable.animated_homepage_background)

        animationDrawable = window.decorView.background as AnimationDrawable

        animationDrawable.setEnterFadeDuration(5000)
        animationDrawable.setExitFadeDuration(2000)

        home_start.setOnLongClickListener {
            home_stop.visibility = View.VISIBLE
            return@setOnLongClickListener true
        }

        home_stop.setOnLongClickListener {
            home_stop.visibility = View.INVISIBLE
            return@setOnLongClickListener true
        }
    }

    override fun onResume() {
        super.onResume()
        animationDrawable.start()
    }

    override fun onPause() {
        super.onPause()
        animationDrawable.stop()
    }
}
