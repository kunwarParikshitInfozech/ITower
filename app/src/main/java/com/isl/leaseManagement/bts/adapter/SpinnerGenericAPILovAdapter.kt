package com.isl.leaseManagement.bts.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.dataClasses.responses.DropdownDataResponse
import infozech.itower.R

class SpinnerGenericAPILovAdapter(
    private val context: BaseActivity,
    private val items: List<DropdownDataResponse.DropdownDataResponseItem>
) : ArrayAdapter<DropdownDataResponse.DropdownDataResponseItem>(
    context, android.R.layout.simple_spinner_item, items
) {
    interface OnItemSelectedListener {
        fun onItemSelected(position: Int)
    }

    private var selectedPosition: Int = -1 // To keep track of the selected position

    init {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent) as TextView
        view.text = items[position].paramId

        // Apply red color to the first item
        if (position == 0) {
            view.setTextColor(context.getColor(R.color.color_B1B7C8))
        } else {
            view.setTextColor(context.getColor(R.color.color_606F8A)) // Default color for other items
        }
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView
        view.text = items[position].paramId

        // Apply red color to the first item in the dropdown
        if (position == 0) {
            view.setTextColor(context.getColor(R.color.color_B1B7C8))
        } else {
            view.setTextColor(context.getColor(R.color.color_606F8A)) // Default color for other items
        }
        return view
    }

    fun attachSpinner(spinner: Spinner) {
        spinner.adapter = this
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedPosition = position // Update selected position
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedPosition = -1 // Reset if nothing is selected
            }
        }
    }

    fun getSelectedItem(): DropdownDataResponse.DropdownDataResponseItem? {
        return if (selectedPosition >= 0) items[selectedPosition] else null
    }
}
