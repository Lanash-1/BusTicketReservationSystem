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

    @Query("SELECT * FROM reviews_table")
    fun getReviewsData(): List<Reviews>
}