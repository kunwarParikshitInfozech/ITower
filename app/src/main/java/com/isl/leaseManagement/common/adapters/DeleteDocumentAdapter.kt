package com.isl.leaseManagement.common.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.dataClasses.otherDataClasses.SaveAdditionalDocument
import com.isl.leaseManagement.utils.ClickInterfaces
import com.isl.leaseManagement.utils.Utilities.getLastChars
import infozech.itower.R
import infozech.itower.databinding.DeleteDocumentLayoutBinding


class DeleteDocumentAdapter(
    private val documentList: List<SaveAdditionalDocument>,
    private val ctx: BaseActivity,
    private val clickListener: ClickInterfaces.AddAdditionalDocument
) : RecyclerView.Adapter<DeleteDocumentAdapter.DeleteDocHolder>() {

    private lateinit var binding: DeleteDocumentLayoutBinding

    override fun getItemCount(): Int = documentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeleteDocHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.delete_document_layout, parent, false)
        return DeleteDocHolder(binding, ctx, clickListener)
    }

    override fun onBindViewHolder(holder: DeleteDocHolder, position: Int) {
        holder.bindItems(documentList[position])
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class DeleteDocHolder(
        private val binding: DeleteDocumentLayoutBinding,
        private val ctx: BaseActivity,
        private val clickListener: ClickInterfaces.AddAdditionalDocument
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindItems(item: SaveAdditionalDocument?) {
            item ?: return
            item.fileName?.let {
                binding.docName.text = getLastChars(it, 20)
            }
            item.docSize?.let {
                binding.docSize.text = getLastChars(it, 8)
            }
            binding.deleteDoc.setOnClickListener { clickListener.deleteDocument(item) }
        }
    }
}