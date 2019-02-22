package com.example.jokot.mendiam

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity(), AppBarLayout.OnOffsetChangedListener {

    lateinit var mSectionPageAdapter: SectionPageAdapter

    private var firebaseAuth = FirebaseAuth.getInstance()

    private var firebaseUser = firebaseAuth.currentUser
    private var firebaseDatabase:DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        mSectionPageAdapter = SectionPageAdapter(supportFragmentManager)

        loadData()

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

        btn_edit.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this,SignUpActivity::class.java))
            finish()
//            intent = Intent(this,EditProfileActivity::class.java)
//            startActivity(intent)
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

            }

            in -170..0 ->{

                toolbar_profile.visibility = View.GONE
                tv_toolbar_profile.visibility = View.GONE
                iv_toolbar_back.visibility = View.GONE
            }
        }
    }

    private fun loadData(){
        tv_profile.text = firebaseAuth.currentUser?.displayName

        firebaseDatabase.child("user").child(firebaseUser!!.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val ds = dataSnapshot.children
                    ds.mapNotNull {

                    }
                }
            })

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
