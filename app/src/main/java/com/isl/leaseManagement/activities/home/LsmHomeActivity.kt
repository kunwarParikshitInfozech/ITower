package com.isl.leaseManagement.activities.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.isl.dao.cache.AppPreferences
import com.isl.itower.HomeActivity
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.dataClass.requests.FetchDeviceIDRequest
import com.isl.leaseManagement.fragments.notifications.LsmNotificationsFragment
import com.isl.leaseManagement.fragments.profile.LsmProfileFragment
import com.isl.leaseManagement.fragments.tasks.LsmTasksFragment
import com.isl.leaseManagement.sharedPref.KotlinPrefkeeper
import infozech.itower.R
import infozech.itower.databinding.ActivityTasksHomeBinding
import java.util.UUID

class LsmHomeActivity : BaseActivity() {

    private lateinit var binding: ActivityTasksHomeBinding
    private lateinit var viewModel: LsmHomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tasks_home)
        init1()
    }

    private fun init1() {
        val factory = LsmHomeViewModelFactory(LsmHomeRepository())
        viewModel = ViewModelProvider(this, factory)[LsmHomeViewModel::class.java]
        fetchDeviceId()
    }

    private fun fetchDeviceId() {
        var mAppPref: AppPreferences? = null
        mAppPref = AppPreferences(this@LsmHomeActivity)
        val deviceToken = mAppPref.gcmRegistationId
        val loginId = mAppPref.loginId
        val uuid = UUID.randomUUID()

        val fetchDeviceIDRequest =
            FetchDeviceIDRequest(
                loginId = loginId,
                pushToken = deviceToken,
                deviceId = uuid.toString()
            )
        viewModel.fetchUserId(
            { successResponse ->
                successResponse?.let { response ->
                    hideProgressBar()
                    response.deviceId?.let {
                        showToastMessage(it)
                        init2()
                    }
                }
            },
            { errorMessage ->
                hideProgressBar()
                init2()
                showToastMessage("Unable to fetch userID!")
            },
            body =
            fetchDeviceIDRequest
        )
    }

    private fun init2() {
        openFragment(LsmTasksFragment())
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

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }


}