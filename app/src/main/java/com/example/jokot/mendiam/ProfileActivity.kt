package com.example.jokot.mendiam

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.View
import com.example.jokot.mendiam.callback.CallbackLoading
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : BaseActivity(), View.OnClickListener, AppBarLayout.OnOffsetChangedListener {


    private lateinit var mSectionPageAdapter: SectionPageAdapter

    private var firebaseAuth = FirebaseAuth.getInstance()

    private var firebaseUser = firebaseAuth.currentUser
    private var firebaseDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        mSectionPageAdapter = SectionPageAdapter(supportFragmentManager)

        initData()

        container_profile.adapter = mSectionPageAdapter
        container_profile.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tab_layout_profile))
        tab_layout_profile.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container_profile))

        iv_back.setOnClickListener(this)
        iv_toolbar_back.setOnClickListener(this)
        iv_more.setOnClickListener(this)
        cl_profile.setOnClickListener(this)
        tv_edit.setOnClickListener(this)
        constrain_profile.setOnClickListener(this)
        tv_keluar.setOnClickListener(this)
        ll_follower.setOnClickListener(this)
        ll_following.setOnClickListener(this)

        app_bar_profile.addOnOffsetChangedListener(this)
    }

    override fun onClick(v: View?) {
        val id = v?.id
        when (id) {
            R.id.iv_back -> {
                intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
            R.id.tv_keluar ->{
                firebaseAuth.signOut()
                intent = Intent(applicationContext,SignUpActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }

            R.id.ll_following -> {
                intent = Intent(this,FollowActivity::class.java)
                intent.putExtra("follow","following")
                startActivity(intent)
            }

            R.id.ll_follower -> {
                intent = Intent(this,FollowActivity::class.java)
                intent.putExtra("follow","follower")
                startActivity(intent)
            }

            R.id.tv_edit -> {
                intent = Intent(this, EditProfileActivity::class.java)
                startActivity(intent)
                finish()
            }

            R.id.cl_profile -> {
                ll_more.visibility = View.GONE
            }

            R.id.constrain_profile -> {
                ll_more.visibility = View.GONE
            }

            R.id.iv_more -> {

                ll_more.visibility = View.VISIBLE
            }
            R.id.iv_toolbar_back -> {
                intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
        }
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        animatedToolbar(verticalOffset)
    }

    private fun animatedToolbar(verticalOffset: Int) {
        when (verticalOffset) {
            in -240..-200 -> {
                tv_toolbar_profile.visibility = View.VISIBLE
                iv_toolbar_back.visibility = View.VISIBLE
                toolbar_profile.visibility = View.VISIBLE

            }

            in -170..0 -> {

                toolbar_profile.visibility = View.GONE
                tv_toolbar_profile.visibility = View.GONE
                iv_toolbar_back.visibility = View.GONE
            }
        }
    }


    private fun initData(){
        ll_more.visibility = View.GONE
        pb_profile.visibility = View.VISIBLE
        iv_profile.visibility = View.INVISIBLE
        tv_profile.visibility = View.INVISIBLE
        tv_about.visibility = View.INVISIBLE
        ll_follow.visibility = View.INVISIBLE

        getProfile(object : CallbackLoading{
            override fun onCallback() {
                iv_profile.visibility = View.VISIBLE
                tv_profile.visibility = View.VISIBLE
                tv_about.visibility = View.VISIBLE
                ll_follow.visibility = View.VISIBLE
                pb_profile.visibility = View.GONE

            }

        })
    }

    private fun getProfile(callbackLoading: CallbackLoading) {
        firebaseDatabase.child("user").child(firebaseUser!!.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val name = dataSnapshot.child("userName").getValue(String::class.java)
                    val autor = dataSnapshot.child("about").getValue(String::class.java)
                    val fingCount = dataSnapshot.child("fingCount").getValue(Int::class.java)
                    val ferCount = dataSnapshot.child("ferCount").getValue(Int::class.java)
                    val urlPic = dataSnapshot.child("urlPic").getValue(String::class.java)
                    tv_profile.text = name
                    tv_toolbar_profile.text = name

                    if (fingCount!=null){
                        tv_jml_following.text = fingCount.toString()
                    }
                    if(ferCount != null){
                        tv_jml_follower.text = ferCount.toString()
                    }
                    if(autor != null){
                        tv_about.text = autor
                    }
                    if(urlPic != ""){
                        Picasso.get().load(urlPic).into(iv_profile)
                    }
                    callbackLoading.onCallback()
                }
            })
    }

    inner class SectionPageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment? {
            return when (position) {
                0 -> {
                    PublishedFragment()
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
