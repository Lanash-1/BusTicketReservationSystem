package com.example.busticketreservationsystem.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "seat_information_table",
    foreignKeys = [ForeignKey(
        entity = PassengerInformation::class,
        childColumns = ["passengerId"],
        parentColumns = ["passengerId"]
    )])
data class SeatInformation(
    @PrimaryKey(autoGenerate = true) val seatInfoId: Int,
    @ColumnInfo(index = true) var passengerId: Int,
    var seatNo: Int,
    var seatType: String
)
