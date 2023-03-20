package com.example.busticketreservationsystem.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.busticketreservationsystem.data.entity.Reviews

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

    @Query("update reviews_table set rating = :rating, feedback = :feedback where reviewId like :reviewId")
    fun updateReviews(reviewId: Int, rating: Int, feedback: String)

    @Query("select * from reviews_table where userId like :userId and busId like :busId")
    fun getUsersBusReview(userId: Int, busId: Int): Reviews

}