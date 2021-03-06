package com.example.jokot.mendiam

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.jokot.mendiam.callback.CallbackLoading
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class EditProfileActivity : AppCompatActivity(), View.OnClickListener {

    private val main = MainApps()
    private var database = main.database.reference
    private var urlPic = ""
    val gCode = 56412
    val REQUEST_IMAGE_CAPTURE = 1
    private var currentPhotoPath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        initData()

        iv_close.setOnClickListener(this)
        tv_save.setOnClickListener(this)
        ll_chose_img.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_close -> {
                finishCancel()
            }
            R.id.tv_save -> {
                saveData()
            }
            R.id.ll_chose_img -> {
                showImagePicker(this)
            }
        }
    }

    private fun initData() {
        pb_edit.visibility = View.VISIBLE
        getData(object : CallbackLoading {
            override fun onCallback() {
                pb_edit.visibility = View.GONE
            }
        })
    }

    private fun getData(callbackLoading: CallbackLoading) {
        main.uid?.let {
            database.child(main.user).child(it)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val name = dataSnapshot.child("userName").getValue(String::class.java)
                        val about = dataSnapshot.child("about").getValue(String::class.java)
                        val urlPic = dataSnapshot.child("urlPic").getValue(String::class.java)
                        et_name.setText(name)
                        if (about != "") {
                            et_about.setText(about)
                        }
                        if (urlPic != "") {
                            Picasso.get().load(urlPic).into(iv_edit)
                        }
                        callbackLoading.onCallback()
                    }

                })
        }
    }

    private fun saveData() {
        val name = et_name.text.toString()
        val about = et_about.text.toString()
        if (urlPic != "") {
            uploadUrlImage(urlPic) {
                dataBase("userName", name) {
                    dataBase("about", about) {
                        finishSuccess()
                    }
                }
            }
        } else {
            dataBase("userName", name) {
                dataBase("about", about) {
                    finishSuccess()
                }
            }
        }
    }

    private fun dataBase(key: String, value: String, onSuccess: () -> Unit) {
        main.editorPref(key, value, this)
        main.uid?.let {
            database.child(main.user)
                .child(it)
                .child(key).setValue(value).addOnSuccessListener {
                    onSuccess()
                }
        }
    }

    private fun finishSuccess() {
        val data = Intent()
        setResult(RESULT_OK, data)
        finish()
    }

    private fun finishCancel() {
        val data = Intent()
        setResult(Activity.RESULT_CANCELED, data)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == gCode) {
                val uri = data?.data
                Log.d("onActivityResult", "requestCode = $requestCode | uri = $uri")
                if (pb_edit != null) {
                    main.showProgressBar(pb_edit)
                }
                uri?.let { it ->
                    main.uploadImage(it, {
                        if (pb_edit != null) {
                            main.hideProgressBar(pb_edit)
                        }
                        toast(it)
                    }, {
                        if (pb_edit != null) {
                            main.hideProgressBar(pb_edit)
                        }
                        toast(it)
                    }, {
                        urlPic = it
                        iv_edit.setImageURI(uri)
                        if (pb_edit != null) {
                            main.hideProgressBar(pb_edit)
                        }
                    })
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                val imageBitmap = data?.extras?.get("data") as Bitmap
                saveCanvas(imageBitmap) { uri ->
                    if (pb_edit != null) {
                        main.showProgressBar(pb_edit)
                    }
                    main.uploadImage(uri, {
                        if (pb_edit != null) {
                            main.hideProgressBar(pb_edit)
                        }
                        toast(it)
                    }, {
                        if (pb_edit != null) {
                            main.hideProgressBar(pb_edit)
                        }
                        toast(it)
                    }, {
                        urlPic = it
                        iv_edit.setImageURI(uri)
                        if (pb_edit != null) {
                            main.hideProgressBar(pb_edit)
                        }
                    })
                }
            }
        }
    }

    private fun uploadUrlImage(url: String, onSuccess: () -> Unit) {
        database.child(main.user).child(main.auth.uid.toString()).child("urlPic").setValue(url)
            .addOnSuccessListener {
                onSuccess()
            }
    }

    fun showImagePicker(activity: Activity) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(getString(R.string.take_image_from))
        builder.setNegativeButton(getString(R.string.camera)) { _, _ ->
            openCamera()
        }
        builder.setPositiveButton(getString(R.string.gallery)) { _, _ ->
            openGallery(activity)
        }
        builder.setNeutralButton(getString(R.string.cancel)) { _, _ ->

        }
        val alert = builder.create()
        alert.show()
    }

    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun openGallery(activity: Activity) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        activity.startActivityForResult(Intent.createChooser(intent, "Select Image"), gCode)
    }


    fun saveCanvas(bitmap: Bitmap, onSuccessful: (Uri) -> Unit) {
        val path = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        path?.mkdirs()
        val fileName = "file" + System.currentTimeMillis() + ".jpg"
        val saveFile = File.createTempFile(
            fileName, /* prefix */
            ".jpg", /* suffix */
            path /* directory */
        ).apply {
            currentPhotoPath = absolutePath
        }
        val fos: FileOutputStream?
        try {
            fos = FileOutputStream(saveFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fos)
            fos.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        onSuccessful(Uri.parse("file://$currentPhotoPath"))
    }
}
