package com.example.busticketreservationsystem.ui.dashboard

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
        this.date = dateViewModel.travelDate
        this.month = dateViewModel.travelMonth
        this.year = dateViewModel.travelYear

        val dialog = DatePickerDialog(requireContext(),this, year, month, date)
        calendar.set(year, month, date)
        dialog.datePicker.minDate = calendar.timeInMillis
        var maxMonth = 0
        var maxYear = year
        if(month <= 5){
            maxMonth = month + 6
        }else{
            maxMonth += month - 12
            maxYear += 1
        }
        calendar.set(maxYear, maxMonth, date)
        dialog.datePicker.maxDate = calendar.timeInMillis
        return dialog
    }


    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, date: Int) {
        dateViewModel.travelDate = date
        dateViewModel.travelMonth = month+1
        dateViewModel.travelYear = year
        dateViewModel.travelDateEdited.value = true
    }

    override fun onCancel(dialog: DialogInterface) {
        dateViewModel.apply {
            travelDate = date
            travelMonth = month
            travelYear = year
        }
        super.onCancel(dialog)
    }
}