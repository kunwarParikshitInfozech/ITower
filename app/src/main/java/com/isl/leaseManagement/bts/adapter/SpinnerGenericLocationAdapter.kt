package com.isl.leaseManagement.bts.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.dataClasses.responses.LocationsListResponse
import infozech.itower.R

enum class AreaType {
    REGION,
    DISTRICT,
    CITY
}

class SpinnerGenericLocationAdapter(
    context: BaseActivity,
    items: List<LocationsListResponse.LocationsListResponseItem>,
    private val listener: OnItemSelectedListener,
    private val areaType: AreaType
) : ArrayAdapter<LocationsListResponse.LocationsListResponseItem>(
    context, android.R.layout.simple_spinner_item, items.toMutableList() // Create a mutable list
) {

    private var mutableItems: MutableList<LocationsListResponse.LocationsListResponseItem> = items.toMutableList()

    interface OnItemSelectedListener {
        fun onItemSelected(item: LocationsListResponse.LocationsListResponseItem, position: Int)
    }

    private var selectedPosition: Int = -1

    init {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    override fun getCount(): Int {
        return mutableItems.size
    }

    override fun getItem(position: Int): LocationsListResponse.LocationsListResponseItem? {
        return mutableItems[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent) as TextView
        view.text = when (areaType) {
            AreaType.REGION -> mutableItems[position].regionName
            AreaType.DISTRICT -> mutableItems[position].districtName
            AreaType.CITY -> mutableItems[position].cityName
        }
        view.setTextColor(context.getColor(R.color.color_606F8A))
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView
        view.text = when (areaType) {
            AreaType.REGION -> mutableItems[position].regionName
            AreaType.DISTRICT -> mutableItems[position].districtName
            AreaType.CITY -> mutableItems[position].cityName
        }
        view.setTextColor(context.getColor(R.color.color_606F8A))
        return view
    }

    // New method to update the data and refresh the spinner
    fun updateData(newItems: List<LocationsListResponse.LocationsListResponseItem>) {
        mutableItems.clear() // Clear existing data in the mutable list
        mutableItems.addAll(newItems) // Add the new items
        notifyDataSetChanged() // Notify the spinner to refresh the data
        selectedPosition = -1 // Reset selected position after updating the data
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
                val selectedItem = mutableItems[position]
                listener.onItemSelected(selectedItem, position) // Trigger callback
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedPosition = -1 // Reset if nothing is selected
            }
        }
    }

    fun getSelectedItem(): LocationsListResponse.LocationsListResponseItem? {
        return if (selectedPosition >= 0) mutableItems[selectedPosition] else null
    }
}


