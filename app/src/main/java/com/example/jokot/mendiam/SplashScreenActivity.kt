package com.example.jokot.mendiam

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashScreenActivity : AppCompatActivity() {
    private val TIMEOUT :Long= 2000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        delay()
    }

    private fun delay(){
        Handler().postDelayed({
            startActivity(Intent(this,SignInActivity::class.java))
        },TIMEOUT)
    }
}
