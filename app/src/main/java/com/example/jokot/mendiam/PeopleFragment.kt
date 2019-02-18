package com.example.jokot.mendiam


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_people.*

class PeopleFragment : Fragment() {

    private lateinit var peopleAdapter: PeopleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
    }

    private fun initRecycler() {
        peopleAdapter = PeopleAdapter {
            val intent = Intent(context, ProfileActivity::class.java)
            startActivity(intent)
        }

        rv_people.adapter = peopleAdapter
        rv_people.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }
}
