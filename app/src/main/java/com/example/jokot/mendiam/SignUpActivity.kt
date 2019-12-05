package com.example.jokot.mendiam

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.example.jokot.mendiam.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*


class SignUpActivity : BaseActivity(), View.OnClickListener {

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]
    private var dataBase = FirebaseDatabase.getInstance()
    private var rootRef = dataBase.reference

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


//        btn_up_facebook.setOnClickListener(this)
        btn_up_google.setOnClickListener(this)
        tv_sign_in.setOnClickListener(this)
        btn_up_email.setOnClickListener(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        // [END config_signin]

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        val tv = btn_up_google.getChildAt(0) as TextView
        tv.text = getString(R.string.sign_up_with_google)
        tv.setTextColor(Color.parseColor("#000000"))
    }

    override fun onClick(v: View?) {
        val id = v?.id
        when (id) {
            R.id.btn_up_google -> signUp()
            R.id.btn_up_email -> startActivity(Intent(applicationContext, SignUpEmailActivity::class.java))
            R.id.tv_sign_in -> {
                intent = Intent(applicationContext, SignInActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            updateUI(currentUser, null)
        }

    }
    // [END on_start_check_user]

    // [START onactivityresult]
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_UP && resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed = ${e.statusCode}", e)
                // [START_EXCLUDE]
                updateUI(null, "Registrasi dengan Google gagal")
                // [END_EXCLUDE]
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct?.id)
        // [START_EXCLUDE silent]
        showProgressDialog()
        // [END_EXCLUDE]

        val credential = GoogleAuthProvider.getCredential(acct?.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information

                    Log.d(TAG, "signInWithCredential:success")

                    val user = auth.currentUser
                    val dataUser = user?.uid?.let {
                        User(
                            it,
                            user.displayName.toString(),
                            user.email.toString()
                        )
                    }
                    writeNewUser(dataUser)

                    updateUI(user, null)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
//                    Snackbar.make(main_layout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                    updateUI(null, "Gagal masuk, Coba Lagi")
                }

                // [START_EXCLUDE]
                hideProgressDialog()
                // [END_EXCLUDE]
            }
    }
    // [END auth_with_google]

    // [START signin]
    private fun signUp() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_UP)
    }
    // [END signin]

    private fun writeNewUser(user: User?) {
        rootRef
            .child("user")
            .child(auth.uid.toString())
            .setValue(user)
    }

    private fun updateUI(user: FirebaseUser?, msg: String?) {
        if (user != null) {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finishAffinity()
        } else {
            toast(this, msg)
        }
    }

    companion object {
        private const val TAG = "Sign Up Activity"
        private const val RC_SIGN_UP = 9001
    }
}
