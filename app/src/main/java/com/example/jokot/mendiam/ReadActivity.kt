package com.example.jokot.mendiam

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_read.*


class ReadActivity : AppCompatActivity() {

    private var sid = ""
    private val main = MainApps()
    private var database = main.database.reference
    private var myId = main.auth.uid.toString()
    private var authorId = ""
    private var isBookmarked = false
    private var isLiked = false
    private var storyLikesId: MutableList<String> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_read)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (intent.getStringExtra("sid") != null) {
            sid = intent.getStringExtra("sid")
        }

        initData()
        getBookmarkId {
            getLiked {
                setUpClick()
            }
        }


//         Set up the user interaction to manually show or hide the system UI.

//
//         Upon interacting with UI controls, delay any scheduled hide()
//         operations to prevent the jarring behavior of controls going away
//         while interacting with the UI.
//        dummy_button.setOnTouchListener(mDelayHideTouchListener)
    }

    private fun setUpClick() {
        btn_follow.setOnClickListener {
            follow()
        }
        ll_author.setOnClickListener {
            val intent = Intent(applicationContext, ProfileActivity::class.java)
            intent.putExtra(main.userId, authorId)
            startActivity(intent)
        }
        ll_write_response.setOnClickListener {
            toast("Coming soon")
        }
        ll_small_author.setOnClickListener {
            val intent = Intent(applicationContext, ProfileActivity::class.java)
            intent.putExtra(main.userId, authorId)
            startActivity(intent)
        }
        iv_like.setOnClickListener {
            if (isLiked) {
                iv_like.setImageResource(R.drawable.ic_thumbs_up)
                isLiked = false
                removeStoryLikes()
                removeLikedStory()
                getStoryLikes()
            } else {
                iv_like.setImageResource(R.drawable.ic_thumbs_up_hand_symbol)
                isLiked = true
                addStoryLikes()
                addLikedStory()
                getStoryLikes()
            }

        }
        iv_bookmark.setOnClickListener {
            if (isBookmarked) {
                iv_bookmark.setImageResource(R.drawable.ic_bookmark_border_black_24dp)
                isBookmarked = false
                removeBookmark()
            } else {
                iv_bookmark.setImageResource(R.drawable.ic_bookmark_red_24dp)
                isBookmarked = true
                addBookmark()
            }
        }
        iv_share.setOnClickListener {
            toast("Coming soon")
        }
//        scrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
//            if (scrollY > oldScrollY) {
//                ll_tool.animate().translationY(ll_tool.height.toFloat())
//            } else if (scrollY < oldScrollY) {
//                ll_tool.animate().translationY(0f)
//            }
//        }

    }

    private fun addBookmark() {
        database.child(main.bookmark).child(myId).child(sid).setValue(true)
    }

    private fun removeBookmark() {
        database.child(main.bookmark).child(myId).child(sid).removeValue()
    }

    private fun addStoryLikes() {
        database.child(main.storyLikes).child(sid).child(myId).setValue(true)
    }

    private fun removeStoryLikes() {
        database.child(main.storyLikes).child(sid).child(myId).removeValue()
    }

    private fun addLikedStory() {
        database.child(main.likedStory).child(myId).child(sid).setValue(true)
    }

    private fun removeLikedStory() {
        database.child(main.likedStory).child(myId).child(sid).removeValue()
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
            getStoryLikes()
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
                            Picasso.get().load(urlPic).error(R.drawable.ic_broken_image_24dp)
                                .into(iv_user)
                            Picasso.get().load(urlPic).error(R.drawable.ic_broken_image_24dp)
                                .into(iv_author)
                        }
                        tv_author.text = name
                        tv_author_name.text = name


                    }
                })
        }
    }

    private fun getStoryLikes() {
        database.child(main.storyLikes).child(sid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    log(p0.message)
                }

                @SuppressLint("SetTextI18n")
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val ds = dataSnapshot.children
                    storyLikesId.clear()
                    ds.mapNotNull {
                        val id = it.key
                        if (id != null) {
                            storyLikesId.add(id)
                        }
                    }
                    tv_like.text = "${storyLikesId.size} ${getString(R.string.likes)}"
                }
            })

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
                    log(p0.message)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val tittle = dataSnapshot.child("judul").getValue(String::class.java)
                    val uid = dataSnapshot.child("uid").getValue(String::class.java)
                    val date = dataSnapshot.child("date").getValue(String::class.java)

                    val textContent = dataSnapshot.child("textContent").getValue(Int::class.java)

                    tv_tittle.text = tittle
                    tv_date.text = date

                    if (uid != main.getUId()) {
                        uid?.let { cekFollow(it) }
                    } else {
                        btn_follow.visibility = View.GONE
                    }

                    authorId = uid.toString()
                    uid?.let {
                        textContent?.let { it1 ->
                            onDataChange(
                                it
                                , it1
                            )
                        }
                    }

//                    getStoryContent(textContent)
                }

            })
    }

    private fun getStoryContent(textContent: Int) {
        for (i in 0 until textContent) {
            database.child(main.storyContent).child(sid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        log(p0.message)
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
                param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
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
                Picasso.get().load(url).error(R.drawable.ic_broken_image_24dp)
                    .into(newImage, object : Callback {
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
            textView.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                resources.getDimension(R.dimen.font_size)
            )
            layoutParent.addView(textView)
        }
    }

    private fun cekFollow(uid: String) {
        database.child("following").child(myId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    log(p0.message)
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
            database.child("following").child(myId).child(authorId).setValue(true)
            database.child("follower").child(authorId).child(myId).setValue(true)
            changeButtonFollow("Following")
        } else {
            database.child("following").child(myId).child(authorId).removeValue()
            database.child("follower").child(authorId).child(myId).removeValue()
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
                    log(p0.message)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val urlPic = dataSnapshot.child("urlPic").getValue(String::class.java)
                    if (urlPic != "") {
                        Picasso.get().load(urlPic).into(iv_my_pic)
                    }
                }

            })
    }


    private fun getBookmarkId(onSuccess: () -> Unit) {
        database.child(main.bookmark).child(myId).child(sid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    log(p0.message)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val ds = dataSnapshot.value
                    isBookmarked = ds != null
                    if (isBookmarked) {
                        iv_bookmark.setImageResource(R.drawable.ic_bookmark_red_24dp)
                    } else {
                        iv_bookmark.setImageResource(R.drawable.ic_bookmark_border_black_24dp)
                    }
                    onSuccess()
                }
            })
    }

    private fun getLiked(onSuccess: () -> Unit) {
        database.child(main.likedStory).child(myId).child(sid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    log(p0.message)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val ds = dataSnapshot.value
                    isLiked = ds != null
                    if (isLiked) {
                        iv_like.setImageResource(R.drawable.ic_thumbs_up_hand_symbol)
                    } else {
                        iv_like.setImageResource(R.drawable.ic_thumbs_up)
                    }
                    onSuccess()
                }
            })
    }
}
