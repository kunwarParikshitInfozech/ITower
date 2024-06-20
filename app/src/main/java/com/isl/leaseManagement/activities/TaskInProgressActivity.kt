package com.isl.leaseManagement.activities

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.isl.leaseManagement.base.BaseActivity
import infozech.itower.R
import infozech.itower.databinding.ActivityTaskInProgressBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TaskInProgressActivity : BaseActivity() {
    private lateinit var binding: ActivityTaskInProgressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_task_in_progress)
        init()
    }

    private fun init() {
        showProgressBar()
        lifecycleScope.launch {
            delay(2000)
            hideProgressBar()
        }
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }
}