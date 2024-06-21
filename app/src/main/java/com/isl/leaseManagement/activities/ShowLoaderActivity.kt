package com.isl.leaseManagement.activities

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.isl.leaseManagement.base.BaseActivity
import infozech.itower.R
import infozech.itower.databinding.ActivityShowLoaderBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ShowLoaderActivity : BaseActivity() {
    private lateinit var binding: ActivityShowLoaderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_loader)
        init()
    }

    private fun init() {
        showProgressBar()
        lifecycleScope.launch {
            delay(1000)
            launchActivity(TaskInProgressActivity::class.java)
            finish()
        }
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }
}