package com.isl.leaseManagement.activities.home

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.isl.itower.HomeActivity
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.fragments.notifications.LsmNotificationsFragment
import com.isl.leaseManagement.fragments.profile.LsmProfileFragment
import com.isl.leaseManagement.fragments.tasks.LsmTasksFragment
import com.isl.leaseManagement.sharedPref.KotlinPrefkeeper
import infozech.itower.R
import infozech.itower.databinding.ActivityTasksHomeBinding

class LsmHomeActivity : BaseActivity() {
    private lateinit var binding: ActivityTasksHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tasks_home)
        init()
    }

    private fun init() {
        openFragment(LsmTasksFragment())
        binding.bottomNavigationView.selectedItemId = R.id.tasks
        setClickListeners()
        updateOnOffDutyStatus()
    }

    private fun updateOnOffDutyStatus() {
        val onDutyTv = binding.leaseManagementToolbar.findViewById<TextView>(R.id.onDutyTv)
        if (!KotlinPrefkeeper.isOnDuty) {
            onDutyTv.setText("Off Duty")
        }
        else{
            onDutyTv.setText("On Duty")
        }
    }

    private fun setClickListeners() {
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            val currentFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) // Get current fragment (replace R.id.fragment_container with your actual container id)
            when (menuItem.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    true
                }

                R.id.tasks -> {
                    if (currentFragment !is LsmTasksFragment) {
                        openFragment(LsmTasksFragment())
                    }
                    true
                }

                R.id.notifications -> {
                    if (currentFragment !is LsmNotificationsFragment) {
                        openFragment(LsmNotificationsFragment())
                    }
                    true
                }

                R.id.profile -> {
                    if (currentFragment !is LsmProfileFragment) {
                        openFragment(LsmProfileFragment())
                    }
                    true
                }

                else -> false
            }
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun openFragment(fragment: Fragment) {
//        binding.leaseManagementToolbar.visibility =
//            if (fragment is HomeFragement) View.INVISIBLE else View.VISIBLE
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}