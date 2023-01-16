package com.example.busticketreservationsystem.utils.customview

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup

class AdminDashboardCustomView(context: Context?, attrs: AttributeSet?): ViewGroup(context, attrs) {
    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        TODO("Not yet implemented")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // TODO: measure each card of admin dashboard
    }
}