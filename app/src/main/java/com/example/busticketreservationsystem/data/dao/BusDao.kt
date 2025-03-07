package com.example.busticketreservationsystem.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.busticketreservationsystem.data.entity.Bus

@Dao
interface BusDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertBusData(bus: Bus)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertBusData(bus: List<Bus>)

    @Query("SELECT * FROM bus_table")
    fun getBusData(): List<Bus>

    @Query("UPDATE bus_table SET ratingPeopleCount=:peopleCount, ratingOverall=:overallRating WHERE busId LIKE :busId")
    fun updateBusRating(peopleCount: Int, overallRating: Double, busId: Int)

    @Query("UPDATE bus_table SET availableSeats=:count WHERE busId LIKE :busId")
    fun updateAvailableSeats(count: Int, busId: Int)

    @Query("SELECT * FROM bus_table WHERE busId LIKE :busId")
    fun getBus(busId: Int): Bus

    @Query("update bus_table set ratingPeopleCount = :count, ratingOverall = :average where busId like :busId")
    fun updateRating(busId: Int, count: Int, average: Double)

    @Query("select * from bus_table where sourceLocation like :source and destination like :destination")
    fun getBusOfRoute(source: String, destination: String): List<Bus>

    @Query("select * from bus_table where partnerId = :partnerId")
    fun getBusOfPartner(partnerId: Int): List<Bus>

    @Update
    fun updateBus(bus: Bus)


}