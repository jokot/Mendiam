package com.example.jokot.mendiam

import android.os.Bundle
import android.view.View
import com.example.jokot.mendiam.callback.CallbackSCount
import com.example.jokot.mendiam.model.Story
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_new_story.*

class NewStoryActivity : BaseActivity(), View.OnClickListener {

    private var database = FirebaseDatabase.getInstance().reference
    private var auth = FirebaseAuth.getInstance()
    private var uid = auth.currentUser?.uid.toString()
    private var did = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_story)
        did = intent.getStringExtra("did")
        if (did != ""){
            getDraft()
        }
        iv_back.setOnClickListener(this)
        tv_publish.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val id = v?.id
        when (id) {
            R.id.iv_back -> finish()
            R.id.tv_publish -> {
                publish()
            }
        }
    }

    private fun publish(){
        readData(object : CallbackSCount {
            override fun onCallback(sCount: Int, id: String?) {
                val sid = id + (sCount.toString())
                val story = Story(
                    sid
                    , id.toString()
                    , et_judul.text.toString()
                    , et_story.text.toString()
                    , auth.currentUser?.displayName.toString()
                )

                database.child("story").child(sid).setValue(story)
                database.child("user").child(id.toString()).child("sCount").setValue(sCount)

                hideProgressDialog()
                finish()
            }
        })
    }

    private fun readData(callbackSCount: CallbackSCount) {
        showProgressDialog()
        val id = auth.currentUser?.uid

        database.child("user").child(id.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val count = dataSnapshot.child("sCount").getValue(Int::class.java)!!
                    callbackSCount.onCallback(count + 1, id)
                }
            })
    }

    private fun getDraft(){
        database.child("draft").child(uid).child(did)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val judul = dataSnapshot.child("judul").getValue(String::class.java)
                    val deskripsi = dataSnapshot.child("deskripsi").getValue(String::class.java)

                    et_judul.setText(judul)
                    et_story.setText(deskripsi)
                }

            })

    }
}
