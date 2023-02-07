package com.example.busticketreservationsystem.viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel

class NavigationViewModel: ViewModel() {

    var fragment: Fragment? = null

    var previousFragment: Fragment? = null

    var adminNavigation: Fragment? = null

}