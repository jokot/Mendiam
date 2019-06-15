package com.example.jokot.mendiam

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import com.example.jokot.mendiam.model.Draft
import com.example.jokot.mendiam.model.Story
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_new_story.*
import java.io.FileNotFoundException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class NewStoryActivity : BaseActivity(), View.OnClickListener {
    private val main = MainApps()
    private var database = main.database.reference
    private var auth = main.auth
    private var uid = main.uid
    private var storage = main.storage

    private var counterAdd = 1
    private var counterRemove = 1
    private var did = ""
    private var firstStory = ""
    private var firstImage = ""


    private var listImage = mutableListOf<String>()
    private var listText = mutableListOf<String>()
    private var listId = mutableListOf<Int>()

    private var viewId = 0
    private var focusEdit = 0
    private var imageId = 0
    private val hintAwal = ""
    private val hintAkhir = ""
    private var nextString = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_story)

        if (intent.getStringExtra("did") != null) {
            did = intent.getStringExtra("did")
            initDraft()
        } else {
//            addEditable(hintAwal)
        }
//        setupOnfocusJudul(et_judul)
        setUpOnKeyEditText(et_judul)

        img_add_image.setOnClickListener(this)
        iv_back.setOnClickListener(this)
        tv_publish.setOnClickListener(this)
    }

    private fun initDraft() {
        getDraft {
            for (i in 0 until it) {
                database
                    .child(main.draftContent)
                    .child(main.getUId())
                    .child(did).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val image = dataSnapshot.child("image$i").getValue(String::class.java)
                            val text = dataSnapshot.child("text$i").getValue(String::class.java)
                            loadDraft(image, text)
                        }

                    })
            }
        }
    }

    private fun loadDraft(image: String?, text: String?) {
        if (listId.isEmpty()) {
            nextString = text!!
            addEditable("")
        } else {
            nextString = text!!
            val idNow = listId[listId.size - 1]
            val index = getIndexId(idNow)
            listId.add(index + 1, TEXT_BASE_ID + viewId)
            listImage.add(index + 1, image!!)

            addText("", idNow)
            val newEditText = findViewById<EditText>(listId[index + 1])
            newEditText.requestFocus()
            val idImage = listId[index + 1] - TEXT_BASE_ID + IMAGE_BASE_ID
            val imageView = findViewById<ImageView>(idImage)
            if (image != "") {
                Picasso.get().load(image).into(imageView)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupOnfocusJudul(editText: EditText) {
        editText.setOnTouchListener { _, _ ->
            toast(editText.id.toString())
//            focusEdit = editText.id
            return@setOnTouchListener false
        }
    }

    //    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                if (listId.isNotEmpty() || et_judul.text.isNotEmpty()) {
                    publishDraft()
                } else {
                    finish()
                }
            }
            R.id.tv_publish -> {
                publish()
            }
            R.id.img_add_image -> {
//                toast("Coming soon")
                requestImage()
                getFocuse()
                log(focusEdit.toString())
            }
        }
    }

    private fun addEditable(hint: String) {
        val id = TEXT_BASE_ID + viewId
//        addimageView(id)
        addText(hint, id)
        val newEditText = findViewById<EditText>(id)
        newEditText.requestFocus()
//        focusEdit = id
    }


    private fun setUpOnKeyEditText(editText: EditText) {
        if (editText != et_judul) {
            editText.setOnKeyListener { v, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN) {
                    when (keyCode) {
                        KeyEvent.KEYCODE_DEL -> {

                            val id = editText.id
                            val imageView = findViewById<ImageView>(id - TEXT_BASE_ID + IMAGE_BASE_ID)

                            val index = getIndexId(id)

                            if (index > 0) {

                                removeView(editText, imageView)
//                            log(viewId.toString())
                                val newEditText = findViewById<EditText>(listId[index - 1])
                                newEditText.requestFocus()
//                                focusEdit = listId[index - 1]
                            } else if (index == 0) {
                                removeView(editText, imageView)
//                            val newEditText = findViewById<EditText>(listId[index])
                                val newEditText = findViewById<EditText>(R.id.et_judul)
                                newEditText.requestFocus()
//                                focusEdit = R.id.et_judul
//                            imageView.setImageDrawable(null)
                            }
                            log(listId.toString())
                            log(listImage.toString())
//                            log(focusEdit.toString())

                            return@setOnKeyListener true
                        }
                        else -> return@setOnKeyListener false
                    }
                }
                return@setOnKeyListener false
            }
        }

        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.isNotEmpty()) {
                    if (containtEnter(editText.text.toString())) {
//
//                    }
//                    if (s.elementAt(s.length - 1) == '\n') {
//                        delete \n
                        val string = editText.text.toString()
                        val stringSplit = string.split("\n")
                        val prevString = stringSplit[0]
                        nextString = stringSplit[1]

//                        editText.text.delete(s.length - 1, s.length)
                        editText.setText(prevString)

                        if (editText != et_judul) {
//                            delete hint
                            editText.hint = hintAkhir

//                            add created id to list
                            val index = getIndexId(editText.id)
                            listId.add(index + 1, TEXT_BASE_ID + viewId)
                            listImage.add(index + 1, "")

//                            add editText and imageView
                            addText(hintAkhir, editText.id)
                            changeViewEnterPosition(index)

//                            set focus to created editText
                            val newEditText = findViewById<EditText>(listId[index + 1])
                            newEditText.requestFocus()
//                            focusEdit = listId[index + 1]
                        } else {
                            if (listId.isEmpty()) {
                                addEditable(hintAwal)
                            } else {
                                addEditable(hintAkhir)
                                listId.add(0, TEXT_BASE_ID + viewId - 1)
                                listImage.add(0, "")
                                changeViewDeletePosition(1)
                            }
                        }

                        log(listId.toString())
                        log(listImage.toString())
//                        log(focusEdit.toString())

                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
    }

    private fun containtEnter(string: String): Boolean {
        return string.contains("\n")
    }

    private fun getIndexId(id: Int): Int {
        return listId.indexOf(id)
    }

    private fun removeId(index: Int) {
        listId.removeAt(index)
    }

    // Open Storage to add Image
//    @RequiresApi(Build.VERSION_CODES.KITKAT)
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
                addImage(data)
                val uri = data!!.data
                uploadImage(uri)
            } catch (e: FileNotFoundException) {
                toast(this, "Something went wrong")
            }
        } else {
            toast(this, "You haven't picked yet")
        }
    }

    private fun getFocuse() {
        var found = false
        for (i in listId) {
            val editText = findViewById<EditText>(i)
            if (editText.isFocused) {
                focusEdit = i
                found = true
                break
            }
        }
        if (!found) {
            focusEdit = R.id.et_judul
        }
    }

    private fun uploadImage(file: Uri) {
        val storageRef = storage.reference
        val riversRef = storageRef.child(main.getCurrentTimeStamp() + ".jpg")
        val uploadTask = riversRef.putFile(file)

        showProgressDialog()
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            toast(this, it.message.toString())
            hideProgressDialog()
        }.addOnSuccessListener {
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            hideProgressDialog()
//            uploadTask = riversRef.putFile(file)
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
                    if (focusEdit != R.id.et_judul) {
                        val index = getIndexId(focusEdit)
                        listImage.add(index, downloadUri.toString())
                        listImage.removeAt(index + 1)

                    } else if (focusEdit == R.id.et_judul) {
                        listImage.add(0, downloadUri.toString())
                        listImage.removeAt(1)

                    }
                    log(listId.toString())
                    log(listImage.toString())
//                    log(focusEdit.toString())

                } else {
                    toast(this, "Failed to upload")
                }

            }
        }
    }

    private fun addImage(data: Intent?) {
//        if (listImage.size > 0) {
//            listImage.removeAt(listImage.size - 1)
//        }
//        val layoutParent = findViewById<LinearLayout>(R.id.ll_dynamic)

        val imageUri: Uri? = data!!.data
        val imageStream: InputStream? = contentResolver.openInputStream(imageUri!!)
        val selectedImage: Bitmap = BitmapFactory.decodeStream(imageStream)

        if (focusEdit != R.id.et_judul) {
            val newImage = findViewById<ImageView>(focusEdit - TEXT_BASE_ID + IMAGE_BASE_ID)
            newImage.setImageBitmap(selectedImage)
        } else if (focusEdit == R.id.et_judul && listId.isEmpty()) {
            addEditable(hintAwal)
            val newImage = findViewById<ImageView>(IMAGE_BASE_ID + viewId - 1)
            newImage.setImageBitmap(selectedImage)
        } else if (focusEdit == R.id.et_judul && listId.isNotEmpty()) {
            val newImage = findViewById<ImageView>(listId[0] - TEXT_BASE_ID + IMAGE_BASE_ID)
            newImage.setImageBitmap(selectedImage)
        }


//        val newImage = ImageView(applicationContext)
//
//        newImage.layoutParams = (
//                LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//                ))
//
//        newImage.id = IMAGE_BASE_ID + viewId
//        layoutParent.addView(newImage)


//        newImage.setImageBitmap(selectedImage)


//        if (imageId > 1) {
//            val text = findViewById<EditText>(TEXT_BASE_ID + viewId - 1)
//            if (text.text.toString() == "") {
//                text.hint = ""
//            }
//        } else {
//            et_story.hint = ""
//        }

//        addEditable("")

//        imageId++
    }

    private fun addimageView(idNow: Int) {
        val layoutParent = findViewById<RelativeLayout>(R.id.rl_story)
        val newImage = ImageView(applicationContext)

//        newImage.layoutParams = (
//                LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    200
//                ))

        val param = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )

        newImage.id = IMAGE_BASE_ID + viewId
        newImage.adjustViewBounds = true
//        newImage.setImageResource(R.color.colorBlack)
        if (getIndexId(newImage.id - IMAGE_BASE_ID + TEXT_BASE_ID) > 0) {
            param.addRule(RelativeLayout.BELOW, listId[getIndexId(idNow)])
        }
//        else if (getIndexId(newImage.id- IMAGE_BASE_ID+ TEXT_BASE_ID) == 0){
//            param.addRule(RelativeLayout.ALIGN_PARENT_TOP)
//        }
        layoutParent.addView(newImage, param)
    }

    private fun removeView(editText: EditText, imageView: ImageView) {
        val layoutParent = findViewById<RelativeLayout>(R.id.rl_story)

        val index = getIndexId(editText.id)
//        if (listId.size > 1) {
        if (editText.text.isNotEmpty()) {
            if (imageView.drawable == null && index != 0) {
                val string = editText.text.toString()

                val idBefore = listId[index - 1]
                val textViewBefore = findViewById<EditText>(idBefore)
                val stringBefore = textViewBefore.text.toString()
                val stringNow = stringBefore + string
                textViewBefore.setText(stringNow)

                val lastIndex = listId.size - 1
                removeId(index)
                listImage.removeAt(index)
                layoutParent.removeView(editText)
                layoutParent.removeView(imageView)
                if (index != 0 && index != lastIndex) {
                    changeViewDeletePosition(index)
                }
            } else if (imageView.drawable == null && index == 0) {
                val string = editText.text.toString()

                val idBefore = R.id.et_judul
                val textViewBefore = findViewById<EditText>(idBefore)
                val stringBefore = textViewBefore.text.toString()
                val stringNow = stringBefore + string
                textViewBefore.setText(stringNow)

                val lastIndex = listId.size - 1
                removeId(index)
                listImage.removeAt(index)
                layoutParent.removeView(editText)
                layoutParent.removeView(imageView)
                if (index != 0 && index != lastIndex) {
                    changeViewDeletePosition(index)
                }

            } else {
                imageView.setImageDrawable(null)
                listImage.removeAt(index)
                listImage.add(index, "")
            }

        }
//            imageView.setImageResource(0)
        else {
            val lastiIndex = listId.size - 1
            removeId(index)
            listImage.removeAt(index)
            layoutParent.removeView(editText)
            layoutParent.removeView(imageView)
            if (index != 0 && index != lastiIndex) {
                changeViewDeletePosition(index)
            }
        }
//        }else if(listId.size == 1){
//            imageView.setImageDrawable(null)
//        }

//        if(editText.text.isEmpty()){
//            editText.visibility = View.GONE
//            imageView.setImageResource(0)
//            imageView.visibility = View.GONE
//        }else{
//
//        }

//        imageId--
    }

    private fun changeViewDeletePosition(index: Int) {
        val nextImageView = findViewById<ImageView>(listId[index] - TEXT_BASE_ID + IMAGE_BASE_ID)
        val idBefore = listId[index - 1]
        val param = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        param.addRule(RelativeLayout.BELOW, idBefore)
        nextImageView.layoutParams = param
    }

    private fun changeViewEnterPosition(index: Int) {
        val lastIndex = listId.size - 1
        if (index + 1 != 0 && index + 1 != lastIndex) {
//            val newImageView = findViewById<ImageView>(listId[index+1]- TEXT_BASE_ID+ IMAGE_BASE_ID)
            val oldImageView = findViewById<ImageView>(listId[index + 2] - TEXT_BASE_ID + IMAGE_BASE_ID)
            val idNow = listId[index + 1]
//            val idNext = listId[index+2]
            val paramOld = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            paramOld.addRule(RelativeLayout.BELOW, idNow)
            oldImageView.layoutParams = paramOld
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addText(hint: String, idNow: Int) {
//        if(listId.isNotEmpty()){
        if (listId.isEmpty()) {
            listId.add(TEXT_BASE_ID + viewId)
            listImage.add("")
        }
        addimageView(idNow)
//        }
//        val layoutParent = findViewById<LinearLayout>(R.id.ll_dynamic)
        val layoutParent = findViewById<RelativeLayout>(R.id.rl_story)
//                val newContext = ContextThemeWrapper(applicationContext, R.style.EditTextNewStory)
        val myEditText = EditText(applicationContext)

//        myEditText.layoutParams = (
//                LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//                ))

        val param = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )


        myEditText.setTextColor(Color.parseColor("#000000"))
        val padding = 16
        val scale: Float = resources.displayMetrics.density
        val paddingDp: Int = (padding * scale + 0.5f).toInt()
        myEditText.setPadding(paddingDp, 0, paddingDp, paddingDp / 2)

//        myEditText.typeface = Typeface.createFromAsset(assets,"font/times_new_roman.ttf")
        myEditText.hint = hint
        myEditText.setText(nextString)
        myEditText.setBackgroundResource(R.color.colorWhite)
        myEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.font_size))
        myEditText.id = TEXT_BASE_ID + viewId

//        if(getIndexId(myEditText.id)>0){
        param.addRule(RelativeLayout.BELOW, myEditText.id - TEXT_BASE_ID + IMAGE_BASE_ID)
//        }

        myEditText.setOnTouchListener { _, _ ->
            toast(myEditText.id.toString())
//            log(myEditText.id.toString())
//            focusEdit = myEditText.id - TEXT_BASE_ID
            return@setOnTouchListener false
        }

        setUpOnKeyEditText(myEditText)

        layoutParent.addView(myEditText, param)

        viewId++
    }

    private fun publish() {
        readData { sCount, _, id ->
            addTextToList()
            for (i in listText) {
                if (i != "") {
                    firstStory = i
                    break
                }
            }
            for (i in listImage) {
                if (i != "") {
                    firstImage = i
                    break
                }
            }

            val sid = id + (sCount.toString())

            val story = Story(
                sid
                , id
                , et_judul.text.toString()
                , firstStory
                , main.getPref(main.userName, "s", this).toString()
                , getCurrentDate()
                , firstImage
                , if (listId.size > 0) listId.size else 0
                , if (listId.size > 0) listId.size else 0
            )
            if(did!=""){
                database.child("draft").child(id).child(did).removeValue()
                database.child(main.draftContent).child(id).child(did).removeValue()
            }
            database.child("story").child(sid).setValue(story)
            database.child("user").child(id).child("sCount").setValue(sCount)
            publishContent(sCount, id)

            hideProgressDialog()
            finish()
        }
    }

    private fun getCurrentDate(): String {
        val c = Calendar.getInstance().time
        val currentDate = SimpleDateFormat("d MMM YYYY")
        return currentDate.format(c)
    }

    private fun publishContent(sCount: Int, id: String) {
//        readData { sCount, id ->
        val sid = "$id$sCount"

//            upload image url
        if (listImage.size > 0) {
            for (i in 0 until listImage.size) {
//                    if(listImage[i] != ""){
                database.child(main.storyContent).child(sid).child("image$i").setValue(listImage[i])
//                    }
            }
        }

//            upload text
        if (listText.size > 0) {
            for (i in 0 until listText.size) {
                database.child(main.storyContent).child(sid).child("text$i").setValue(listText[i])
            }
        }

//        }
    }

    private fun addTextToList() {
//        add text to list
        for (i in listId) {
            val text = findViewById<EditText>(i)
            listText.add(text.text.toString())
        }
    }

    private fun readData(onDataChange: (Int, Int, String) -> Unit) {
        showProgressDialog()

        database.child(main.user).child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    toast(p0.message)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var count = dataSnapshot.child("sCount").getValue(Int::class.java)
                    var dCount = dataSnapshot.child("dCount").getValue(Int::class.java)
                    log(count.toString()+"\n"+dCount.toString())

                    if (count == null && dCount == null) {
                        count = 0
                        dCount = 0
                        log("A")
                        onDataChange(count + 1, dCount + 1, uid)
                    } else if (count == null && dCount != null) {
                        count = 0
                        log("B")
                        onDataChange(count + 1, dCount + 1, uid)
                    } else if (dCount == null && count != null) {
                        dCount = 0
                        log("C")
                        onDataChange(count + 1, dCount + 1, uid)
                    }else{
                        log("D")
                        onDataChange(count!! + 1, dCount!! + 1, uid)
                    }

                }
            })
    }


    private fun getDraft(onDataChange: (Int) -> Unit) {
        database.child(main.draft).child(uid).child(did)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    toast(p0.message)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val judul = dataSnapshot.child("judul").getValue(String::class.java)
                    val textContent = dataSnapshot.child("textContent").getValue(Int::class.java)
//                    val did = dataSnapshot.child("did").getValue(String::class.java)
                    if (textContent != null) {
                        onDataChange(textContent)
                    }
                    et_judul.setText(judul)

                }

            })
    }


    private fun publishDraft() {
        readData { _, dCount, id ->
            addTextToList()
            for (i in listText) {
                if (i != "") {
                    firstStory = i
                    break
                }
            }
            for (i in listImage) {
                if (i != "") {
                    firstImage = i
                    break
                }
            }
            var draftId = id + (dCount.toString()) + "d"
            if (did != "") {
                draftId = did
            }


            val draft = Draft(
                draftId
                , id
                , et_judul.text.toString()
                , firstStory
                , getCurrentDate()
                , firstImage
                , if (listId.size > 0) listId.size else 0
                , if (listId.size > 0) listId.size else 0
            )
            log("sampe buat objec draft")

            if(did !=""){
                database.child("draft").child(id).child(draftId).removeValue()
            }
            database.child("draft").child(id).child(draftId).setValue(draft)
            if(did==""){
                database.child("user").child(id).child("dCount").setValue(dCount)
            }
            log("sampe kirim ke draft dan dCount")
            if(listId.isNotEmpty()){
                publishDraftContent(dCount, id)
            }
            hideProgressDialog()
            finish()
        }
    }

    private fun publishDraftContent(dCount: Int, id: String) {
        //        readData { sCount, id ->
        var draftId = "$id${dCount}d"
        if (did != "") {
            draftId = did
        }

        database.child(main.draftContent).child(id).child(draftId).removeValue()
//            upload image url
        if (listImage.size > 0) {
            for (i in 0 until listImage.size) {
//                    if(listImage[i] != ""){
                database.child(main.draftContent).child(id).child(draftId).child("image$i").setValue(listImage[i])
//                    }
            }
        }

//            upload text
        if (listText.size > 0) {
            for (i in 0 until listText.size) {
                database.child(main.draftContent).child(id).child(draftId).child("text$i").setValue(listText[i])
            }
        }

//        }
    }

    companion object {
        const val REQUEST_IMAGE = 2
        const val IMAGE_BASE_ID = 500
        const val TEXT_BASE_ID = 100
    }
}

