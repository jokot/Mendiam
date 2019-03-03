package com.example.jokot.mendiam

import android.app.ProgressDialog
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

open class BaseActivity : AppCompatActivity() {

    val pd by lazy {
        ProgressDialog(this)
    }

    fun showProgressDialog() {
        pd.setMessage("Loading...")
        pd.isIndeterminate = true
        pd.show()
    }

    fun hideProgressDialog() {
        if (pd.isShowing) {
            pd.dismiss()
        }
    }

    fun toast(context: Context, msg: String?) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

    public override fun onStop() {
        super.onStop()
        hideProgressDialog()
    }
}

