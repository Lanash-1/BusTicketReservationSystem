package com.example.busticketreservationsystem.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.busticketreservationsystem.entity.Reviews

@Dao
interface ReviewsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(reviews: Reviews)
}