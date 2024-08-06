package com.isl.leaseManagement.paymentProcess.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.dataClasses.otherDataClasses.SaveAdditionalDocument
import com.isl.leaseManagement.utils.ClickInterfaces
import com.isl.leaseManagement.utils.Utilities.getLastChars
import infozech.itower.R
import infozech.itower.databinding.AdditionalDocumentItemBinding


class AdditionalDocumentsListAdapter(
    private val documentList: List<SaveAdditionalDocument>,
    private val ctx: BaseActivity,
    private val clickListener: ClickInterfaces.AdditionalDocumentList
) : RecyclerView.Adapter<AdditionalDocumentsListAdapter.AdditionalDocHolder>() {

    private lateinit var binding: AdditionalDocumentItemBinding

    override fun getItemCount(): Int = documentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdditionalDocHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding =
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.additional_document_item,
                parent,
                false
            )
        return AdditionalDocHolder(binding, ctx, clickListener)
    }
    override fun onBindViewHolder(holder: AdditionalDocHolder, position: Int) {
        holder.bindItems(documentList[position])
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class AdditionalDocHolder(
        private val binding: AdditionalDocumentItemBinding,
        private val ctx: BaseActivity,
        private val clickListener: ClickInterfaces.AdditionalDocumentList
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindItems(item: SaveAdditionalDocument?) {
            item ?: return
            item.fileName?.let {
                binding.docName.text = getLastChars(it, 16)
            }
            item.docSize?.let {
                binding.docSize.text = getLastChars(it, 10)
            }
            binding.iIconIv.setOnClickListener {
                clickListener.docInfo(item)
            }
            binding.downloadIv.setOnClickListener {
                clickListener.docDownload(item)
            }
        }
    }
}