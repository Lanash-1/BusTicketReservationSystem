package com.example.busticketreservationsystem.view.ui

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.busticketreservationsystem.viewmodel.DateViewModel
import java.util.*

class TravelDatePickerFragment: DialogFragment(), DatePickerDialog.OnDateSetListener  {

    private val dateViewModel: DateViewModel by activityViewModels()
    private var date: Int = 0
    private var month: Int = 0
    private var year: Int = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val date = calendar.get(Calendar.DAY_OF_MONTH)
//
        this.date = dateViewModel.date
        this.month = dateViewModel.month
        this.year = dateViewModel.year

        val dialog = DatePickerDialog(requireContext(),this, year, month, date)
        calendar.set(year, month, date)
        dialog.datePicker.minDate = calendar.timeInMillis
        return dialog
//        return DatePickerDialog(requireContext(), this,year, month, date)
    }


    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, date: Int) {
        dateViewModel.date = date
        dateViewModel.month = month+1
        dateViewModel.year = year
        dateViewModel.travelEdited.value = true
    }

    override fun onCancel(dialog: DialogInterface) {
        dateViewModel.apply {
            this.date = date
            this.month = month
            this.year = year
        }
        super.onCancel(dialog)
    }
}