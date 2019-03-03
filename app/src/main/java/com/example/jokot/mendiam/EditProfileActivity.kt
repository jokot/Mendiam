package com.example.jokot.mendiam

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.jokot.mendiam.callback.CallbackLoading
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity(), View.OnClickListener {

    private var database = FirebaseDatabase.getInstance().reference
    private var auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        initData()

        iv_close.setOnClickListener(this)
        tv_save.setOnClickListener(this)
        ll_chose_img.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        val id = v?.id
        when (id) {
            R.id.iv_close -> {
                startActivity(Intent(applicationContext,ProfileActivity::class.java))
                finish()
            }
            R.id.tv_save -> {
                saveData()
                finish()
                startActivity(Intent(applicationContext,ProfileActivity::class.java))
            }
            R.id.ll_chose_img -> {

            }
        }
    }

    private fun initData() {
        pb_edit.visibility = View.VISIBLE
        getData(object : CallbackLoading {
            override fun onCallback() {
                pb_edit.visibility = View.GONE
            }
        })
    }

    private fun getData(callbackLoading: CallbackLoading) {
        database.child("user").child(auth.currentUser?.uid.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val name = dataSnapshot.child("userName").getValue(String::class.java)
                    val about = dataSnapshot.child("about").getValue(String::class.java)
//                    val image =
                    et_nama.setText(name)
                    if (about != null) {
                        et_about.setText(about)
                    }
                    callbackLoading.onCallback()
                }

            })
    }

    private fun saveData() {
        val name = et_nama.text.toString()
        val about = et_about.text.toString()
        dataBase("userName", name)
        dataBase("about", about)
    }

    private fun dataBase(key: String, value: Any) {
        database.child("user")
            .child(auth.currentUser?.uid.toString())
            .child(key).setValue(value)
    }
}
