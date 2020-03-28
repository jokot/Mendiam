package com.example.jokot.mendiam


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_interest.*


class InterestFragment : Fragment() {
    private lateinit var mSectionPagesAdapter: SectionPagesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_interest, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mSectionPagesAdapter = SectionPagesAdapter(childFragmentManager)

        container.adapter = mSectionPagesAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tab_layout))
        tab_layout.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))


    }

    inner class SectionPagesAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> {
                    TopicFragment()
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
