package com.isl.leaseManagement.utils

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.google.gson.Gson
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.common.activities.addAdditionalDoc.AddAdditionalDocumentActivity
import com.isl.leaseManagement.paymentProcess.activities.requestDetails.RequestDetailsActivity
import infozech.itower.R
import infozech.itower.databinding.ActionsPopupBinding
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

object Utilities {
    fun dpToPx(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

    fun getDateFromISO8601(inputDate: String): String {
        val inputFormatter = DateTimeFormatter.ISO_DATE_TIME
        val outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val zonedDateTime = ZonedDateTime.parse(inputDate, inputFormatter)
        val localDate = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDate()
        return localDate.format(outputFormatter)
    }

    fun toIsoString(dateString: String): String? {
        val formatter = SimpleDateFormat(
            "dd.MM.yyyy",
            Locale.getDefault()
        ) // Set format with MMM for month name
        val parsedDate = formatter.parse(dateString) ?: return null // Try parsing

        val calendar = Calendar.getInstance().apply {
            time = parsedDate
        }

        val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        isoFormatter.timeZone = TimeZone.getTimeZone("UTC")
        return isoFormatter.format(calendar.time)
    }


    fun showDatePickerFromCurrentDate(
        context: Context,
        onDateSelected: (selectedDate: Calendar) -> Unit
    ) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context,
            { _, yearSelected, monthOfYear, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(yearSelected, monthOfYear, dayOfMonth)
                onDateSelected(selectedDate)
            }, year, month, day
        )
        datePickerDialog.datePicker.minDate = calendar.timeInMillis
        datePickerDialog.show()
    }

    object IBANValidityCheck {
        fun checkIfIbanNumberIsValid(iban: String?): Boolean {
            if (iban == null || iban.length != 27) {
                return false
            }
            val charsRevertedIban = iban.substring(4) + iban.substring(0, 4)
            val numReplacedIban = replaceAlphabetsWithNumbers(charsRevertedIban)
            return isDivisibleBy97Modulo1(numReplacedIban)

            return true
        }

        fun isDivisibleBy97Modulo1(numberString: String): Boolean {
            try {
                val numberBigInteger = BigInteger(numberString)
                val remainder = numberBigInteger.mod(BigInteger.valueOf(97))
                return remainder.equals(BigInteger.ONE)
            } catch (e: NumberFormatException) {
                // Handle the exception here (e.g., log an error, return false)
                println("Error: String '$numberString' cannot be converted to Long.")
                return false  // Or throw an exception if desired
            }
        }

        fun replaceAlphabetsWithNumbers(text: String): String {
            val alphabetMap =
                ('a'..'z').mapIndexed { index, char -> char to (index + 10).toString() }.toMap()
            val result = StringBuilder()
            for (char in text) {
                result.append(alphabetMap[char.toLowerCase()] ?: char)
            }
            return result.toString()
        }
    }

    fun showYesNoDialog(
        context: Context,
        message: String,
        firstOptionName: String,
        secondOptionName: String,
        optionSelection: ClickInterfaces.TwoOptionSelection
    ) {
        try {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.two_options_popup_layout)
            if (!dialog.isShowing) {
                dialog.show()
            }

            val tvMsg: CustomTextView = dialog.findViewById(R.id.message)
            val btnOption1: CustomTextView = dialog.findViewById(R.id.option1Btn)
            val tvTitle: CustomTextView = dialog.findViewById(R.id.title)
            val btnOption2: CustomTextView = dialog.findViewById(R.id.option2Btn)
            tvMsg.visibility = View.VISIBLE
            tvTitle.gravity = Gravity.CENTER
            tvMsg.text = message

            tvTitle.setPadding(30, 0, 30, 0)
            btnOption1.text = firstOptionName
            btnOption2.text = secondOptionName
            btnOption1.setOnClickListener {
                optionSelection.option1Selected()
                dialog.dismiss()
            }
            btnOption2.setOnClickListener {
                optionSelection.option2Selected()
                dialog.dismiss()
            }
        } catch (e: java.lang.Exception) {//no need
        }
    }


    fun formatSingleDigitNumber(numberString: String?): String {
        if (numberString == null) return ""
        return if (numberString.length == 1) {
            "0$numberString" // Add leading zero for single digit
        } else {
            numberString // Leave double digits as is
        }
    }

    fun getLastChars(str: String?, maxLength: Int): String? {
        if (str == null) return null
        val length = str.length
        return if (length <= maxLength) str else str.substring(length - maxLength)
    }

//    fun initializeDropDownWithStringAndIdArray(
//        firstHintText: String = "Choose an option",
//        context: BaseActivity,
//        stringsArray: Array<String>,
//        spinner: Spinner,
//        commonInterface: ClickInterfaces.CommonInterface
//    ) {
////        val adapter =
////            ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, stringsArray){
////
////            }
//
//
//        val adapter = object : ArrayAdapter<String>(.layout.spinner_item_layout, stringsArray) {
//            override fun isEnabled(position: Int): Boolean {
//                // Disable the first item from being selected (it acts as a placeholder)
//                return position != 0
//            }
//
//            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
//                val view = super.getDropDownView(position, convertView, parent)
//                val textView = view.findViewById<TextView>(R.id.spinnerItem)
//
//                // Set the color for the placeholder text
//                if (position == 0) {
//                    textView.setTextColor(Color.GRAY)
//                } else {
//                    textView.setTextColor(Color.BLACK)
//                }
//
//                return view
//            }
//        }
//
//
//        spinner.adapter = adapter
//
//        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                val selectedItem = parent.getItemAtPosition(position) as String
//                commonInterface.triggerWithInt(position)
//                commonInterface.triggerWithString(selectedItem)
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {//-
//            }
//
//
//        }
//    }

    fun initializeDropDownWithStringAndIdArray(
        firstHintText: String = "Choose an option",
        context: BaseActivity,
        stringsArray: Array<String>,
        spinner: Spinner,
        commonInterface: ClickInterfaces.CommonInterface
    ) {
        // Create a new list with the hint text as the first item
        val items = listOf(firstHintText) + stringsArray

        // Define a custom ArrayAdapter
        val adapter = object : ArrayAdapter<String>(context, R.layout.spinner_item_layout, items) {

            // Override to display the selected item in the Spinner
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(R.id.spinnerItem)

                // Set the color for the selected text
                if (position == 0) {
                    textView.setTextColor(Color.GRAY)
                } else {
                    textView.setTextColor(Color.BLACK)
                }

                return view
            }

            override fun isEnabled(position: Int): Boolean {
                // Disable the first item from being selected (it acts as a placeholder)
                return position != 0
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view.findViewById<TextView>(R.id.spinnerItem)

                // Set the color for the placeholder text
                if (position == 0) {
                    textView.setTextColor(Color.GRAY)
                } else {
                    textView.setTextColor(Color.BLACK)
                }

                return view
            }
        }

        // Set the adapter to the spinner
        spinner.adapter = adapter

        // Set an item selected listener on the spinner
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    val selectedItem = parent.getItemAtPosition(position) as String
                    commonInterface.triggerWithInt(position)
                    commonInterface.triggerWithString(selectedItem)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No action needed when nothing is selected
            }
        }
    }


    fun showActionPopup(context: BaseActivity) {
        val dialog = Dialog(context)
        val binding = ActionsPopupBinding.inflate(context.layoutInflater)
        dialog.setContentView(binding.root)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window!!.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        val heightInPixels = Utilities.dpToPx(context, 480)
        layoutParams.height = heightInPixels
        layoutParams.gravity = Gravity.BOTTOM
        dialog.window!!.attributes = layoutParams
        dialog.show()
        binding.closeTv.setOnClickListener {
            dialog.dismiss()
        }
        binding.requestDetailsTv.setOnClickListener {
            dialog.dismiss()
            context.launchActivity(RequestDetailsActivity::class.java)
        }
        binding.addAdditionalDocTv.setOnClickListener {
            dialog.dismiss()
            context.launchActivity(AddAdditionalDocumentActivity::class.java)
        }
    }

    fun showDatePickerAndFillDate(view: TextView, context: BaseActivity) {
        showDatePickerFromCurrentDate(context) { selectedDate ->
            val formatter = SimpleDateFormat(
                "dd.MM.yyyy",
                Locale.getDefault()
            ) // Set format with MMM for 3-letter month
            val formattedDate = formatter.format(selectedDate.time)
            view.text = formattedDate
        }
    }

    fun getJsonFromDataClass(dataClass: Any): String {
        val gson = Gson()
        return gson.toJson(dataClass)
    }

    inline fun <reified T> getDataClassFromJson(dataJson: String?): T? {
        return if (dataJson != null) {
            try {
                Gson().fromJson(dataJson, T::class.java)
            } catch (e: Exception) {
                return null
            }
        } else null
    }

}