package com.example.busticketreservationsystem.model.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.busticketreservationsystem.model.entity.BusAmenities

@Dao
interface BusAmenitiesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(list: List<BusAmenities>)

    @Query("select amenity from bus_amenities_table where busId like :busId")
    fun getBusAmenities(busId: Int): List<String>

}