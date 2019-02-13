package com.example.jokot.mendiam

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val editor = getSharedPreferences(MainActivity.MY_PREF, Context.MODE_PRIVATE).edit()
        val pref = getSharedPreferences(MainActivity.MY_PREF, Context.MODE_PRIVATE)
        val login = pref.getInt("login",0)
        if(login == 1){
            intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_in_facebook.setOnClickListener {
            intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            editor.putInt("login",1)
            editor.apply()
            finish()
        }

        tv_sign_up.setOnClickListener {
            intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }

        btn_in_email.setOnClickListener {
            intent =Intent(this,LoginEmailActivity::class.java)
            startActivity(intent)
        }
    }
}
