package com.example.jokot.mendiam

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {

    lateinit var mSectionPageAdapter: SectionPageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        mSectionPageAdapter = SectionPageAdapter(supportFragmentManager)

        iv_back.setOnClickListener {
            finish()
        }

        container_search.adapter = mSectionPageAdapter
        container_search.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tab_layout_search))
        tab_layout_search.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container_search))
    }

    inner class SectionPageAdapter(fm:FragmentManager):FragmentPagerAdapter(fm){
        override fun getItem(position: Int): Fragment? {
            return when(position){
                0->{
                    HomeFragment()
                }
                1->{

                    PeopleFragment()
                }
                else->{
                    null
                }
            }
        }

        override fun getCount(): Int {
            return 2
        }

    }
}
