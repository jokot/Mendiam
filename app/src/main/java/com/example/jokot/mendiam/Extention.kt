package com.example.jokot.mendiam

import android.content.Context
import android.util.Log
import android.widget.Toast

fun Context.toast(msg: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, length).show()
}

fun log(msg: String) {
    Log.d("LOG_D", msg)
}

