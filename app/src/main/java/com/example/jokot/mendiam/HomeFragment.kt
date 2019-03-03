package com.example.jokot.mendiam

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.jokot.mendiam.model.Story
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_home.*


//// TODO: Rename parameter arguments, choose names that match
//// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [HomeFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class HomeFragment : Fragment() {
//    // TODO: Rename and change types of parameters
//    private var param1: String? = null
//    private var param2: String? = null
//    private var listener: OnFragmentInteractionListener? = null

    //    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////        arguments?.let {
////            param1 = it.getString(ARG_PARAM1)
////            param2 = it.getString(ARG_PARAM2)
////        }
////    }

    private lateinit var homeAdapter: MainAdapter
    private var database = FirebaseDatabase.getInstance().reference
    private var list: MutableList<Story> = mutableListOf()
    private var temp: MutableList<Story> = mutableListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initRecycler()
        sr_home.setOnRefreshListener {
            rvMain.visibility = View.INVISIBLE
            getStory()
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    private fun initData(){
        pb_home.visibility = View.VISIBLE
        getStory()
    }

    private fun initRecycler() {
        homeAdapter = MainAdapter(list) {
            val intent = Intent(context, ReadActivity::class.java)
            intent.putExtra("sid", it.sid)
            startActivity(intent)
        }

        rvMain.adapter = homeAdapter
        rvMain.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private fun getStory() {
        database.child("story").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val ds = dataSnapshot.children
                temp.clear()
                ds.mapNotNull {
                    val sid = it.child("sid").getValue(String::class.java)
                    val uid = it.child("uid").getValue(String::class.java)
                    val judul = it.child("judul").getValue(String::class.java)
                    val deskripsi = it.child("deskripsi").getValue(String::class.java)
                    val name = it.child("name").getValue(String::class.java)
                    temp.add(
                        Story(
                            sid.toString(),
                            uid.toString(),
                            judul.toString(),
                            deskripsi.toString(),
                            name.toString()
                        )
                    )
                }
                pb_home.visibility = View.INVISIBLE
                list.clear()
                list.addAll(temp)
                homeAdapter.notifyDataSetChanged()
                sr_home.isRefreshing = false
                rvMain.visibility = View.VISIBLE
            }
        })
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    fun onButtonPressed(uri: Uri) {
//        listener?.onFragmentInteraction(uri)
//    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
//        }
//    }
//
//    override fun onDetach() {
//        super.onDetach()
//        listener = null
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     *
//     *
//     * See the Android Training lesson [Communicating with Other Fragments]
//     * (http://developer.android.com/training/basics/fragments/communicating.html)
//     * for more information.
//     */
//    interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        fun onFragmentInteraction(uri: Uri)
//    }
//
//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment HomeFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            HomeFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}
