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
    private var rootRef = firebaseDatabase.reference
    private lateinit var firebaseAuth: FirebaseAuth
    private var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_email)

        btn_create_acc.setOnClickListener(this)

        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
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
            toast(applicationContext, getString(R.string.fill_in_all))
        } else {
            firebaseAuth
                .createUserWithEmailAndPassword(
                    et_email.text.toString(),
                    et_password.text.toString()
                )
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        firebaseUser = this.firebaseAuth.currentUser

                        val user =
                            firebaseUser?.uid?.let {
                                User(
                                    it,
                                    et_name.text.toString().trim(),
                                    et_email.text.toString().trim()
                                )
                            }
                        writeNewUser(user)

                        startActivity(Intent(applicationContext, MainActivity::class.java))
                        finishAffinity()
                    } else {
                        toast(applicationContext, getString(R.string.email_already_registered))
                    }
                    hideProgressDialog()
                }

        }
    }

    private fun writeNewUser(user: User?) {
        rootRef
            .child("user")
            .child(firebaseAuth.uid.toString())
            .setValue(user)
    }
}
