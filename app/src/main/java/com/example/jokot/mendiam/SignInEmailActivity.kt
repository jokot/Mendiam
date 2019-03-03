package com.example.jokot.mendiam

import android.content.Intent
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in_email.*

class SignInEmailActivity : BaseActivity() {
    private var firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_email)

        btn_continue.setOnClickListener {
            val email = et_email.text.toString().trim()
            val password = et_password.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                toast(this, "Isi Semua kolom !")
            } else {
                signIn(email, password)
            }
        }
    }

    private fun signIn(email: String, password: String) {
        showProgressDialog()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    toast(applicationContext, "Email atau Password Anda Salah")
                }
                hideProgressDialog()
            }
    }
}
