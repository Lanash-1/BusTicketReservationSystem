package com.example.busticketreservationsystem.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.busticketreservationsystem.data.entity.Bus
import com.example.busticketreservationsystem.data.entity.BusAmenities

@Dao
interface BusAmenitiesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(amenity: BusAmenities)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(amenity: List<BusAmenities>)

    @Query("select amenity from bus_amenities_table where busId like :busId")
    fun getBusAmenities(busId: Int): List<String>



}