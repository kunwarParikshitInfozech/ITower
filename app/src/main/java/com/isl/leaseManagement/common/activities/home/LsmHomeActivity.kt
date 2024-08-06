package com.isl.leaseManagement.common.activities.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.isl.dao.cache.AppPreferences
import com.isl.itower.HomeActivity
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.dataClasses.requests.FetchDeviceIDRequest
import com.isl.leaseManagement.common.fragments.notifications.LsmNotificationsFragment
import com.isl.leaseManagement.common.fragments.profile.LsmProfileFragment
import com.isl.leaseManagement.common.fragments.tasks.LsmTasksFragment
import com.isl.leaseManagement.sharedPref.KotlinPrefkeeper
import infozech.itower.R
import infozech.itower.databinding.ActivityTasksHomeBinding
import java.util.UUID

class LsmHomeActivity : BaseActivity() {
    companion object {
        var taskIsAssigned = true  //used in lsm home and lsm list fragment
        var myFragmentManager: FragmentManager? = null
        fun openFragment(fragment: Fragment, supportFragmentManager: FragmentManager) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    private lateinit var binding: ActivityTasksHomeBinding
    private lateinit var viewModel: LsmHomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tasks_home)
        init1()
    }

    private fun init1() {
        myFragmentManager = supportFragmentManager
        val factory = LsmHomeViewModelFactory(LsmHomeRepository())
        viewModel = ViewModelProvider(this, factory)[LsmHomeViewModel::class.java]
        fetchDeviceId()
    }

    private fun fetchDeviceId() {
        var mAppPref: AppPreferences? = null
        mAppPref = AppPreferences(this@LsmHomeActivity)
        val deviceToken = mAppPref.gcmRegistationId
        val loginId = mAppPref.loginId
        if (KotlinPrefkeeper.deviceUUID == null || KotlinPrefkeeper.deviceUUID!!.isEmpty()) {  //updating only after logout (as data will be cleared)
            KotlinPrefkeeper.deviceUUID = UUID.randomUUID().toString()
        }

        val fetchDeviceIDRequest =
            FetchDeviceIDRequest(
                loginId = loginId,
                pushToken = deviceToken,
                deviceId = KotlinPrefkeeper.deviceUUID
            )
        showProgressBar()
        viewModel.fetchUserId(
            { successResponse ->
                successResponse?.let { response ->
                    hideProgressBar()
                    if (response.userId != null) {
                        KotlinPrefkeeper.lsmUserId = response.userId.toString()
                        init2()
                        //                  showToastMessage("User's ID is ${KotlinPrefkeeper.lsmUserId}")
                    } else {
                        showToastMessage("User's ID is empty")
                        finish()
                    }
                }
            },
            { errorMessage ->
                //for testing only --
//                KotlinPrefkeeper.lsmUserId = "13"
//                hideProgressBar()
//                hideProgressBar()
//                init2()

                //original code
                hideProgressBar()
                showToastMessage("Unable to get user's ID")
                finish()
            },
            body =
            fetchDeviceIDRequest
        )
    }

    private fun init2() {
        openFragment(LsmTasksFragment(), supportFragmentManager)
        binding.bottomNavigationView.selectedItemId = R.id.tasks
        setClickListeners()
        updateOnOffDutyStatus()
    }

    private fun updateOnOffDutyStatus() {
        try {
            val onDutyTv = binding.leaseManagementToolbar.findViewById<TextView>(R.id.onDutyTv)
            if (!KotlinPrefkeeper.isOnDuty) {
                onDutyTv.text = getString(R.string.off_duty)
                onDutyTv.setTextColor(getColor(R.color.orange))
            } else {
                onDutyTv.text = getString(R.string.on_duty)
                onDutyTv.setTextColor(getColor(R.color.color_34C759))
            }
        } catch (e: Exception) {// no need
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
                        openFragment(LsmTasksFragment(), supportFragmentManager)

//                        val toolbar: View = binding.leaseManagementToolbar
//                        val profileLayoutToolbar = toolbar.findViewById<ConstraintLayout>(R.id.profileDutyCl)
//                        val searchSortLayoutToolbar = toolbar.findViewById<ConstraintLayout>(R.id.searchSortCl)
//                        profileLayoutToolbar.visibility = View.VISIBLE
//                        searchSortLayoutToolbar.visibility = View.GONE
                        binding.leaseManagementToolbar.visibility = View.VISIBLE
                    }
                    true
                }

                R.id.notifications -> {
                    if (currentFragment !is LsmNotificationsFragment) {
                        openFragment(LsmNotificationsFragment(), supportFragmentManager)
                        binding.leaseManagementToolbar.visibility = View.GONE
                    }
                    true
                }

                R.id.profile -> {
                    if (currentFragment !is LsmProfileFragment) {
                        openFragment(LsmProfileFragment(), supportFragmentManager)
                        binding.leaseManagementToolbar.visibility = View.GONE
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


    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }


}