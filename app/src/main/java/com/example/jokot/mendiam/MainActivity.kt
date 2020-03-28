package com.example.jokot.mendiam

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    //    private var mutableList = mutableListOf<User>()
    private val main = MainApps()
    private var database = main.database.reference
    private var auth = main.auth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        supportFragmentManager
            .beginTransaction()
            .add(R.id.frame_fragment, HomeFragment())
            .commit()

        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)

        setUpOnClick()


        main.getUName(main.getUId()) {
            if (main.getPref(main.userName, "s", this) == "") {
                main.editorPref(main.userName, it, this)
            }
        }

        loadProfile()

    }

    private fun setUpOnClick() {
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val header = navigationView.getHeaderView(0)

        val headerView = header.findViewById<LinearLayout>(R.id.ll_nav_bar)
        headerView.setOnClickListener {
            intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra(main.userId, main.uid)
            startActivity(intent)
        }

        iv_notification.setOnClickListener {
            toast("Coming soon")
        }
        iv_search.setOnClickListener {
            toast("Coming soon")
//            intent = Intent(this, SearchActivity::class.java)
//            startActivity(intent)
        }
    }

//    private fun getUserName(){
//        if (main.getPref(main.userName,"s",this)=="") {
//            database.child("user").child(main.getUId())
//                .addListenerForSingleValueEvent(object : ValueEventListener {
//                    override fun onCancelled(p0: DatabaseError) {
//
//                    }
//
//                    override fun onDataChange(dataSnapshot: DataSnapshot) {
//                        val name = dataSnapshot.child("userName").getValue(String::class.java)!!
//                        main.editorPref(main.userName,name,this@MainActivity)
//
//                    }
//                })
//        }
//    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun loadProfile() {

        auth.currentUser?.uid?.let {
            database
                .child("user")
                .child(it)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        toast(error.message)
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val name = dataSnapshot.child("userName").getValue(String::class.java)
                        val urlPic = dataSnapshot.child("urlPic").getValue(String::class.java)
                        val navigationView: NavigationView = findViewById(R.id.nav_view)
                        val header = navigationView.getHeaderView(0)
                        val headerText = header.findViewById<TextView>(R.id.tv_header_main)
                        val headerImage = header.findViewById<ImageView>(R.id.iv_header)
                        val headerProgressBar = header.findViewById<ProgressBar>(R.id.pb_header)
                        if (urlPic != "") {
                            Picasso.get().load(urlPic).error(R.drawable.ic_broken_image_24dp)
                                .into(headerImage, object :
                                    Callback {
                                    override fun onSuccess() {
                                        headerProgressBar.visibility = View.GONE
                                    }

                                    override fun onError(e: Exception?) {
                                        headerProgressBar.visibility = View.GONE
                                    }

                                })
                        } else {
                            headerProgressBar.visibility = View.GONE
                            headerImage.setImageResource(R.drawable.ic_person_24dp)
                        }
                        headerText.text = name
                    }

                })
        }

    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.main, menu)
//        return true
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        return when (item.itemId) {
//            R.id.action_settings -> true
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_fragment, fragment)
            .commit()
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {

            R.id.nav_home -> {
                changeFragment(HomeFragment())
                tb_label.text = getString(R.string.home)
            }
            R.id.nav_bookmark -> {
                changeFragment(BookmarkFragment())
                tb_label.text = getString(R.string.bookmark)

            }
            R.id.nav_interest -> {
                changeFragment(InterestFragment())
                tb_label.text = getString(R.string.interest)

            }
            R.id.nav_new_story -> {
                intent = Intent(this, NewStoryActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_stories -> {
                changeFragment(StoriesFragment())
                tb_label.text = getString(R.string.stories)
            }
//            R.id.nav_full_screen ->{
//                startActivity(Intent(this,FullscreenActivity::class.java))
//            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


}
