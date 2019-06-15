package com.example.jokot.mendiam

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_read.*


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */

class ReadActivity : AppCompatActivity() {

    private var sid = ""
    private val main = MainApps()
    private var database = main.database.reference
    private var myid = main.auth.uid.toString()
    private var authorId = ""

//    private val mHideHandler = Handler()
//    private val mHidePart2Runnable = Runnable {
//        // Delayed removal of status and navigation bar
//
//        // Note that some of these constants are new as of API 16 (Jelly Bean)
//        // and API 19 (KitKat). It is safe to use them, as they are inlined
//        // at compile-time and do nothing on earlier devices.
//        fullscreen_content.systemUiVisibility =
//            View.SYSTEM_UI_FLAG_LOW_PROFILE or
//                    View.SYSTEM_UI_FLAG_FULLSCREEN or
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
//                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
//                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
//                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//    }
//    private val mShowPart2Runnable = Runnable {
//        // Delayed display of UI elements
//        supportActionBar?.show()
////        fullscreen_content_controls.visibility = View.VISIBLE
//    }
//    private var mVisible: Boolean = false
//    private val mHideRunnable = Runnable { hide() }
//    /**
//     * Touch listener to use for in-layout UI controls to delay hiding the
//     * system UI. This is to prevent the jarring behavior of controls going away
//     * while interacting with activity UI.
//     */
//    private val mDelayHideTouchListener = View.OnTouchListener { _, _ ->
//        if (AUTO_HIDE) {
//            delayedHide(AUTO_HIDE_DELAY_MILLIS)
//        }
//        false
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_read)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        if (intent.getStringExtra("sid") != null) {
            sid = intent.getStringExtra("sid")
        }

        initData()
        setUpClick()

//        mVisible = true

        // Set up the user interaction to manually show or hide the system UI.
//        fullscreen_content.setOnClickListener { toggle() }

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
//        dummy_button.setOnTouchListener(mDelayHideTouchListener)
    }

    private fun setUpClick() {
        btn_follow.setOnClickListener {
            follow()
        }
        ll_author.setOnClickListener {
            startActivity(Intent(applicationContext, ProfileActivity::class.java))
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initData() {
        pb_read.visibility = View.VISIBLE
        ll_read.visibility = View.INVISIBLE
        getStory { uid
                   , textContent
            ->
            getStoryContent(textContent)
            getMyPic()
            database.child("user").child(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        toast(p0.message)
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val name = dataSnapshot.child("userName").getValue(String::class.java)

                        val about = dataSnapshot.child("about").getValue(String::class.java)
                        val urlPic = dataSnapshot.child("urlPic").getValue(String::class.java)

                        if (about != "") {
                            tv_author_about.text = about
                        }
                        if (urlPic != "") {
                            Picasso.get().load(urlPic).error(R.drawable.ic_broken_image_24dp).into(iv_user)
                            Picasso.get().load(urlPic).error(R.drawable.ic_broken_image_24dp).into(iv_author)
                        }
                        tv_author.text = name
                        tv_author_name.text = name


                    }
                })
        }
    }

    private fun getStory(
        onDataChange: (
            String
            , Int
        ) -> Unit
    ) {
        database.child(main.story).child(sid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    toast(p0.message)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val judul = dataSnapshot.child("judul").getValue(String::class.java)
                    val uid = dataSnapshot.child("uid").getValue(String::class.java)
                    val date = dataSnapshot.child("date").getValue(String::class.java)
//                    val imageContent = dataSnapshot.child("imageContent").getValue(Int::class.java)
                    val textContent = dataSnapshot.child("textContent").getValue(Int::class.java)

//                    toast(this@ReadActivity,"$imageContent $textContent")
                    tv_judul.text = judul
                    tv_date.text = date
//                    tv_deskripsi.text = deskipsi

                    if (uid != main.getUId()) {
                        cekFollow(uid!!)
                    } else {
                        btn_follow.visibility = View.GONE
                    }

                    authorId = uid
                    onDataChange(
                        uid!!
                        , textContent!!
                    )

//                    getStoryContent(textContent)
                }

            })
    }

    private fun getStoryContent(textContent: Int?) {
        for (i in 0 until textContent!!) {
            database.child(main.storyContent).child(sid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        val image = dataSnapshot.child("image$i").getValue(String::class.java)
                        val text = dataSnapshot.child("text$i").getValue(String::class.java)

                        loadContent(
                            i,
                            image,
                            text
                        )
                    }

                })
        }
        pb_read.visibility = View.INVISIBLE
        ll_read.visibility = View.VISIBLE
    }

//    private fun addText() {

//        val padding = 16
//        val scale: Float = resources.displayMetrics.density
//        val paddingDp: Int = (padding * scale + 0.5f).toInt()
//        myEditText.setPadding(paddingDp, 0, paddingDp, 0)

    private fun loadContent(
        id: Int,
        url: String?,
        text: String?
    ) {
        if (id >= 0) {
            val layoutParent = findViewById<LinearLayout>(R.id.ll_content)


//            add relative layout
            val newLayout = RelativeLayout(applicationContext)
            if (url != "") {
                newLayout.layoutParams = (
                        LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            500
                        ))
                newLayout.setBackgroundResource(R.color.colorGrey)
                layoutParent.addView(newLayout)
            } else {
                newLayout.layoutParams = (
                        LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ))

                layoutParent.addView(newLayout)
            }
//            add progressbar
            val progress = ProgressBar(applicationContext)
            if (url != "") {
                val param = (RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                        ))
                param.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE)
                progress.layoutParams = param
                progress.bringToFront()
                newLayout.addView(progress)
            } else {
                progress.layoutParams = (
                        RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                        ))
                progress.visibility = View.GONE
                newLayout.addView(progress)
            }


//            add image
            val newImage = ImageView(applicationContext)
            newImage.layoutParams = (
                    RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                    ))
            newImage.adjustViewBounds = true
            newLayout.addView(newImage)
            if (url != "") {
                Picasso.get().load(url).error(R.drawable.ic_broken_image_24dp).into(newImage, object : Callback {
                    override fun onSuccess() {
                        progress.visibility = View.GONE
                        val newLayoutParam = (
                                LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                ))
                        newImage.setBackgroundResource(R.color.colorWhite)
                        newLayout.layoutParams = newLayoutParam
                    }

                    override fun onError(e: Exception?) {

                    }

                })
            }

            //            add text

            val newContext = ContextThemeWrapper(applicationContext, R.style.TextReadStoryStyle)
            val textView = TextView(newContext)
            val padding = 8
            val scale: Float = resources.displayMetrics.density
            val paddingUp: Int = (padding * scale + 0.5f).toInt()
            textView.setPadding(0, 0, 0, paddingUp)

            textView.layoutParams = (
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ))
            textView.setTextIsSelectable(true)
            textView.text = text
            textView.setTextColor(Color.parseColor("#000000"))
            textView.setBackgroundResource(R.color.colorWhite)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.font_size))
            layoutParent.addView(textView)
        } else {
//            tv_deskripsi.text = text
        }
    }

    private fun cekFollow(uid: String) {
        database.child("following").child(myid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    toast(p0.message)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.child(uid).exists()) {
                        changeButtonFollow("Following")
                    } else {
                        changeButtonFollow("Follow")
                    }
                }

            })
    }

    private fun follow() {
        if (btn_follow.text.toString() == "Follow") {
            database.child("following").child(myid).child(authorId).setValue(true)
            database.child("follower").child(authorId).child(myid).setValue(true)
            changeButtonFollow("Following")
        } else {
            database.child("following").child(myid).child(authorId).removeValue()
            database.child("follower").child(authorId).child(myid).removeValue()
            changeButtonFollow("Follow")
        }
    }

    private fun changeButtonFollow(string: String) {
        btn_follow.text = string
        if (string == "Following") {
            btn_follow.setBackgroundResource(R.drawable.rectangle_btn_following)
            btn_follow.setTextColor(Color.parseColor("#ffffff"))
        } else {
            btn_follow.setBackgroundResource(R.drawable.rectangle_btn_follow)
            btn_follow.setTextColor(Color.parseColor("#000000"))
        }

    }

    private fun getMyPic() {
        database.child(main.user)
            .child(main.getUId())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val urlPic = dataSnapshot.child("urlPic").getValue(String::class.java)
                    if (urlPic != null) {
                        Picasso.get().load(urlPic).into(iv_my_pic)
                    }
                }

            })
    }
//    override fun onPostCreate(savedInstanceState: Bundle?) {
//        super.onPostCreate(savedInstanceState)
//
//        // Trigger the initial hide() shortly after the activity has been
//        // created, to briefly hint to the user that UI controls
//        // are available.
//        delayedHide(100)
//    }
//
//    private fun toggle() {
//        if (mVisible) {
//            hide()
//        } else {
//            show()
//        }
//    }
//
//    private fun hide() {
//        // Hide UI first
//        supportActionBar?.hide()
////        fullscreen_content_controls.visibility = View.GONE
//        mVisible = false
//
//        // Schedule a runnable to remove the status and navigation bar after a delay
//        mHideHandler.removeCallbacks(mShowPart2Runnable)
//        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
//    }
//
//    private fun show() {
//        // Show the system bar
//        fullscreen_content.systemUiVisibility =
//            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
//                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//        mVisible = true
//
//        // Schedule a runnable to display UI elements after a delay
//        mHideHandler.removeCallbacks(mHidePart2Runnable)
//        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
//    }
//
//    /**
//     * Schedules a call to hide() in [delayMillis], canceling any
//     * previously scheduled calls.
//     */
//    private fun delayedHide(delayMillis: Int) {
//        mHideHandler.removeCallbacks(mHideRunnable)
//        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
//    }
//
//    companion object {
//        /**
//         * Whether or not the system UI should be auto-hidden after
//         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
//         */
//        private val AUTO_HIDE = true
//
//        /**
//         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
//         * user interaction before hiding the system UI.
//         */
//        private val AUTO_HIDE_DELAY_MILLIS = 3000
//
//        /**
//         * Some older devices needs a small delay between UI widget updates
//         * and a change of the status and navigation bar.
//         */
//        private val UI_ANIMATION_DELAY = 300
//    }
}
//
//@SuppressLint("StaticFieldLeak")
//private class DownloadImageTask(internal var bmImage: ImageView) : AsyncTask<String, Void, Bitmap>() {
//
//    override fun doInBackground(vararg urls: String): Bitmap? {
//        val urlDisplay = urls[0]
//        var mIcon11: Bitmap? = null
//        try {
//            val `in` = java.net.URL(urlDisplay).openStream()
//            mIcon11 = BitmapFactory.decodeStream(`in`)
//        } catch (e: Exception) {
//            Log.e("Error", e.message)
//            e.printStackTrace()
//        }
//
//        return mIcon11
//    }
//
//    override fun onPostExecute(result: Bitmap) {
//        bmImage.setImageBitmap(result)
//    }
//}