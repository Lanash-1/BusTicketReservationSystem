package com.example.busticketreservationsystem.ui.editprofile

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.busticketreservationsystem.viewmodel.DateViewModel
import java.util.*

class DatePickerFragment: DialogFragment(), DatePickerDialog.OnDateSetListener {

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
        this.date = dateViewModel.birthDate
        this.month = dateViewModel.birthMonth
        this.year = dateViewModel.birthYear

        dateViewModel.editedDate = dateViewModel.birthDate
        dateViewModel.editedMonth = dateViewModel.birthMonth
        dateViewModel.editedYear = dateViewModel.birthYear

        val dialog = DatePickerDialog(requireContext(),this, year, month, date)
        calendar.set(year-13, month, date)
        dialog.datePicker.maxDate = calendar.timeInMillis
        return dialog
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, date: Int) {
        dateViewModel.birthDate = date
        dateViewModel.birthMonth = month+1
        dateViewModel.birthYear = year
        dateViewModel.birthDateEdited.value = true
    }

    override fun onCancel(dialog: DialogInterface) {
        dateViewModel.apply {
            birthDate = date
            birthMonth = month
            birthYear = year
        }
        super.onCancel(dialog)
    }

}