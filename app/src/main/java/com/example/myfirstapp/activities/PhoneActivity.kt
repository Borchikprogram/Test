
package com.example.myfirstapp.activities

import com.example.myfirstapp.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_phone.*


class PhoneActivity : BaseActivity(3) {
    private val TAG = "PhoneActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone)
        setupBottomNavigation()
        Log.d(TAG, "onCreate: ")
        edit_profile_btn.setOnClickListener{
            val intent= Intent(this,EditProfileActivity::class.java)
            startActivity(intent)
        }
    }
}
