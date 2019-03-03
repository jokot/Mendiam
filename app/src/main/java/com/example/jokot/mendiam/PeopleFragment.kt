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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_people.*

class PeopleFragment : Fragment() {

    private lateinit var peopleAdapter: PeopleAdapter

    private var list: MutableList<User> = mutableListOf()
    private var temp: MutableList<User> = mutableListOf()

    private var fbDatabase = FirebaseDatabase.getInstance().reference


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
            rv_people.visibility = View.INVISIBLE
            initData()
        }
    }

    private fun initRecycler() {
        peopleAdapter = PeopleAdapter(list) {
            val intent = Intent(context, ProfileActivity::class.java)
            startActivity(intent)
        }
        rv_people.adapter = peopleAdapter
        rv_people.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private fun initData() {
        getPeople(object : CallbackLoading{
            override fun onCallback() {
                pb_people.visibility = View.GONE
                sr_people.isRefreshing = false
            }

        })
    }

    private fun getPeople(callbackLoading: CallbackLoading) {
        fbDatabase.child("user").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val ds = dataSnapshot.children
                temp.clear()
                ds.mapNotNull {
                    val id = it.child("id").getValue(String::class.java)
                    val userName = it.child("userName").getValue(String::class.java)
                    val email = it.child("email").getValue(String::class.java)

                    temp.add(User(id.toString(), userName.toString(), email.toString()))
                }
                list.clear()
                list.addAll(temp)
                peopleAdapter.notifyDataSetChanged()
                rv_people.visibility = View.VISIBLE
                sr_people.isRefreshing = false
                callbackLoading.onCallback()
            }
        })
    }


}
