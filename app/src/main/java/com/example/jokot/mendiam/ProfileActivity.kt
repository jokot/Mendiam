package com.example.jokot.mendiam

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity(), AppBarLayout.OnOffsetChangedListener {

    lateinit var mSectionPageAdapter: SectionPageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        mSectionPageAdapter = SectionPageAdapter(supportFragmentManager)

        container_profile.adapter = mSectionPageAdapter
        container_profile.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tab_layout_profile))
        tab_layout_profile.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container_profile))

        iv_back.setOnClickListener {
            intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        iv_toolbar_back.setOnClickListener {
            intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        app_bar_profile.addOnOffsetChangedListener(this)
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        animatedToolbar(verticalOffset)
    }

    private fun animatedToolbar(verticalOffset:Int){
        when(verticalOffset){
            in -240..-200 ->{
                tv_toolbar_profile.visibility = View.VISIBLE
                iv_toolbar_back.visibility = View.VISIBLE
                toolbar_profile.visibility = View.VISIBLE

//                tv_profile.visibility = View.GONE
//                iv_back.visibility = View.GONE
//                iv_profile.visibility = View.GONE
//                iv_back.visibility = View.GONE
//                tv_deskripsi.visibility = View.GONE
//                tv_follower.visibility = View.GONE
//                tv_jml_follower.visibility = View.GONE
//                tv_jml_following.visibility = View.GONE
//                tv_following.visibility = View.GONE
//                v_profile_horizontal.visibility = View.GONE
//                v_profile_vertical.visibility = View.GONE
            }

            in -170..0 ->{
//                tv_profile.visibility = View.VISIBLE
//                iv_back.visibility = View.VISIBLE
//                iv_profile.visibility = View.VISIBLE
//                iv_back.visibility = View.VISIBLE
//                tv_deskripsi.visibility = View.VISIBLE
//                tv_follower.visibility = View.VISIBLE
//                tv_jml_follower.visibility = View.VISIBLE
//                tv_jml_following.visibility = View.VISIBLE
//                tv_following.visibility = View.VISIBLE
//                v_profile_horizontal.visibility = View.VISIBLE
//                v_profile_vertical.visibility = View.VISIBLE

                toolbar_profile.visibility = View.GONE
                tv_toolbar_profile.visibility = View.GONE
                iv_toolbar_back.visibility = View.GONE
            }
        }
    }

    inner class SectionPageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment? {
            return when (position) {
                0 ->{
                    HomeFragment()
                }
                1 -> {
                    PeopleFragment()
                }
                else -> {
                    null
                }
            }
        }

        override fun getCount(): Int {
            return 2
        }

    }


}
