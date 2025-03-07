package com.example.busticketreservationsystem.data.dao

import androidx.room.*
import com.example.busticketreservationsystem.data.entity.RecentlyViewed

@Dao
interface RecentlyViewedDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(recentlyViewed: RecentlyViewed)

    @Query("SELECT * FROM recently_viewed_table WHERE userId LIKE :userId")
    fun getRecentlyViewed(userId: Int): List<RecentlyViewed>

    @Query("select count(*) from recently_viewed_table where userId like :userId and busId like :busId and date like :date")
    fun isAvailable(userId: Int, busId: Int, date: String): Int

    @Delete
    fun deleteRecentlyViewed(recentlyViewed: RecentlyViewed)

}