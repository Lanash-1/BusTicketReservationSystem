package com.example.busticketreservationsystem.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "passenger_information_table",
    foreignKeys = [ForeignKey(
        entity = Bookings::class,
        childColumns = ["bookingId"],
        parentColumns = ["bookingId"],
        onDelete = CASCADE
    )])
data class PassengerInformation(
    @PrimaryKey(autoGenerate = true) val passengerId: Int,
    @ColumnInfo(index = true) var bookingId: Int,
    var passengerName: String,
    var passengerAge: Int,
    var passengerGender: String,
    var passengerSeatCode: String,
    var ticketStatus: String
)
