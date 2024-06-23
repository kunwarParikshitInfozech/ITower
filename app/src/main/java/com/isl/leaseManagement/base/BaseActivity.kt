package com.isl.leaseManagement.base

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import infozech.itower.R

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun launchActivity(activityClass: Class<out BaseActivity>) {
        startActivity(Intent(this, activityClass))
    }

    fun launchActivityClearTop(activityClass: Class<out BaseActivity>) {
        startActivity(Intent(this, activityClass).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
    }

    fun launchActivityWithBundle(activityClass: Class<out BaseActivity>, bundle: Bundle) {
        val intent = Intent(this, activityClass)
        intent.putExtras(bundle)
        startActivity(intent)
    }

}