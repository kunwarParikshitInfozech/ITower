package com.isl.leaseManagement.bts.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.isl.leaseManagement.base.BaseActivity

class SpinnerGenericYesNoAdapter(
    context: BaseActivity
) : ArrayAdapter<String>(
    context, android.R.layout.simple_spinner_item, predefinedValues()
) {

    private var selectedPosition: Int = -1 // To keep track of the selected position

    init {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent) as TextView
        view.text = getItem(position) // Display string value
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView
        view.text = getItem(position)
        return view
    }

    fun getSelectedItem(): String? {
        return if (selectedPosition >= 0) getItem(selectedPosition) else null

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
                parent.requestFocus() // Request focus to prevent scrolling
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedPosition = -1 // Reset if nothing is selected
            }
        }
    }

    companion object {
        private fun predefinedValues(): List<String> {
            return listOf("Choose an option", "Yes", "No", "Unavailable")
        }
    }
}
