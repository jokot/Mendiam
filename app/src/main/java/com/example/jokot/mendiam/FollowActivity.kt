package com.example.jokot.mendiam

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.example.jokot.mendiam.callback.CallbackLoading
import com.example.jokot.mendiam.callback.CallbackString
import com.example.jokot.mendiam.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_follow.*


class FollowActivity : AppCompatActivity() {

    private val main = MainApps()
    private var database = main.database.reference
    private val uid = main.uid

    private lateinit var adapter: PeopleAdapter

    private var follow = ""
    private var listUser: MutableList<User> = mutableListOf()
    private var listUserId: MutableList<String> = mutableListOf()
    private var tempUserId: MutableList<String> = mutableListOf()

    private var listFollowingId: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_follow)

        follow = intent.getStringExtra("follow")
        tv_toolbar_name.text = follow.capitalize()
        iv_toolbar_back.setOnClickListener {
            finish()
        }

        initData()
        initRecycler()

        sr_follow.setOnRefreshListener {
            initData()
        }
    }

    private fun initRecycler() {
        adapter = PeopleAdapter(
            listUser, if (follow == main.follower) {
                listFollowingId
            } else {
                listUserId
            }, {

            }, {
                database.child(main.following).child(uid).child(it.id).removeValue()
                database.child(main.follower).child(it.id).child(uid).removeValue()
                listUser.remove(it)
            }, {
                database.child(main.following).child(uid).child(it.id).setValue(true)
                database.child(main.follower).child(it.id).child(uid).setValue(true)
            })

        rv_follow.adapter = adapter
        rv_follow.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }


    private fun initData() {
        rv_follow.visibility = View.GONE
        if (follow == main.follower) {
            getFollowingId(object : CallbackLoading {
                override fun onCallback() {
                    getUserId(object : CallbackLoading {
                        override fun onCallback() {
                            getUsers()
                        }
                    })
                }
            })
        } else {
            getUserId(object : CallbackLoading {
                override fun onCallback() {
                    getUsers()
                }
            })
        }
    }

    private fun getUserId(callbackLoading: CallbackLoading) {
        database.child(follow).child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val ds = dataSnapshot.children
                    tempUserId.clear()
                    ds.mapNotNull {
                        val id = it.key
                        tempUserId.add(id.toString())
                    }
                    listUserId.clear()
                    listUserId.addAll(tempUserId)
                    callbackLoading.onCallback()
                }

            })
    }

    private fun getFollowingId(callbackLoading: CallbackLoading) {
        database.child(main.following).child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val ds = dataSnapshot.children
                    tempUserId.clear()
                    ds.mapNotNull {
                        val id = it.key
                        tempUserId.add(id.toString())
                    }
                    listFollowingId.clear()
                    listFollowingId.addAll(tempUserId)
                    callbackLoading.onCallback()
                }

            })
    }

    private fun getUser(callbackString: CallbackString) {
        listUser.clear()
        for (id in listUserId) {
            database.child(main.user).child(id)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val userName = dataSnapshot.child("userName").getValue(String::class.java).toString()
                        val email = dataSnapshot.child("email").getValue(String::class.java).toString()
                        val about = dataSnapshot.child("about").getValue(String::class.java).toString()
                        listUser.add(User(id, userName, email, about))

                        adapter.notifyDataSetChanged()
                        callbackString.onCallback(id)
                    }
                })
        }
    }


    private fun getUsers() {
        rv_follow.visibility = View.GONE
        if (listUserId.size != 0) {
            val lastUId = listUserId[listUserId.size - 1]
            getUser(object : CallbackString {
                override fun onCallback(lastId: String) {
                    if (lastUId == lastId) {
                        rv_follow.visibility = View.VISIBLE
                        pb_follow.visibility = View.GONE
                        sr_follow.isRefreshing = false
                    }
                }
            })
        } else {
            listUser.clear()
            adapter.notifyDataSetChanged()
            rv_follow.visibility = View.VISIBLE
            pb_follow.visibility = View.GONE
            sr_follow.isRefreshing = false
        }
    }
}