package com.example.jokot.mendiam

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.jokot.mendiam.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up_email.*

class SignUpEmailActivity : BaseActivity(), View.OnClickListener {

    private var firebaseDatabase = FirebaseDatabase.getInstance()
    var rootRef = firebaseDatabase.reference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_email)

        btn_create_acc.setOnClickListener(this)

        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onClick(v: View?) {
        val id = v?.id
        when (id) {
            R.id.btn_create_acc -> signUp()
        }
    }

    private fun signUp() {
        showProgressDialog()
        if (et_name.text.toString().trim().isEmpty()
            || et_email.text.toString().trim().isEmpty()
            || et_password.text.toString().isEmpty()
            || (et_confirm_password.text.toString().isEmpty()
                    || et_confirm_password.text.toString() != et_password.text.toString())
        ) {
            toast(applicationContext, "Isi Semua kolom dengan Benar !")
        } else {
            firebaseAuth
                .createUserWithEmailAndPassword(et_email.text.toString(), et_password.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        firebaseUser = this.firebaseAuth.currentUser!!

                        val user =
                            User(
                                firebaseUser.uid,
                                et_name.text.toString().trim(),
                                et_email.text.toString().trim()
                            )
                        writeNewUser(user)

                        startActivity(Intent(applicationContext, MainActivity::class.java))
                        finish()
                    } else {
                        toast(applicationContext, "Email sudah di Registrasikan")
                    }
                    hideProgressDialog()
                }

        }
    }

    //  memasukkan data user ke database
    private fun writeNewUser(user: User) {
        rootRef
            .child("user")
            .child(firebaseAuth.uid.toString())
            .setValue(user)
    }
}
