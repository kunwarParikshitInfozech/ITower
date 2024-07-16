package com.isl.leaseManagement.utils

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import java.math.BigInteger
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.TimeZone

object Utilities {
    fun dpToPx(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

    fun getDateFromISO8601(inputDate: String): String {
        val inputFormatter = DateTimeFormatter.ISO_DATE_TIME
        val outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

        val zonedDateTime = ZonedDateTime.parse(inputDate, inputFormatter)
        val localDateTime = zonedDateTime.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()

        return localDateTime.format(outputFormatter)
    }

    fun toIsoString(dateString: String): String? {
        val parts = dateString.split("/")
        if (parts.size != 3) return null  // Check for valid format

        val year = parts[2].toIntOrNull() ?: return null  // Check for valid year
        val month = parts[1].toIntOrNull()?.minus(1) ?: return null  // Months are 0-based
        val day = parts[0].toIntOrNull() ?: return null  // Check for valid day

        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
        }

        val formatter = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        return formatter.format(calendar.time)
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
        firstOptionName: String,
        secondOptionName: String,
        context: Context,
        title: String,
        message: String,
        firstOptionClicked: () -> Unit,
        secondOptionClicked: () -> Unit
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)

        // Set positive (Yes) button
        builder.setPositiveButton(firstOptionName) { dialog, which ->
            firstOptionClicked()
            dialog.dismiss()
        }

        // Set negative (No) button
        builder.setNegativeButton(secondOptionName) { dialog, which ->
            secondOptionClicked()
            dialog.dismiss()
        }

        builder.create().show()
    }

    fun formatSingleDigitNumber(numberString: String?): String {
        if (numberString == null) return ""
        return if (numberString.length == 1) {
            "0$numberString" // Add leading zero for single digit
        } else {
            numberString // Leave double digits as is
        }
    }

}