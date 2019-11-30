package com.example.jokot.mendiam


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.jokot.mendiam.callback.CallbackLoading
import com.example.jokot.mendiam.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_people.*

class PeopleFragment : Fragment() {

    private lateinit var peopleAdapter: PeopleAdapter

    private var listUser: MutableList<User> = mutableListOf()
    private var temp: MutableList<User> = mutableListOf()
    private var listFollowingId : MutableList<String> = mutableListOf()
    private var tempId: MutableList<String> = mutableListOf()

    private var database = FirebaseDatabase.getInstance().reference
    private var auth = FirebaseAuth.getInstance()
    private val main = MainApps()
    private var uid = auth.currentUser?.uid.toString()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initRecycler()
        sr_people.setOnRefreshListener {
            initData()
        }
    }

    private fun initRecycler() {
        peopleAdapter = PeopleAdapter(listUser,listFollowingId, {
            val intent = Intent(context, ProfileActivity::class.java)
            intent.putExtra(main.userId,it.id)
            startActivity(intent)
        },{
            database.child("following").child(uid).child(it.id).removeValue()
            database.child("follower").child(it.id).child(uid).removeValue()
        },{
            database.child("following").child(uid).child(it.id).setValue(true)
            database.child("follower").child(it.id).child(uid).setValue(true)
        })
        rv_people.adapter = peopleAdapter
        rv_people.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private fun initData() {
        rv_people.visibility = View.GONE
        getFollowingId(object : CallbackLoading{
            override fun onCallback() {
                getPeople(object : CallbackLoading{
                    override fun onCallback() {
                        rv_people.visibility = View.VISIBLE
                        pb_people.visibility = View.GONE
                        sr_people.isRefreshing = false
                    }
                })
            }

        })

    }

    private fun getFollowingId(callbackLoading: CallbackLoading){
        database.child("following").child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    requireActivity().log(p0.message)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val ds = dataSnapshot.children
                    tempId.clear()
                    ds.mapNotNull {
                        val id = it.key
                        if(id != null){
                            tempId.add(id.toString())
                        }
                    }
                    listFollowingId.clear()
                    listFollowingId.addAll(tempId)
                    callbackLoading.onCallback()
                }

            })
    }

    private fun getPeople(callbackLoading: CallbackLoading) {
        database.child("user").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                requireActivity().log(p0.message)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val ds = dataSnapshot.children
                temp.clear()
                ds.mapNotNull {
                    val id = it.child("id").getValue(String::class.java).toString()
                    val userName = it.child("userName").getValue(String::class.java).toString()
                    val email = it.child("email").getValue(String::class.java).toString()
                    val about = it.child("about").getValue(String::class.java).toString()
                    val image = it.child("urlPic").getValue(String::class.java).toString()

                    if(id != uid && id != "null"){
                        temp.add(User(id, userName, email,about,image))
                    }
                }
                listUser.clear()
                listUser.addAll(temp)
                peopleAdapter.notifyDataSetChanged()
                rv_people.visibility = View.VISIBLE
                sr_people.isRefreshing = false
                callbackLoading.onCallback()
            }
        })
    }
}
