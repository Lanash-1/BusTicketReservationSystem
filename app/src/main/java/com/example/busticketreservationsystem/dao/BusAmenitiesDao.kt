package com.example.busticketreservationsystem.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.busticketreservationsystem.entity.BusAmenities

@Dao
interface BusAmenitiesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(list: List<BusAmenities>)

}