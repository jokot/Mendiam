package com.example.jokot.mendiam

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_sign_in_email.*

class SignInEmailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_email)

        var editor = getSharedPreferences(MainActivity.MY_PREF, Context.MODE_PRIVATE).edit()


        btn_continue.setOnClickListener {
            editor.putInt("login",1)
            editor.putInt("register",1)
            editor.apply()
            intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
