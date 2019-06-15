package com.example.jokot.mendiam


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.jokot.mendiam.callback.CallbackLoading
import com.example.jokot.mendiam.callback.CallbackString
import com.example.jokot.mendiam.model.Story
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_published.*


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

    private lateinit var adapter: StoriesAdapter

    private var tempId: MutableList<String> = mutableListOf()
    private var listStoryId: MutableList<String> = mutableListOf()
    private var temp: MutableList<Story> = mutableListOf()
    private var listStory: MutableList<Story> = mutableListOf()

    private var database = FirebaseDatabase.getInstance().reference
    private var auth = FirebaseAuth.getInstance()
    private val uid = auth.currentUser?.uid.toString()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_published, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initRecycler()

        sr_my_story.setOnRefreshListener {
            initData()
        }
    }

    private fun initRecycler() {
        adapter = StoriesAdapter("published", listStory, {
            val intent = Intent(context, ReadActivity::class.java)
            intent.putExtra("sid", it.sid)
            startActivity(intent)
        }, {
            val intent = Intent(context, NewStoryActivity::class.java)
            intent.putExtra("sid", it.sid)
            startActivity(intent)
        }, {
            database.child("story").child(uid).child(it.sid).removeValue()
            listStory.remove(it)
            adapter.notifyDataSetChanged()
        })

        rv_my_story.adapter = adapter
        rv_my_story.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private fun getStories() {
        rv_my_story.visibility = View.GONE
        if (listStoryId.size != 0) {
            val lastSId = listStoryId[listStoryId.size - 1]
            getStory(object : CallbackString {
                override fun onCallback(lastId: String) {
                    if (lastId == lastSId) {
                        rv_my_story.visibility = View.VISIBLE
                        pb_my_story.visibility = View.GONE
                        sr_my_story.isRefreshing = false
                    }
                }
            })
        } else {
            listStory.clear()
            adapter.notifyDataSetChanged()
            rv_my_story.visibility = View.VISIBLE
            pb_my_story.visibility = View.GONE
            sr_my_story.isRefreshing = false
        }
    }

    private fun initData() {
        rv_my_story.visibility = View.GONE
        getSCount(object : CallbackLoading {
            override fun onCallback() {
                getStories()
            }
        })
    }

    private fun getSCount(callbackLoading: CallbackLoading) {
        database.child("user").child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val sCount = dataSnapshot.child("sCount").getValue(Int::class.java)
                    if (sCount != null) {
                        makeListStoryID(sCount)
                        activity?.log(sCount.toString())
                    }
                    callbackLoading.onCallback()
                }

            })
    }

    private fun makeListStoryID(sCount: Int) {
        listStoryId.clear()
        for (count in 1..sCount) {
            listStoryId.add(uid + count.toString())
            activity?.log(listStoryId[count - 1])
        }
    }


    private fun getStory(callbackString: CallbackString) {
        listStory.clear()
        activity?.log(listStoryId.toString())
        for (sid in listStoryId) {
            activity?.log(sid)
            database.child("story").child(sid)
                .addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onCancelled(p0: DatabaseError) {
                        activity?.toast(p0.message)
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val uid = dataSnapshot.child("uid").getValue(String::class.java)
                        val judul = dataSnapshot.child("judul").getValue(String::class.java)
                        val deskripsi = dataSnapshot.child("deskripsi").getValue(String::class.java)
                        val name = dataSnapshot.child("name").getValue(String::class.java)
                        val date = dataSnapshot.child("date").getValue(String::class.java)

                        if(uid!=null){
                            listStory.add(
                                Story(
                                    sid,
                                    uid.toString(),
                                    judul.toString(),
                                    deskripsi.toString(),
                                    name.toString(),
                                    date.toString()
                                )
                            )
                        }
                        adapter.notifyDataSetChanged()
                        callbackString.onCallback(sid)
                    }

                })
        }
    }
}
