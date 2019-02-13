package com.example.jokot.mendiam

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val editor = getSharedPreferences(MainActivity.MY_PREF, Context.MODE_PRIVATE).edit()
        val pref = getSharedPreferences(MainActivity.MY_PREF, Context.MODE_PRIVATE)
        val register = pref.getInt("register",0)

        if (register == 1){
            intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_up_facebook.setOnClickListener {
            intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            editor.putInt("register",1)
            editor.apply()
            finish()
        }

        tv_sign_in.setOnClickListener {
            intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

    }
}
