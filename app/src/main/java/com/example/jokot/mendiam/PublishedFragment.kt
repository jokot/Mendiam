package com.example.jokot.mendiam


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.jokot.mendiam.callback.CallbackLoading
import com.example.jokot.mendiam.model.Story
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_my_story.*


//// TODO: Rename parameter arguments, choose names that match
//// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"
//
///**
// * A simple [Fragment] subclass.
// *
// */
class PublishedFragment : Fragment() {

    private lateinit var adapter : StoriesAdapter

    private var listStoryId : MutableList<String> = mutableListOf()
    private var listStory : MutableList<Story> = mutableListOf()

    private var database = FirebaseDatabase.getInstance().reference
    private var auth = FirebaseAuth.getInstance()
    private val uid = auth.currentUser?.uid.toString()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_story, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initRecycler()

        sr_my_story.setOnRefreshListener {
            getStory()
        }
    }

    private fun initRecycler(){
        adapter = StoriesAdapter("published",listStory){

        }

        rv_my_story.adapter = adapter
        rv_my_story.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
    }

    private fun initData(){
        pb_my_story.visibility = View.VISIBLE
        getSCount(object : CallbackLoading{
            override fun onCallback() {
                getStory()
                pb_my_story.visibility = View.GONE
            }

        })
    }

    private fun getSCount(callbackLoading: CallbackLoading){
        database.child("user").child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val sCount = dataSnapshot.child("sCount").getValue(Int::class.java)
                    if (sCount != null) {
                        makeListStoryID(sCount)
                    }
                    callbackLoading.onCallback()
                }

            })
    }

    private fun makeListStoryID(sCount: Int) {
        for(count in 1..sCount){
            listStoryId.add(uid+count.toString())
        }
    }


    private fun getStory() {
        listStory.clear()
        for(sid in listStoryId){
            database.child("story").child(sid)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val uid = dataSnapshot.child("uid").getValue(String::class.java)
                        val judul = dataSnapshot.child("judul").getValue(String::class.java)
                        val deskripsi = dataSnapshot.child("deskripsi").getValue(String::class.java)
                        val name = dataSnapshot.child("name").getValue(String::class.java)
                        listStory.add(
                            Story(
                                sid,
                                uid.toString(),
                                judul.toString(),
                                deskripsi.toString(),
                                name.toString()
                            )
                        )
                        adapter.notifyDataSetChanged()
                        sr_my_story.isRefreshing = false
                    }

                })
        }
    }
}
