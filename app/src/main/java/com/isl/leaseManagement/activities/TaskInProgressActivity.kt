package com.isl.leaseManagement.activities

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.isl.leaseManagement.base.BaseActivity
import infozech.itower.R
import infozech.itower.databinding.ActivityTaskInProgressBinding

class TaskInProgressActivity : BaseActivity() {
    private lateinit var binding: ActivityTaskInProgressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_task_in_progress)
        init()
    }

    private fun init() {

    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }
}