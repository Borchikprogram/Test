package com.example.myfirstapp.activities

import com.example.myfirstapp.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity(0) {
private val TAG ="HomeActivity"
    private lateinit var mAuth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        Log.d(TAG, "onCreate")
setupBottomNavigation()
        mAuth = FirebaseAuth.getInstance()

      //  nAuth.signInWithEmailAndPassword("timka49809@gmail.com","tim120798")
       //     .addOnCompleteListener() {
       //         if (it.isSuccessful){
       //             Log.d(TAG, "signIn: success")
       //         } else {
       //             Log.e(TAG, "signIn: failure", it.exception)
       //         }
       //     }

        mAuth.addAuthStateListener {
            if (it.currentUser == null) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if(mAuth.currentUser==null){
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}

