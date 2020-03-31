package com.example.jokot.mendiam


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_topic.*


class TopicFragment : Fragment() {
    lateinit var topicAdapter: TopicAdapter
    private lateinit var rvTopic : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_topic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        topicAdapter = TopicAdapter {
            //            val intent = Intent(context, ReadActivity::class.java)
//            startActivity(intent)
            requireActivity().toast("Coming soon")
        }

        rvTopic = requireActivity().findViewById(R.id.rv_topic)
        rvTopic.adapter = topicAdapter
        rvTopic.layoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
    }

}
