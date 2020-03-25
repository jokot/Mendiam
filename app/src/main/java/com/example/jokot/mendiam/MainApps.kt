package com.example.jokot.mendiam

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ProgressBar
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.sql.Timestamp

@SuppressLint("Registered")
class MainApps : Application() {
    companion object {
        const val sharePref = "MY PREF"
        const val uName = "name"
    }

    val database = FirebaseDatabase.getInstance()
    val storage = FirebaseStorage.getInstance()
    val auth = FirebaseAuth.getInstance()
    //    val editor = getSharedPreferences(sharePref, Context.MODE_PRIVATE).edit()
//    val pref = getSharedPreferences(sharePref, Context.MODE_PRIVATE)
    val uid = auth.currentUser?.uid
    val name = auth.currentUser?.displayName

    val userId = "uid"
    val storyLikes = "storyLikes"
    val likedStory = "likedStory"
    val bookmark = "bookmark"
    val draft = "draft"
    val draftContent = "draftContent"
    val follower = "follower"
    val follow = "follow"
    val following = "following"
    val story = "story"
    val storyContent = "storyContent"
    val user = "user"
    val userName = "userName"

    val G_CODE = 56412

    val firebaseDatabase = FirebaseDatabase.getInstance().reference
    val firebaseAuth = FirebaseAuth.getInstance()

    fun showImagePicker(activity: Activity) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Ambil Gambar dari ?")
        builder.setNegativeButton("Camera") { _, _ ->
            openCamera()
        }
        builder.setPositiveButton("Galery") { _, _ ->
            openGallery(activity)
        }
        builder.setNeutralButton("Batal") { _, _ ->

        }
        val alert = builder.create()
        alert.show()
    }

    private fun openCamera() {

    }

    private fun openGallery(activity: Activity) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        activity.startActivityForResult(Intent.createChooser(intent, "Select Image"), G_CODE)
    }

    fun uploadImage(
        file: Uri,
        onFailureUpload: (String) -> Unit,
        onFailureGetUrl: (String) -> Unit,
        onCompleteGetUrl: (String) -> Unit
    ) {
        val storageRef = storage.reference
        val riversRef = storageRef.child(getCurrentTimeStamp() + ".jpg")
        val uploadTask = riversRef.putFile(file)

        uploadTask.addOnFailureListener {
            onFailureUpload(it.message.toString())
        }.addOnSuccessListener {
            val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation riversRef.downloadUrl
            })

            urlTask.addOnFailureListener {
                onFailureGetUrl(it.message.toString())
            }
            urlTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    onCompleteGetUrl(downloadUri.toString())
                }
            }
        }
    }

    fun showProgressBar(pb: ProgressBar) {
        pb.visibility = View.VISIBLE
    }

    fun hideProgresBar(pb: ProgressBar) {

        pb.visibility = View.GONE
    }

    fun getUName(uid:String,onDataChange:(String)->Unit) {

            firebaseDatabase.child("user").child(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        log(p0.message)
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var name = dataSnapshot.child("userName").getValue(String::class.java)
                        if(name == null){
                            name = ""
                        }
                        onDataChange(name)
                    }
                })
    }

    fun getUId(): String {
        return firebaseAuth.uid.toString()
    }

    fun editorPref(key: String, value: String,activity: Activity) {
        val editor = activity.getSharedPreferences(sharePref, Context.MODE_PRIVATE).edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getPref(key: String, type: String,activity: Activity): Any? {
        val pref = activity.getSharedPreferences(sharePref, Context.MODE_PRIVATE)
        return when (type) {
            "s" -> pref.getString(key, "")
            "i" -> pref.getInt(key, 0)
            "b" -> pref.getBoolean(key, false)
            else -> ""
        }
    }

    fun getCurrentTimeStamp():String{
        val t = System.currentTimeMillis()
        val tT = Timestamp(t)
        return tT.toString()
    }
}