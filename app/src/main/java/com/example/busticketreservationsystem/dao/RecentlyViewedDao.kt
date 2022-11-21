package com.example.busticketreservationsystem.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.busticketreservationsystem.entity.RecentlyViewed

@Dao
interface RecentlyViewedDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(recentlyViewed: RecentlyViewed)

    @Query("SELECT * FROM recently_viewed_table WHERE userId LIKE :userId")
    fun getRecentlyViewed(userId: Int): List<RecentlyViewed>

}