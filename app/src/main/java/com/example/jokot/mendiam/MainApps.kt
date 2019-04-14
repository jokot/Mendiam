package com.example.jokot.mendiam

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

@SuppressLint("Registered")
class MainApps : Application() {
    private val sharePref = "MY PREF"
    val database = FirebaseDatabase.getInstance()
    val storage = FirebaseStorage.getInstance()
    val auth = FirebaseAuth.getInstance()!!
//    val editor = getSharedPreferences(sharePref, Context.MODE_PRIVATE).edit()
//    val pref = getSharedPreferences(sharePref, Context.MODE_PRIVATE)
    val uid = auth.currentUser!!.uid
    val name = auth.currentUser!!.displayName

    val bookmark = "bookmark"
    val draft = "draft"
    val draftContent = "draftContent"
    val follower = "follower"
    val following = "following"
    val story = "story"
    val storyContent = "storyContent"
    val user = "user"
}