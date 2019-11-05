package com.example.myfirstapp.activities

import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import com.example.myfirstapp.R
import com.example.myfirstapp.views.PasswordDialog
import com.google.firebase.auth.EmailAuthProvider

import com.example.myfirstapp.User
import com.google.firebase.auth.AuthCredential
import com.google.firebase.database.DatabaseReference
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_edit_profile.*


class EditProfileActivity : AppCompatActivity(), PasswordDialog.Listener {
    private val TAG = "EditProfileActivity"
    private lateinit var mUser: User
    private lateinit var mPendingUser: User
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        Log.d(TAG, "onCreate")

        close_image.setOnClickListener { finish() }
        save_img.setOnClickListener { updateProfile() }


        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
        mDatabase.child("users").child(mAuth.currentUser!!.uid).addListenerForSingleValueEvent(ValueEventListenerAdapter {
            mUser = it.getValue(User::class.java)!!
                name_input.setText(mUser.name, TextView.BufferType.EDITABLE)
                belt_input.setText(mUser.belt, TextView.BufferType.EDITABLE)
                old_input.setText(mUser.old.toString(), TextView.BufferType.EDITABLE)
                phone_input.setText(mUser.phone.toString(), TextView.BufferType.EDITABLE)
            email_inputi.setText(mUser.email, TextView.BufferType.EDITABLE)

        })
    }
    private fun updateProfile() {
        mPendingUser = readInputs()
        val error = validate(mPendingUser)
        if (error == null) {
            if (mPendingUser.email == mUser.email) {
                updateUser(mPendingUser)
            } else {
                PasswordDialog().show(supportFragmentManager, "password_dialog")
            }
        } else {
            showToast(error)
        }
    }
    private fun readInputs(): User {
        val phoneStr = phone_input.text.toString()
        val oldStr = old_input.text.toString()
        return User(
            name = name_input.text.toString(),
            belt = belt_input.text.toString(),
            old = if (oldStr.isEmpty()) 0 else oldStr.toLong(),
            email = email_inputi.text.toString(),
            phone = if (phoneStr.isEmpty()) 0 else phoneStr.toLong()
        )
    }
    override fun onPasswordConfirm(password: String) {
        if (password.isNotEmpty()) {
            val credential = EmailAuthProvider.getCredential(mUser.email, password)
            mAuth.currentUser!!.reauthenticate(credential) {
                mAuth.currentUser!!.updateEmail(mPendingUser.email) {
                    updateUser(mPendingUser)
                }

            }  } else {
                    showToast("Введите ваш пароль")
        }
    }

    private fun updateUser(user: User) {
        val updatesMap = mutableMapOf<String, Any>()
        if (user.name != mUser.name) updatesMap["name"] = user.name
        if (user.belt != mUser.belt) updatesMap["belt"] = user.belt
        if (user.old != mUser.old) updatesMap["old"] = user.old
        if (user.email != mUser.email) updatesMap["email"] = user.email
        if (user.phone != mUser.phone) updatesMap["phone"] = user.phone

        mDatabase.updateUser(mAuth.currentUser!!.uid, updatesMap) {
            showToast("Профиль сохранен")
            finish()
        }
    }

    private fun validate(user: User): String? =
        when {
            user.name.isEmpty() -> "Please enter name"
            user.email.isEmpty() -> "Please enter email"
            else -> null
        }
private fun DatabaseReference.updateUser(uid: String, updates: Map<String, Any>,
                                         onSuccess: () -> Unit) {
    child("users").child(mAuth.currentUser!!.uid).updateChildren(updates)
        .addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else {
                showToast(it.exception!!.message!!)
            }
        }
}

private fun FirebaseUser.updateEmail(email: String, onSuccess: () -> Unit) {
    updateEmail(email).addOnCompleteListener {
        if (it.isSuccessful) {
            onSuccess()
        } else {
            showToast(it.exception!!.message!!)
        }
    }
}

private fun FirebaseUser.reauthenticate(credential: AuthCredential, onSuccess: () -> Unit) {
    reauthenticate(credential).addOnCompleteListener {
        if (it.isSuccessful) {
            onSuccess()
        } else {
            showToast(it.exception!!.message!!)
        }
    }
}
}