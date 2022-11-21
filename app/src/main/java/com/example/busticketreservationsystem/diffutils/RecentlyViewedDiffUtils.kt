package com.example.busticketreservationsystem.diffutils

import androidx.recyclerview.widget.DiffUtil
import com.example.busticketreservationsystem.entity.RecentlyViewed

class RecentlyViewedDiffUtils(
    private val oldRecentlyViewedList: List<RecentlyViewed>,
    private val newRecentlyViewedList: List<RecentlyViewed>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldRecentlyViewedList.size
    }

    override fun getNewListSize(): Int {
        return newRecentlyViewedList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldRecentlyViewedList[oldItemPosition] == newRecentlyViewedList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when{
            oldRecentlyViewedList[oldItemPosition].recentId != newRecentlyViewedList[newItemPosition].recentId -> {
                false
            }
            oldRecentlyViewedList[oldItemPosition].busId != newRecentlyViewedList[newItemPosition].busId -> {
                false
            }
            oldRecentlyViewedList[oldItemPosition].userId != newRecentlyViewedList[newItemPosition].userId -> {
                false
            }
            oldRecentlyViewedList[oldItemPosition].date != newRecentlyViewedList[newItemPosition].date-> {
                false
            }
            else -> true
        }
    }
}