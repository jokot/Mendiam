package com.example.jokot.mendiam


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jokot.mendiam.callback.CallbackLoading
import com.example.jokot.mendiam.callback.CallbackString
import com.example.jokot.mendiam.model.Story
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_published.*


class PublishedFragment : Fragment() {

    private lateinit var adapter: StoriesAdapter

    private var listStoryId: MutableList<String> = mutableListOf()
    private var listStory: MutableList<Story> = mutableListOf()

    private var database = FirebaseDatabase.getInstance().reference
    private var uid = ""
    private val main = MainApps()

    private lateinit var rvMyStory : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        uid = arguments?.getString(main.userId).toString()

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_published, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
        initData()
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
            intent.putExtra("edit", "true")
            startActivity(intent)
        }, {
            database.child("story").child(it.sid).removeValue()
            database.child("storyContent").child(it.sid).removeValue()
            listStory.remove(it)
            adapter.notifyDataSetChanged()
        })

        rvMyStory = requireActivity().findViewById(R.id.rv_my_story)
        rvMyStory.adapter = adapter
        rvMyStory.layoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
    }

    private fun getStories() {
        rvMyStory.visibility = View.GONE
        if (listStoryId.size != 0) {
            val lastSId = listStoryId[listStoryId.size - 1]
            getStory(object : CallbackString {
                override fun onCallback(lastId: String) {
                    if (lastId == lastSId) {
                        rvMyStory.visibility = View.VISIBLE
                        if(pb_my_story != null){
                            pb_my_story.visibility = View.GONE
                        }
                        sr_my_story.isRefreshing = false
                    }
                }
            })
        } else {
            listStory.clear()
            adapter.notifyDataSetChanged()
            rvMyStory.visibility = View.VISIBLE
            if(pb_my_story != null){
                pb_my_story.visibility = View.GONE
            }
            sr_my_story.isRefreshing = false
        }
    }

    private fun initData() {
        rvMyStory.visibility = View.GONE
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
                        log(sCount.toString())
                    }
                    callbackLoading.onCallback()
                }

            })
    }

    private fun makeListStoryID(sCount: Int) {
        listStoryId.clear()
        for (count in 1..sCount) {
            listStoryId.add(uid + count.toString())
            log(listStoryId[count - 1])
        }
    }


    private fun getStory(callbackString: CallbackString) {
        listStory.clear()
        log(listStoryId.toString())
        for (sid in listStoryId) {
            log(sid)
            database.child("story").child(sid)
                .addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onCancelled(p0: DatabaseError) {
                        activity?.toast(p0.message)
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val uid = dataSnapshot.child("uid").getValue(String::class.java)
                        val tittle = dataSnapshot.child("judul").getValue(String::class.java)
                        val description = dataSnapshot.child("deskripsi").getValue(String::class.java)
                        val name = dataSnapshot.child("name").getValue(String::class.java)
                        val date = dataSnapshot.child("date").getValue(String::class.java)

                        if (uid != null) {
                            listStory.add(
                                Story(
                                    sid,
                                    uid.toString(),
                                    tittle.toString(),
                                    description.toString(),
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
