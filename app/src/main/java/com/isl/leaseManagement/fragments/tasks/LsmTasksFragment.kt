package com.isl.leaseManagement.fragments.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.isl.leaseManagement.adapters.TasksAdapter
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.base.BaseFragment
import infozech.itower.R
import infozech.itower.databinding.FragmentLsmTasksBinding

class LsmTasksFragment : BaseFragment() {

    private var tasksAdapter: TasksAdapter? = null
    private lateinit var binding: FragmentLsmTasksBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLsmTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        setClickListeners()
    }

    private fun setClickListeners() {
        binding.assignedTask.root.setOnClickListener {
            showTasksList()
        }
        val toolbar: View = requireActivity().findViewById(R.id.leaseManagementToolbar)
        val toolbarBackIv = toolbar.findViewById<ImageView>(R.id.toolbarBackIv)
        toolbarBackIv.setOnClickListener { reverseLayoutsVisibility() }
    }

    private fun showTasksList() {
        reverseLayoutsVisibility()
        setTasksListAdapter()
    }

    private fun setTasksListAdapter() {
        val stringList = arrayListOf("a", "b", "c")
        tasksAdapter = TasksAdapter(stringList, activity as BaseActivity)
        binding.tasksRv.layoutManager = LinearLayoutManager(activity)
        binding.tasksRv.adapter = tasksAdapter
    }

    private fun reverseLayoutsVisibility() {
        val toolbar: View = requireActivity().findViewById(R.id.leaseManagementToolbar)
        val profileLayoutToolbar = toolbar.findViewById<ConstraintLayout>(R.id.profileDutyCl)
        val searchSortLayoutToolbar = toolbar.findViewById<ConstraintLayout>(R.id.searchSortCl)
        val isProfileVisible = profileLayoutToolbar.visibility == View.VISIBLE

        profileLayoutToolbar.visibility = if (isProfileVisible) View.GONE else View.VISIBLE
        searchSortLayoutToolbar.visibility = if (isProfileVisible) View.VISIBLE else View.GONE
        binding.assignedUnassignedRl.visibility = if (isProfileVisible) View.GONE else View.VISIBLE
        binding.tasksRv.visibility = if (isProfileVisible) View.VISIBLE else View.GONE
    }

}