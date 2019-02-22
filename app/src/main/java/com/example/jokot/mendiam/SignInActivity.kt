package com.example.jokot.mendiam

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private const val RC_SIGN_IN = 9001
        private const val TAG = "Sign In Activity"
    }

    private lateinit var firebaseAuth: FirebaseAuth
    private var firebaseUser: FirebaseUser? = null
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser

        btn_in_facebook.setOnClickListener(this)
        tv_sign_up.setOnClickListener(this)
        btn_in_google.setOnClickListener(this)
        btn_in_email.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val id = v?.id
        when (id) {
            R.id.btn_in_facebook -> { }
            R.id.btn_in_google -> signIn()
            R.id.btn_in_email -> {
                startActivity(Intent(this, SignInEmailActivity::class.java))
            }
            R.id.tv_sign_up -> {
                intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val currentUser = firebaseAuth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }else{
        }
    }

    private fun signIn() {
        val intent = googleSignInClient.signInIntent
        startActivityForResult(intent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseSignIn(account!!)
            }catch (e:ApiException){
                Log.w(TAG,"Result Failed = ${e.statusCode}",e)
                Toast.makeText(this,"Result Failed",Toast.LENGTH_LONG).show()

            }
        }
    }

    private fun firebaseSignIn(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken,null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
            if(task.isSuccessful){
                val user = firebaseAuth.currentUser
                updateUI(user)
            }else{
                Log.w(TAG,"Sign in Filed")
                Toast.makeText(this,"Sign in Filed",Toast.LENGTH_LONG).show()
            }
        }
    }
}
