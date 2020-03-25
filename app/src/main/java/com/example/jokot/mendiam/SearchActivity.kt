package com.example.jokot.mendiam

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {

    private lateinit var mSectionPageAdapter: SectionPageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        mSectionPageAdapter = SectionPageAdapter(supportFragmentManager)

        setUpOnClick()

        container_search.adapter = mSectionPageAdapter
        container_search.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                tab_layout_search
            )
        )
        tab_layout_search.addOnTabSelectedListener(
            TabLayout.ViewPagerOnTabSelectedListener(
                container_search
            )
        )
    }

    private fun setUpOnClick() {
        iv_back.setOnClickListener {
            finish()
        }
        iv_search.setOnClickListener {
            toast("Coming soon")
        }
    }

    inner class SectionPageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> {
                    HomeFragment()
                }
                else -> {

                    PeopleFragment()
                }
            }
        }

        override fun getCount(): Int {
            return 2
        }

    }
}
