package com.isl.leaseManagement.utils

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.Window
import infozech.itower.R
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZoneOffset
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

    fun getLastChars(str: String, maxLength: Int): String {
        val length = str.length
        return if (length <= maxLength) str else str.substring(length - maxLength)
    }

}