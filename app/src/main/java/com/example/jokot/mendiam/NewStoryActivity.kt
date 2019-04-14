package com.example.jokot.mendiam

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.jokot.mendiam.model.Story
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_new_story.*
import java.io.FileNotFoundException
import java.io.InputStream

class NewStoryActivity : BaseActivity(), View.OnClickListener {
    private val main = MainApps()
    private var database = main.database.reference
    private var auth = main.auth
    private var uid = main.uid
    private var storage = main.storage

    private var did = ""

    private var listImage = mutableListOf<String>()
    private var listText = mutableListOf<String>()

    private var textId = 0
    private var imageId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_story)

        if (intent.getStringExtra("did") != null) {
            did = intent.getStringExtra("did")
            getDraft()
        }

        img_add_image.setOnClickListener(this)
        iv_back.setOnClickListener(this)
        tv_publish.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClick(v: View?) {
        val id = v?.id
        when (id) {
            R.id.iv_back -> finish()
            R.id.tv_publish -> {
                publish()
            }
            R.id.img_add_image -> {
                requestImage()
            }
        }
    }

    // Open Storage to add Image
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun requestImage() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK) {
            try {
                addView(data)

                val uri = data!!.data
                uploadImage(uri)
            } catch (e: FileNotFoundException) {
                toast(this, "Something went wrong")
            }
        } else {
            toast(this, "You haven't picked yet")
        }
    }

    private fun uploadImage(file: Uri) {
        val storageRef = storage.reference
        val riversRef = storageRef.child(file.lastPathSegment)
        var uploadTask = riversRef.putFile(file)

        showProgressDialog()
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            toast(this, it.message.toString())
            hideProgressDialog()
        }.addOnSuccessListener {
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            hideProgressDialog()
            uploadTask = riversRef.putFile(file)
            val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation riversRef.downloadUrl
            })
            urlTask.addOnFailureListener {
                toast(this, it.message.toString())
            }
            urlTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    listImage.add(downloadUri.toString())
                } else {
                    toast(this, "Failed to upload")
                }

            }
        }
    }

    private fun addView(data: Intent?) {
        val layoutParent = findViewById<LinearLayout>(R.id.ll_dynamic)
        val imageUri: Uri? = data!!.data
        val imageStream: InputStream? = contentResolver.openInputStream(imageUri!!)
        val selectedImage: Bitmap = BitmapFactory.decodeStream(imageStream)
        val newImage = ImageView(applicationContext)

        newImage.layoutParams = (
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ))

        newImage.id = IMAGE_BASE_ID + imageId
        layoutParent.addView(newImage)
        newImage.setImageBitmap(selectedImage)

        if (imageId > 1) {
            val text = findViewById<EditText>(TEXT_BASE_ID + textId - 1)
            if (text.text.toString() == "") {
                text.hint = ""
            }
        } else {
            et_story.hint = ""
        }
        addText()

        imageId++
        textId++
    }

    private fun addText() {
        val layoutParent = findViewById<LinearLayout>(R.id.ll_dynamic)
        //        val newContext = ContextThemeWrapper(applicationContext, R.style.EditTextNewStory)
        val myEditText = EditText(applicationContext)

        myEditText.layoutParams = (
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ))
        myEditText.setTextColor(Color.parseColor("#000000"))
        val padding = 16
        val scale: Float = resources.displayMetrics.density
        val paddingDp: Int = (padding * scale + 0.5f).toInt()
        myEditText.setPadding(paddingDp, 0, paddingDp, 0)

        myEditText.hint = "Cerita di sini..."
        myEditText.setBackgroundResource(R.color.colorWhite)
        myEditText.id = TEXT_BASE_ID + textId

        layoutParent.addView(myEditText)
    }

    private fun publish() {
        readData { sCount, id ->
            val sid = id + (sCount.toString())

            val story = Story(
                sid
                , id
                , et_judul.text.toString()
                , et_story.text.toString()
                , auth.currentUser?.displayName.toString()
                , listImage[0]
                , if (imageId > 0) imageId - 1 else 0
                , textId
            )

            database.child("story").child(sid).setValue(story)

            publishContent()

            hideProgressDialog()
            finish()
        }
    }

    private fun publishContent() {
        readData { sCount, id ->
            val sid = "$id$sCount"

//            upload image url
            if (listImage.size > 0) {
                for (i in 1 until listImage.size) {
                    database.child(main.storyContent).child(sid).child("image$i").setValue(listImage[i])
                }
            }

//            add text to list
            for (i in 0 until textId) {
                val text = findViewById<EditText>(TEXT_BASE_ID + i)
                listText.add(text.text.toString())
            }

//            upload text
            if (listText.size > 0) {
                for (i in 0 until listText.size) {
                    database.child(main.storyContent).child(sid).child("text$i").setValue(listText[i])
                }
            }
            database.child(main.user).child(id).child("sCount").setValue(sCount)
        }
    }

    private fun readData(onDataChange: (Int, String) -> Unit) {
        showProgressDialog()

        database.child(main.user).child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val count = dataSnapshot.child("sCount").getValue(Int::class.java)!!
                    onDataChange(count + 1, uid)
                }
            })
    }

    private fun getDraft() {
        database.child(main.draft).child(uid).child(did)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val judul = dataSnapshot.child("judul").getValue(String::class.java)
                    val deskripsi = dataSnapshot.child("deskripsi").getValue(String::class.java)

                    et_judul.setText(judul)
                    et_story.setText(deskripsi)
                }

            })
    }

    companion object {
        const val REQUEST_IMAGE = 2
        const val IMAGE_BASE_ID = 200
        const val TEXT_BASE_ID = 100
    }
}
