package com.isl.leaseManagement.base

import android.content.Context
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {
    lateinit var baseActivity: BaseActivity

    override fun onAttach(activity: Context) {
        super.onAttach(activity)
        this.baseActivity = activity as BaseActivity
    }
}