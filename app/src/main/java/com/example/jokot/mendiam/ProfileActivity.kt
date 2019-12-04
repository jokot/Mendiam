package com.example.jokot.mendiam

import android.app.Activity
import android.content.Context
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
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.File

class ProfileActivity : BaseActivity(), View.OnClickListener, AppBarLayout.OnOffsetChangedListener {

    private lateinit var mSectionPageAdapter: SectionPageAdapter

    private var firebaseAuth = FirebaseAuth.getInstance()

    private var database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private var main = MainApps()

    private val editId = 1

    private var uid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        mSectionPageAdapter = SectionPageAdapter(supportFragmentManager)

        uid = intent.getStringExtra(main.userId)

        changeUiIfNotMe()

        initData()

        container_profile.adapter = mSectionPageAdapter
        container_profile.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                tab_layout_profile
            )
        )
        tab_layout_profile.addOnTabSelectedListener(
            TabLayout.ViewPagerOnTabSelectedListener(
                container_profile
            )
        )

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
                finish()
            }
            R.id.tv_keluar -> {
                firebaseAuth.signOut()

                deleteAppDir(this)
                finish()
            }

            R.id.ll_following -> {
                intent = Intent(this, FollowActivity::class.java)
                intent.putExtra("follow", "following")
                intent.putExtra(main.userId, uid)
                startActivity(intent)
            }

            R.id.ll_follower -> {
                intent = Intent(this, FollowActivity::class.java)
                intent.putExtra("follow", "follower")
                intent.putExtra(main.userId, uid)
                startActivity(intent)
            }

            R.id.tv_edit -> {
                intent = Intent(this, EditProfileActivity::class.java)
                startActivityForResult(intent,editId)
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
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == editId && resultCode == Activity.RESULT_OK){
            initData()
        }
    }

    private fun changeUiIfNotMe(){
        if(uid != main.getUId()){
            iv_more.visibility = View.GONE
        }
    }

    private fun deleteAppDir(context: Context) {
        try {
            val dir = context.cacheDir
            log(dir.parent)
            val dirApp = File(dir.parent)
            deleteDir(dirApp)
        } catch (e: Exception) {
            log("deleteAppDir ${e.message}")
        }
    }

    private fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list()
            for (fileName in children) {
                val success: Boolean = deleteDir(File(dir, fileName))
                log("delete dir")
                if (!success) {
                    log("delet selesai")
                    return false
                }
            }
            return dir.delete()
        } else if (dir != null && dir.isFile) {
            log("delet file")
            return dir.delete()
        } else {
            log("gagal")
            return false
        }
    }

    private fun getFollowing(CountChild: (Int) -> Unit) {
        database.child(main.following).child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    log(dataSnapshot.childrenCount.toString())
                    CountChild(dataSnapshot.childrenCount.toInt())
                }

            })
//            .addChildEventListener(object : ChildEventListener {
//                override fun onCancelled(p0: DatabaseError) {
//                }
//
//                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
//                }
//
//                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
//                }
//
//                override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
//                    log(dataSnapshot.childrenCount.toString())
//                    log(main.getUId())
//                    CountChild(dataSnapshot.childrenCount.toInt())
//                }
//
//                override fun onChildRemoved(p0: DataSnapshot) {
//                }
//
//            })
    }

    private fun getFollower(CountChild: (Int) -> Unit) {
        database.child(main.follower).child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    CountChild(dataSnapshot.childrenCount.toInt())
                }

            })
//            .addChildEventListener(object : ChildEventListener {
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//
//            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
//
//            }
//
//            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
//
//            }
//
//            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
//                log(dataSnapshot.childrenCount.toString())
//                CountChild(dataSnapshot.childrenCount.toInt())
//            }
//
//            override fun onChildRemoved(p0: DataSnapshot) {
//
//            }

//        })
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


    private fun initData() {
        ll_more.visibility = View.GONE
        pb_profile.visibility = View.VISIBLE
        iv_profile.visibility = View.INVISIBLE
        tv_profile.visibility = View.INVISIBLE
        tv_about.visibility = View.INVISIBLE
        ll_follow.visibility = View.INVISIBLE

        getProfile(object : CallbackLoading {
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
        database.child(main.user).child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val name = dataSnapshot.child("userName").getValue(String::class.java)
                    val autor = dataSnapshot.child("about").getValue(String::class.java)
                    //                    val fingCount = dataSnapshot.child("fingCount").getValue(Int::class.java)
                    //                    val ferCount = dataSnapshot.child("ferCount").getValue(Int::class.java)
                    val urlPic = dataSnapshot.child("urlPic").getValue(String::class.java)
                    tv_profile.text = name
                    tv_toolbar_profile.text = name

                    //                    if (fingCount!=null){
                    //                        tv_jml_following.text = fingCount.toString()
                    //                    }
                    //                    if(ferCount != null){
                    //                        tv_jml_follower.text = ferCount.toString()
                    //                    }
                    getFollower {
                        tv_jml_follower.text = it.toString()
                    }
                    getFollowing {
                        tv_jml_following.text = it.toString()
                    }

                    if (autor != null) {
                        tv_about.text = autor
                    }
                    if (urlPic != "") {
                        Picasso.get()
                            .load(urlPic).error(R.drawable.ic_broken_image_24dp)
                            .into(iv_profile, object : Callback {
                                override fun onSuccess() {
                                    pb_image.visibility = View.GONE
                                }

                                override fun onError(e: Exception?) {
                                    pb_image.visibility = View.GONE
                                }

                            })
                    } else {
                        pb_image.visibility = View.GONE
                        iv_profile.setImageResource(R.drawable.ic_person_24dp)
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
                    LikedStoryFragment()
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
