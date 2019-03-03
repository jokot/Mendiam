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
import kotlinx.android.synthetic.main.fragment_bookmark.*


//// TODO: Rename parameter arguments, choose names that match
//// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"
//
///**
// * A simple [Fragment] subclass.
// *
// */
class BookmarkFragment : Fragment() {

    lateinit var bookmarkAdapter: MainAdapter

    private var listBookmarkId: MutableList<String> = mutableListOf()

    //    private var tempStoryId : MutableList<StoryId> = mutableListOf()
    private var listBookmark: MutableList<Story> = mutableListOf()

    private var database = FirebaseDatabase.getInstance().reference
    private var auth = FirebaseAuth.getInstance()

    private val uid = auth.currentUser?.uid.toString()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bookmark, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initRecycle()
        sr_bookmark.setOnRefreshListener {
            getBookmark()
        }
    }

    private fun initRecycle() {
        bookmarkAdapter = MainAdapter(listBookmark){

        }

        rv_bookmark.adapter = bookmarkAdapter
        rv_bookmark.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

    }

    private fun initData() {
        pb_bookmark.visibility = View.VISIBLE
        getBookmarkId(object : CallbackLoading {
            override fun onCallback() {
                getBookmark()
                pb_bookmark.visibility = View.GONE
            }
        })
    }

    private fun getBookmarkId(callbackLoading: CallbackLoading) {
        database.child("bookmark").child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val ds = dataSnapshot.children
                    ds.mapNotNull {
                        val id = it.key
                        if (id != null) {
                            listBookmarkId.add(id)
                        }
                    }
                    callbackLoading.onCallback()
                }
            })
    }

    private fun getBookmark() {
        listBookmark.clear()
        for (id in listBookmarkId) {
            database.child("story").child(id)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val sid = dataSnapshot.child("sid").getValue(String::class.java)
                        val uid = dataSnapshot.child("uid").getValue(String::class.java)
                        val judul = dataSnapshot.child("judul").getValue(String::class.java)
                        val deskripsi = dataSnapshot.child("deskripsi").getValue(String::class.java)
                        val name = dataSnapshot.child("name").getValue(String::class.java)
                        listBookmark.add(
                            Story(
                                sid.toString(),
                                uid.toString(),
                                judul.toString(),
                                deskripsi.toString(),
                                name.toString()
                            )
                        )

                        bookmarkAdapter.notifyDataSetChanged()
                        sr_bookmark.isRefreshing = false
                    }
                })
        }

    }
}
