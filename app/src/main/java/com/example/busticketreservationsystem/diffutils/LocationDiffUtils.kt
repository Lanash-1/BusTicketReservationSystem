package com.example.busticketreservationsystem.diffutils

import androidx.recyclerview.widget.DiffUtil

class LocationDiffUtils(
    private val oldLocationList: List<String>,
    private val newLocationList: List<String>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldLocationList.size
    }

    override fun getNewListSize(): Int {
        return newLocationList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldLocationList[oldItemPosition] == newLocationList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldLocationList[oldItemPosition] == newLocationList[newItemPosition]
    }

}