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
import com.example.jokot.mendiam.model.Story
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_draft.*


//// TODO: Rename parameter arguments, choose names that match
//// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"
//
///**
// * A simple [Fragment] subclass.
// *
// */
class DraftFragment : Fragment() {

    private lateinit var adapter: StoriesAdapter
    private var listDraft: MutableList<Story> = mutableListOf()
    private var tempListDraft: MutableList<Story> = mutableListOf()

    private val main = MainApps()
    private var database = main.database.reference

    private var uid = ""
    private lateinit var rvDraft : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        uid = arguments?.getString(main.userId).toString()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_draft, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
        initData()
        sr_draft.setOnRefreshListener {
            initData()
        }
    }

    private fun initRecycler() {
        adapter = StoriesAdapter("Edited", listDraft, {
            val intent = Intent(context, NewStoryActivity::class.java)
            intent.putExtra("did", it.sid)
            startActivity(intent)
        }, {
            val intent = Intent(context, NewStoryActivity::class.java)
            intent.putExtra("did", it.sid)
            startActivity(intent)
        }, {
            uid.let { it1 -> database.child(main.draft).child(it1).child(it.sid).removeValue() }
            uid.let { it1 ->
                database.child(main.draftContent).child(it1).child(it.sid).removeValue()
            }
            listDraft.remove(it)
            adapter.notifyDataSetChanged()
        })

        rvDraft = requireActivity().findViewById(R.id.rv_draft)
        rvDraft.adapter = adapter
        rvDraft.layoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
    }

    private fun initData() {
        rvDraft.visibility = View.GONE
        getDraft(object : CallbackLoading {
            override fun onCallback() {
                rvDraft.visibility = View.VISIBLE
                if(pb_draft !=null){
                    pb_draft.visibility = View.GONE
                }
                sr_draft.isRefreshing = false
            }

        })
    }

    private fun getDraft(callbackLoading: CallbackLoading) {
        uid.let { it ->
            database.child(main.draft).child(it)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val ds = dataSnapshot.children
                        tempListDraft.clear()
                        ds.mapNotNull {
                            val tittle = it.child("judul").getValue(String::class.java).toString()
                            val description =
                                it.child("deskripsi").getValue(String::class.java).toString()
                            val did = it.child("did").getValue(String::class.java).toString()
                            val date = it.child("date").getValue(String::class.java).toString()
                            val uid = main.getUId()
                            tempListDraft.add(
                                Story(
                                    uid = uid,
                                    judul = tittle,
                                    deskripsi = description,
                                    sid = did,
                                    date = date
                                )
                            )
                        }
                        listDraft.clear()
                        listDraft.addAll(tempListDraft)
                        adapter.notifyDataSetChanged()
                        callbackLoading.onCallback()
                    }
                })
        }
    }
}
