package com.example.myfirstapp.activities


import com.example.myfirstapp.R

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class ProfileActivity : BaseActivity(4) {
    private val TAG = "ProfileActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupBottomNavigation()
        Log.d(TAG, "onCreate")
        var stoptime:Long=0
        start_button.setOnClickListener{
            timer.base=SystemClock.elapsedRealtime() +stoptime
            timer.start()
            start_button.visibility= View.GONE
            pause_button.visibility= View.VISIBLE
        }
        pause_button.setOnClickListener{
            stoptime=timer.base-SystemClock.elapsedRealtime() +stoptime
            timer.stop()
            start_button.visibility= View.VISIBLE
            pause_button.visibility= View.GONE
        }
        reset_button.setOnClickListener{
            timer.base=SystemClock.elapsedRealtime()
            stoptime=0
            timer.stop()
            start_button.visibility= View.VISIBLE
            pause_button.visibility= View.GONE
        }
    }
}

