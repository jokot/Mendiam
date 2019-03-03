package com.example.jokot.mendiam

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    //    private var mutableList = mutableListOf<User>()
    private var database = FirebaseDatabase.getInstance().reference
    private var auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        supportFragmentManager
            .beginTransaction()
            .add(R.id.frame_fragment, HomeFragment())
            .commit()

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val header = navigationView.getHeaderView(0)

        val headerView = header.findViewById<LinearLayout>(R.id.ll_nav_bar)




        loadName()

        headerView.setOnClickListener {
            intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        iv_search.setOnClickListener {
            intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun loadName() {

        database
            .child("user")
            .child(auth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    toast(this@MainActivity,"Selamat Datang di Mendiam")
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val name = dataSnapshot.child("userName").getValue(String::class.java)
                    val navigationView: NavigationView = findViewById(R.id.nav_view)
                    val header = navigationView.getHeaderView(0)
                    val headerText = header.findViewById<TextView>(R.id.tv_header_main)
                    headerText.text = name
                }

            })

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
                tb_label.text = "Home"
            }
            R.id.nav_bookmark -> {
                changeFragment(BookmarkFragment())
                tb_label.text = "Bookmark"

            }
            R.id.nav_interest -> {
                changeFragment(InterestFragment())
                tb_label.text = "Interest"

            }
            R.id.nav_new_story -> {
                intent = Intent(this, NewStoryActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_stories ->{
                changeFragment(StoriesFragment())
                tb_label.text = "Stories"
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


}
