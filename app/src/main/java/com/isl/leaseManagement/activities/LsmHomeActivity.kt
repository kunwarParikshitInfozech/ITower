package com.isl.leaseManagement.activities

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.fragments.home.LsmHomeFragment
import com.isl.leaseManagement.fragments.notifications.LsmNotificationsFragment
import com.isl.leaseManagement.fragments.profile.LsmProfileFragment
import com.isl.leaseManagement.fragments.tasks.LsmTasksFragment
import infozech.itower.R
import infozech.itower.databinding.ActivityTasksHomeBinding

class LsmHomeActivity : BaseActivity() {
    private lateinit var binding: ActivityTasksHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks_home)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_tasks_home)
        init()
    }

    private fun init() {
        openFragment(LsmTasksFragment())
        binding.bottomNavigationView.selectedItemId = R.id.tasks
        setClickListeners()
    }

    private fun setClickListeners() {
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    openFragment(LsmHomeFragment())
                    true
                }

                R.id.tasks -> {
                    openFragment(LsmTasksFragment())
                    true
                }

                R.id.notifications -> {
                    openFragment(LsmNotificationsFragment())
                    true
                }

                R.id.profile -> {
                    openFragment(LsmProfileFragment())
                    true
                }

                else -> false
            }
        }
    }

    private fun openFragment(fragment: Fragment) {
        binding.leaseManagementToolbar.visibility =
            if (fragment is LsmHomeFragment) View.INVISIBLE else View.VISIBLE
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}