package com.example.busticketreservationsystem.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.busticketreservationsystem.data.entity.PassengerInformation

@Dao
interface PassengerInformationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(passengerInformation: PassengerInformation)

//    @Query("SELECT * FROM passenger_information_table WHERE bookingId LIKE :bookingId")
//    fun getPassengerInfo(bookingId: Int): List<PassengerInformation>

    @Query("select * from passenger_information_table where bookingId like :bookingId and ticketStatus like :ticketStatus")
    fun getPassengerInfo(bookingId: Int, ticketStatus: String): List<PassengerInformation>

    @Query("select * from passenger_information_table")
    fun getPassengersInformation(): List<PassengerInformation>

    @Query("update passenger_information_table set ticketStatus=:updatedTicketStatus where ticketStatus like :ticketStatus and bookingId like :bookingId")
    fun updatePassengerTicketStatus(ticketStatus: String, bookingId: Int, updatedTicketStatus: String)

    @Query("select count(*) from passenger_information_table where bookingId = :bookingId and ticketStatus = :ticketStatus")
    fun getTicketStatusCount(ticketStatus: String, bookingId: Int): Int

    @Query("update passenger_information_table set ticketStatus=:ticketStatus where passengerId like :passengerId")
    fun cancelPassengerTicket(passengerId: Int, ticketStatus: String)

}