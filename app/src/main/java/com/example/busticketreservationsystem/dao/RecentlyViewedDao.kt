package com.example.busticketreservationsystem.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.busticketreservationsystem.entity.RecentlyViewed

@Dao
interface RecentlyViewedDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(recentlyViewed: RecentlyViewed)

}