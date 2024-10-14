package com.isl.leaseManagement.base

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.isl.dao.cache.AppPreferences
import com.isl.leaseManagement.roomKotlin.db.CommonDatabase
import infozech.itower.R

open class BaseActivity : AppCompatActivity() {

    var appPreferences: AppPreferences? = null
    lateinit var commonDatabase: CommonDatabase
    var userId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        init()
    }

    private fun init() {
        appPreferences = AppPreferences(this)
        commonDatabase = CommonDatabase.getDatabase(this)
    }

    fun launchActivity(activityClass: Class<out BaseActivity>) {
        startActivity(Intent(this, activityClass))
    }

    fun launchActivityForResult(activityClass: Class<out BaseActivity>, intentCode: Int) {
        startActivityForResult(Intent(this, activityClass), intentCode)
    }

    fun launchActivityWithIntent(intent: Intent) {
        startActivity(intent)
    }

    fun launchNewActivityCloseAllOther(newActivity: Class<out BaseActivity>) {
        val intent = Intent(this, newActivity)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    fun showToastMessage(string: String?) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
    }

    fun loadFragmentInFrameLayout(fragment: Fragment, frameLayoutID: Int, bundle: Bundle? = null) {
        bundle?.let {
            fragment.arguments = it
        }
        supportFragmentManager.beginTransaction()
            .replace(frameLayoutID, fragment)
            .commit()
    }

}