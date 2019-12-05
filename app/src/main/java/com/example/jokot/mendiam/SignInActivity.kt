package com.example.jokot.mendiam

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.example.jokot.mendiam.model.Story
import com.example.jokot.mendiam.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.util.UidVerifier
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : BaseActivity(), View.OnClickListener {

    companion object {
        private const val RC_SIGN_IN = 9001
        private const val TAG = "Sign In Activity"
    }

    private lateinit var firebaseAuth: FirebaseAuth
    private var firebaseUser: FirebaseUser? = null
    private lateinit var googleSignInClient: GoogleSignInClient
    private var dataBase = FirebaseDatabase.getInstance()
    private var rootRef = dataBase.reference

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

        tv_sign_up.setOnClickListener(this)
//        btn_in_google.setOnClickListener(this)
        btn_in_email.setOnClickListener(this)
        google_button.setOnClickListener(this)

        val tv = google_button.getChildAt(0) as TextView
        tv.text = getString(R.string.sign_in_with_google)
        tv.setTextColor(Color.parseColor("#000000"))
    }

    override fun onClick(v: View?) {
        val id = v?.id
        when (id) {
            R.id.google_button -> signIn()
//            R.id.btn_in_google -> signIn()
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
        if (currentUser != null) {
            updateUI(currentUser, null)
        }
    }

    private fun updateUI(currentUser: FirebaseUser?, msg: String?) {
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        } else {
            toast(this, msg)
        }
    }

    private fun signIn() {
        val intent = googleSignInClient.signInIntent
        startActivityForResult(intent, RC_SIGN_IN)
    }
    private fun writeNewUser(user: User?) {
        rootRef
            .child("user")
            .child(firebaseAuth.uid.toString())
            .setValue(user)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseSignIn(account)
            } catch (e: ApiException) {
                Log.w(TAG, "Result Failed = ${e.statusCode}", e)
                updateUI(null, "Gagal Masuk")

            }
        }
    }

    private fun firebaseSignIn(account: GoogleSignInAccount?) {
        showProgressDialog()
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val dataUser = user?.uid?.let {
                        User(
                            it,
                            user.displayName.toString(),
                            user.email.toString()
                        )
                    }
                    user?.uid?.let { it ->
                        isRegistered(it){
                            if (!it){
                                writeNewUser(dataUser)
                            }
                            updateUI(user, null)
                        }
                    }


                } else {
                    Log.w(TAG, "Sign in Filed")
                    updateUI(null, "Gagal Masuk, Coba lagi")
                }
                hideProgressDialog()
            }
    }

    fun isRegistered(uid: String, onSuccess : (Boolean) -> Unit){
        rootRef.child("user").child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val isRegister = dataSnapshot.hasChildren()

                    if(isRegister){
                        log("isRegistered registered $isRegister")
                        onSuccess(true)
                    }else{
                        log("isRegistered not registered $isRegister")
                        onSuccess(false)

                    }
                }
            })
    }
}
