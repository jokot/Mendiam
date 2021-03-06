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

    lateinit var adapter: StoryAdapter

    private var listBookmarkId: MutableList<String> = mutableListOf()

    //    private var tempStoryId : MutableList<StoryId> = mutableListOf()
    private var listBookmark: MutableList<Story> = mutableListOf()

    private val main = MainApps()
    private var database = main.database.reference
    private val uid = main.uid
    private lateinit var rvBookmark: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bookmark, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycle()
        initData()
        sr_bookmark.setOnRefreshListener {
            initData()
        }
    }

    private fun initRecycle() {
        adapter = StoryAdapter(listBookmark, listBookmarkId, {
            uid?.let { it1 ->
                database.child(main.bookmark).child(it1).child(it.sid).setValue(true)
            }
        }, {
            uid?.let { it1 -> database.child(main.bookmark).child(it1).child(it.sid).removeValue() }
            listBookmark.remove(it)
//            adapter.notifyDataSetChanged()
        }, {
            val intent = Intent(context, ReadActivity::class.java)
            intent.putExtra("sid", it.sid)
            startActivity(intent)
        })

        rvBookmark =  requireActivity().findViewById(R.id.rv_bookmark)

        rvBookmark.adapter = adapter
        rvBookmark.layoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )

    }

    private fun initData() {
        rvBookmark.visibility = View.GONE
        getBookmarkId(object : CallbackLoading {
            override fun onCallback() {
                getBookmarks()
            }
        })
    }

    private fun getBookmarkId(callbackLoading: CallbackLoading) {
        uid?.let { it ->
            database.child(main.bookmark).child(it)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val ds = dataSnapshot.children
                        listBookmarkId.clear()
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
    }

    private fun getBookmarks() {
        adapter.notifyDataSetChanged()
        rvBookmark.visibility = View.GONE
        if (listBookmarkId.size != 0) {
            val lastBId = listBookmarkId[listBookmarkId.size - 1]
            getBookmark(object : CallbackString {
                override fun onCallback(lastId: String) {
                    if (lastId == lastBId) {
                        rvBookmark.visibility = View.VISIBLE
                        if(pb_bookmark != null){
                            pb_bookmark.visibility = View.GONE
                        }
                        sr_bookmark.isRefreshing = false
                    }
                }
            })
        } else {
            listBookmark.clear()
            adapter.notifyDataSetChanged()
            rvBookmark.visibility = View.VISIBLE
            if(pb_bookmark != null){
                pb_bookmark.visibility = View.GONE
            }
            sr_bookmark.isRefreshing = false
        }
    }

    private fun getBookmark(callbackString: CallbackString) {
        listBookmark.clear()
        for (sid in listBookmarkId) {
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
                            listBookmark.add(
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
                            uid?.let {
                                database.child(main.bookmark).child(it).child(sid).removeValue()
                            }
                        }
//                        rvBookmark.recycledViewPool.clear()
                        adapter.notifyDataSetChanged()
                        callbackString.onCallback(sid)
                    }
                })

        }
    }
}
