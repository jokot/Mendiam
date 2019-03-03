package com.example.jokot.mendiam

import android.content.Context
import android.widget.Toast

fun Context.toast(context: Context, msg: String, length: Int = Toast.LENGTH_LONG) {
    Toast.makeText(context, msg, length).show()
}

