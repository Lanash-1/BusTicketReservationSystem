package com.example.busticketreservationsystem.viewmodel

import androidx.lifecycle.ViewModel

class SortAndFilterViewModel: ViewModel() {

    var checkedList = listOf<Int>()
    var selectedSort: Int? = null
}

