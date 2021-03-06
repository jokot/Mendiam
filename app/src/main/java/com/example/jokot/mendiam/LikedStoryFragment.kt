package com.example.jokot.mendiam

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_liked_story.*

class LikedStoryFragment : Fragment() {

    lateinit var adapter: StoryAdapter

    private var listLikedStoryId: MutableList<String> = mutableListOf()

    //    private var tempStoryId : MutableList<StoryId> = mutableListOf()
    private var listStory: MutableList<Story> = mutableListOf()

    private var listBookmarkId: MutableList<String> = mutableListOf()

    private val main = MainApps()
    private var database = main.database.reference
    private val myUid = main.getUId()
    private var uid = ""
    private lateinit var rvLikedStory : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        uid = arguments?.getString(main.userId).toString()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_liked_story, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycle()
        initData()
        sr_liked_story.setOnRefreshListener {
            initData()
        }
    }

    private fun initRecycle() {
        adapter = StoryAdapter(listStory, listBookmarkId, {
            uid.let { it1 ->
                database.child(main.likedStory).child(it1).child(it.sid).setValue(true)
            }
        }, {
            uid.let { it1 ->
                database.child(main.likedStory).child(it1).child(it.sid).removeValue()
            }
            listStory.remove(it)
//            adapter.notifyDataSetChanged()
        }, {
            val intent = Intent(context, ReadActivity::class.java)
            intent.putExtra("sid", it.sid)
            startActivity(intent)
        })

        rvLikedStory = requireActivity().findViewById(R.id.rv_liked_story)
        rvLikedStory.adapter = adapter
        rvLikedStory.layoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )

    }

    private fun initData() {
        rvLikedStory.visibility = View.GONE
        getBookmarkId(object : CallbackLoading {
            override fun onCallback() {
                getLikedStoryId(object : CallbackLoading {
                    override fun onCallback() {
                        getStories()
                    }
                })
            }
        })
    }

    private fun getLikedStoryId(callbackLoading: CallbackLoading) {
        uid.let { uid ->
            database.child(main.likedStory).child(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        log(p0.message)
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val ds = dataSnapshot.children
                        listLikedStoryId.clear()
                        ds.mapNotNull {
                            val id = it.key
                            if (id != null) {
                                listLikedStoryId.add(id)
                            }
                        }
                        callbackLoading.onCallback()
                    }
                })
        }
    }

    private fun getStories() {
        try {
            adapter.notifyDataSetChanged()
            rvLikedStory.visibility = View.GONE
            if (listLikedStoryId.size != 0) {
                val lastBId = listLikedStoryId[listLikedStoryId.size - 1]
                getStory(object : CallbackString {
                    override fun onCallback(lastId: String) {
                        if (lastId == lastBId) {
                            rvLikedStory.visibility = View.VISIBLE
                            if(pb_liked_story != null){
                                pb_liked_story.visibility = View.GONE
                            }
                            sr_liked_story.isRefreshing = false
                        }
                    }
                })
            } else {
                listStory.clear()
                adapter.notifyDataSetChanged()
                rvLikedStory.visibility = View.VISIBLE
                if(pb_liked_story != null){
                    pb_liked_story.visibility = View.GONE
                }
                sr_liked_story.isRefreshing = false
            }
        } catch (e: Exception) {
            e.message?.let { log(it) }
        }
    }

    private fun getBookmarkId(callbackLoading: CallbackLoading) {
        myUid.let { uid ->
            database.child(main.bookmark).child(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        Log.d("getBookmarkId", error.message)
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val ds = dataSnapshot.children
                        listBookmarkId.clear()
                        ds.mapNotNull {
                            val bid = it.key
                            if (bid != null) {
                                listBookmarkId.add(bid)
                            }

                        }
                        callbackLoading.onCallback()
                    }

                })
        }
    }


    private fun getStory(callbackString: CallbackString) {
        listStory.clear()
        for (sid in listLikedStoryId) {
            database.child(main.story).child(sid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val id = dataSnapshot.child("uid").getValue(String::class.java)
                        val tittle = dataSnapshot.child("judul").getValue(String::class.java)
                        val description = dataSnapshot.child("deskripsi").getValue(String::class.java)
                        val name = dataSnapshot.child("name").getValue(String::class.java)
                        val date = dataSnapshot.child("date").getValue(String::class.java)
                        val image = dataSnapshot.child("image").getValue(String::class.java)
                        if (id != null) {
                            listStory.add(
                                Story(
                                    sid,
                                    id.toString(),
                                    tittle.toString(),
                                    description.toString(),
                                    name.toString(),
                                    date.toString(),
                                    image.toString()
                                )
                            )
                        } else {
                            uid.let {
                                database.child(main.likedStory).child(it).child(sid).removeValue()
                            }
                        }
                        adapter.notifyDataSetChanged()
                        callbackString.onCallback(sid)
                    }
                })
        }
    }
}
