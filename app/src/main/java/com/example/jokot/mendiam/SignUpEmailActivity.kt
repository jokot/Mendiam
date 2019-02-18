package com.example.jokot.mendiam

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_sign_up_email.*

class SignUpEmailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_email)

        btn_create_acc.setOnClickListener {
            intent = Intent(this,SignInEmailActivity::class.java)
            startActivity(intent)
        }
    }
}
