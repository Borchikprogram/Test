package com.example.myfirstapp.activities

import android.content.Context
import android.content.Intent
import android.util.Log
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.AuthResult
import com.example.myfirstapp.R
import com.example.myfirstapp.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_register_email.*
import kotlinx.android.synthetic.main.fragment_register_namepass.*

class RegisterActivity : AppCompatActivity(), EmailFragment.Listener, NamePassFragment.Listener {//Класс регистрация пользователя
    private val TAG = "RegisterActivity"


    private var mEmail: String? = null

    private lateinit var mAuth: FirebaseAuth//Аутентификация пользователя
    private lateinit var mDatabase: DatabaseReference //База данных



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(R.id.frame_layout, EmailFragment())
                .commit()
        }
    }
    override fun onNext(email: String) {
        if (email.isNotEmpty()) {//Проверка на пустой E-mail
            mEmail = email
            mAuth.fetchSignInMethodsForEmail(email) { signInMethods ->//Проверка на имеющийся e-mail и вызов параметра
                if (signInMethods.isEmpty()) {//если e-mail'a нет
                    supportFragmentManager.beginTransaction().replace(R.id.frame_layout, NamePassFragment())
                        .addToBackStack(null)
                        .commit()
                } else {
                    showToast("This email already exists")//Используется при имеющимся e-mail
                }
            }
        } else {
            showToast("Please enter email")//вывидется при пустом E-mail
        }
    }

    override fun onRegister(fullName: String, password: String) {
        if (fullName.isNotEmpty() && password.isNotEmpty()) {//Проверка на пустой fullname, password
            val email = mEmail
            if (email != null) {//Если e-mail не пустой
                mAuth.createUserWithEmailAndPassword(email, password) {//регистрация
                    if (it != null) {
                        mDatabase.createUser(it.user.uid, mkUser(fullName, email)) {//создаем User в mDatabase
                            startHomeActivity()//возвращет на главный экран
                        }
                    }
                }
            } else {
                Log.e(TAG, "onRegister: email is null")
                showToast("Please enter email")
                supportFragmentManager.popBackStack()//Шаг назад для подстраховки
            }
        } else {
            showToast("Please enter full name and password")
        }
    }

    private fun unknownRegisterError(it: Task<*>) {//функция при ошибке регистрации польозвателя
        Log.e(TAG, "failed to create user profile", it.exception)
        showToast("Something wrong happened. Please try again later")
    }

    private fun startHomeActivity() {//переход на HomeActivity
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun mkUser(fullName: String, email: String): User {
        return User(name = fullName, email = email)
    }

    private fun mkUsername(fullName: String) =
        fullName.toLowerCase().replace(" ", ".")

    private fun FirebaseAuth.fetchSignInMethodsForEmail(email: String,//дифференцированный способ входа для пользователя с паролем / ссылкой
                                                        onSuccess: (List<String>) -> Unit) {
        fetchSignInMethodsForEmail(email).addOnCompleteListener {// Пользователь входит с помощью электронной почты
            if (it.isSuccessful) {
                onSuccess(it.result!!.signInMethods ?: emptyList<String>())
            } else {
                showToast(it.exception!!.message!!)
            }
        }
    }

    private fun DatabaseReference.createUser(uid: String, user: User, onSuccess: () -> Unit) {//Создаем пользователя
        val reference = child("users").child(uid)//добавлем в users
        reference.setValue(user).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else {
                unknownRegisterError(it)
            }
        }
    }

    private fun FirebaseAuth.createUserWithEmailAndPassword(email: String, password: String,
                                                            onSuccess: (AuthResult?) -> Unit) {//Принимаем значение e-mail и password и возвращаем в Unit
        createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess(it.result)
                } else {
                    unknownRegisterError(it)
                }
            }
    }
}


// 1 - Телефон,Кнопка next
class EmailFragment : Fragment() {
    private lateinit var mListener: Listener
    private lateinit var next_btn: Button
    private lateinit var email_input:EditText
    interface Listener {
        fun onNext(email: String)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var v = inflater.inflate(R.layout.fragment_register_email, container, false)
        next_btn = v.findViewById(R.id.next_btn)
        email_input = v.findViewById(R.id.email_input)
        return v
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        coordinateBtnAndInputs(next_btn, email_input)
        next_btn.setOnClickListener {//При нажатии кнопки
            val email = email_input.text.toString()//мы получаем e-mail
            mListener.onNext(email)//он добавляется в mListener который является нашим activity
        }
    }

    override fun onAttach(context: Context) {//Вызывается, когда фрагмент связывается с активностью
        super.onAttach(context)
        mListener = context as Listener
    }
}

// 2 - Full name, password, register button
class NamePassFragment : Fragment() {
    private lateinit var mListener: Listener
    private lateinit var register_btn:Button
    private lateinit var full_name_input:EditText
    private lateinit var password_input:EditText

    interface Listener {
        fun onRegister(fullName: String, password: String)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,//это класс, который умеет из содержимого layout-файла создать View-элемент
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register_namepass, container, false)//возвращем значение из layout
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        coordinateBtnAndInputs(register_btn, full_name_input, password_input)
        register_btn.setOnClickListener {//При нажатии на кнопку
            val fullName = full_name_input.text.toString()//получаем имя
            val password = password_input.text.toString()//получаем пароль
            mListener.onRegister(fullName, password)//Добвляем в Activity данные
        }
    }

    override fun onAttach(context: Context) {//Вызывается, когда фрагмент связывается с активностью
        super.onAttach(context)
        mListener = context as Listener
    }
}
