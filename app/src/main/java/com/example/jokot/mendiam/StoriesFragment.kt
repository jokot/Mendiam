package com.example.jokot.mendiam


import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_stories.*

//
//// TODO: Rename parameter arguments, choose names that match
//// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"
//
///**
// * A simple [Fragment] subclass.
// *
// */
class StoriesFragment : Fragment() {

    private lateinit var mSectionPageAdapter: SectionPagesAdapter
    private val main = MainApps()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mSectionPageAdapter = SectionPagesAdapter(childFragmentManager)

        vp_stories.adapter = mSectionPageAdapter

        vp_stories.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tl_stories))
        tl_stories.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(vp_stories))

    }

    inner class SectionPagesAdapter(fm : FragmentManager) : FragmentPagerAdapter(fm){
        override fun getItem(position: Int): Fragment {
            return when(position){
                0 -> {
                    val bundle = Bundle()
                    bundle.putString(main.userId, main.getUId())
                    val fragment = DraftFragment()
                    fragment.arguments = bundle
                    fragment
                }
                else-> {
                    val bundle = Bundle()
                    bundle.putString(main.userId, main.getUId())
                    val fragment = PublishedFragment()
                    fragment.arguments = bundle
                    fragment
                }
            }
        }

        override fun getCount(): Int {
            return 2
        }

    }

}
