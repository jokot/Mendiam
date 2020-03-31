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
import com.example.jokot.mendiam.model.Story
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {
    private lateinit var adapter: StoryAdapter
    private val main = MainApps()
    private var database = main.database.reference
    private val uid = main.uid

    private var listStory: MutableList<Story> = mutableListOf()
    private var tempStory: MutableList<Story> = mutableListOf()
    private var listBookmarkId: MutableList<String> = mutableListOf()

    private lateinit var rvHome : RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
        initData()
        sr_home.setOnRefreshListener {
            initData()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    private fun initData() {
        rvHome.visibility = View.INVISIBLE
        listStory.clear()
        listBookmarkId.clear()

        getBookmarkId(object : CallbackLoading {
            override fun onCallback() {
                adapter.notifyDataSetChanged()
                getStory()
                if(pb_home != null){
                    pb_home.visibility = View.GONE
                }
                sr_home.isRefreshing = false
                rvHome.visibility = View.VISIBLE
            }
        })
    }

    private fun initRecycler() {
        adapter = StoryAdapter(listStory, listBookmarkId, {
            uid?.let { it1 ->
                database.child(main.bookmark).child(it1).child(it.sid).setValue(true)
            }

        }, {
            uid?.let { it1 -> database.child(main.bookmark).child(it1).child(it.sid).removeValue() }
//            listStory.remove(it)
//            adapter.notifyDataSetChanged()
        }, {
            val intent = Intent(context, ReadActivity::class.java)
            intent.putExtra("sid", it.sid)
            startActivity(intent)
        })

        rvHome = requireActivity().findViewById(R.id.rv_home)
        rvHome.adapter = adapter
        rvHome.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
    }

    private fun getStory() {
        database.child(main.story).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val ds = dataSnapshot.children
                tempStory.clear()
                ds.mapNotNull {
                    //                        it ->
                    val sid = it.child("sid").getValue(String::class.java)
                    val uid = it.child("uid").getValue(String::class.java)
                    val tittle = it.child("judul").getValue(String::class.java)
                    val description = it.child("deskripsi").getValue(String::class.java)
                    val date = it.child("date").getValue(String::class.java)
                    val image = it.child("image").getValue(String::class.java)

                    val name = it.child("name").getValue(String::class.java)
//                    main.getUName(uid!!){
//                        activity?.toast(it)
//                        name=it
//                    }
                    tempStory.add(
                        Story(
                            sid.toString(),
                            uid.toString(),
                            tittle.toString(),
                            description.toString(),
                            name.toString(),
                            date.toString(),
                            image.toString()
                        )
                    )
                }

                listStory.clear()
                listStory.addAll(tempStory)
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun getBookmarkId(callbackLoading: CallbackLoading) {
        uid?.let { uid ->
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

}
