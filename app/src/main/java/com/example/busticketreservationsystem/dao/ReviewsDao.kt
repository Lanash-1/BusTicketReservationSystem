package com.example.busticketreservationsystem.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.busticketreservationsystem.entity.Reviews

@Dao
interface ReviewsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(reviews: Reviews)

    @Query("SELECT * FROM reviews_table where busId like :busId")
    fun getReviewsData(busId: Int): List<Reviews>

    @Query("SELECT count(*) FROM reviews_table WHERE busId LIKE :busId")
    fun getReviewCount(busId: Int): Int

    @Query("SELECT rating FROM reviews_table WHERE busId LIKE :busId")
    fun getRatingsOfABus(busId: Int): List<Int>

    @Query("SELECT * FROM reviews_table WHERE busId LIKE :busId AND userId LIKE :userId")
    fun getReviewByUser(busId: Int, userId: Int): List<Reviews>

}