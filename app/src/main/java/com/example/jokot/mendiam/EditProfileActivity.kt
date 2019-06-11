package com.example.jokot.mendiam

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.jokot.mendiam.callback.CallbackLoading
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity(), View.OnClickListener {

    private val main = MainApps()
    private var database = main.database.reference
    private var urlPic = ""

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
                main.showImagePicker(this)
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
        database.child(main.user).child(main.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val name = dataSnapshot.child("userName").getValue(String::class.java)
                    val about = dataSnapshot.child("about").getValue(String::class.java)
                    val urlPic = dataSnapshot.child("urlPic").getValue(String::class.java)
                    et_nama.setText(name)
                    if (about != "") {
                        et_about.setText(about)
                    }
                    if(urlPic != ""){
                        Picasso.get().load(urlPic).into(iv_edit)
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
        if(urlPic !=""){
            uploadUrlImage(urlPic)
        }
    }

    private fun dataBase(key: String, value: String) {
        main.editorPref(key,value,this)
        database.child(main.user)
            .child(main.uid)
            .child(key).setValue(value)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == main.G_CODE){
            val uri = data?.data
            main.showProgressBar(pb_edit)
            main.uploadImage(uri!!,{
                main.hideProgresBar(pb_edit)
                toast(it)
            },{
                main.hideProgresBar(pb_edit)
                toast(it)
            },{
                urlPic = it
                iv_edit.setImageURI(uri)
                main.hideProgresBar(pb_edit)
            })
        }
    }

    private fun uploadUrlImage(url:String){
        database.child(main.user).child(main.auth.uid.toString()).child("urlPic").setValue(url)
    }
}
