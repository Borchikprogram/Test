package com.example.myfirstapp.activities
import com.example.myfirstapp.R
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_search.*
import android.widget.CalendarView
import java.util.*


class RaspActivity : BaseActivity(2) {
    private val TAG = "RaspActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setupBottomNavigation()
        Log.d(TAG, "onCreate")
        val button = findViewById<Button>(R.id.button2)
        button.setOnClickListener {
            val calendarView = findViewById<CalendarView>(R.id.calendarView)
            calendarView?.setOnDateChangeListener { view, year, month, dayOfMonth ->
                // Note that months are indexed from 0. So, 0 means January, 1 means february, 2 means march etc.
                val msg = "Текущая дата: " + dayOfMonth + "/" + (month + 1) + "/" + year
                val calendar = Calendar.getInstance()
                val day = calendar.get(Calendar.DAY_OF_WEEK)

                when (day) {
                    Calendar.SUNDAY -> {showToast("Нет тренировок")
                    }
                    Calendar.MONDAY -> {showToast("Есть")
                    }
                    Calendar.TUESDAY -> {showToast("Нет ")
                    }
                }// Current day is Sunday
                // Current day is Monday
                // etc.
                /*Toast.makeText(this@RaspActivity, msg, Toast.LENGTH_SHORT).show()*/
        }
    }}}




