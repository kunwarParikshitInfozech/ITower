package com.isl.leaseManagement.bts.captureCandidate.capturedCandidate

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.bts.adapter.ExistingCandidateListAdapter
import com.isl.leaseManagement.dataClasses.responses.ExistingCandidateListResponse
import com.isl.leaseManagement.utils.ClickInterfaces
import infozech.itower.R
import infozech.itower.databinding.ActivityCapturedCandidateBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class CapturedCandidateActivity : BaseActivity() {

    private lateinit var binding: ActivityCapturedCandidateBinding
    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_captured_candidate)
        init()
    }

    private fun init() {
        getAllCandidatesAndProceed()
        setClickListeners()
    }

    private fun setClickListeners() {
        binding.backIv.setOnClickListener { finish() }
    }

    private fun getAllCandidatesAndProceed() {
        disposable =
            commonDatabase.existingCandidateListResponseItemDao()
                .getAllCandidates()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ candidatesList ->
                    setAdapter(candidatesList)
                }, { error ->
                })
    }

    private fun setAdapter(candidatesList: List<ExistingCandidateListResponse.ExistingCandidateListResponseItem>) {
        val capturedCandidateAdapter = ExistingCandidateListAdapter(
            candidatesList = ArrayList(candidatesList),
            this,
            object : ClickInterfaces.ExistingCandidateSelection {
                override fun candidateClicked(selectedCandidateData: ExistingCandidateListResponse.ExistingCandidateListResponseItem) {
                    candidateClicked()
                }
            })
        binding.capturedCandidatesRv.layoutManager = LinearLayoutManager(this)
        binding.capturedCandidatesRv.adapter = capturedCandidateAdapter

    }

    private fun candidateClicked() {

    }

}