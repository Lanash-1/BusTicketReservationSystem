package com.example.busticketreservationsystem.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "seat_information_table",
    foreignKeys = [ForeignKey(
        entity = Bus::class,
        childColumns = ["busId"],
        parentColumns = ["busId"]
    ), ForeignKey(
        entity = Bookings::class,
        childColumns = ["bookingId"],
        parentColumns = ["bookingId"],
    )])
data class SeatInformation(

    @PrimaryKey(autoGenerate = true) val seatInfoId: Int,
    @ColumnInfo(index = true) var busId: Int,
    @ColumnInfo(index = true) var bookingId: Int,
    var seatName: String,
    var date: String
    
)