package com.example.busticketreservationsystem.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "seat_information_table",
    foreignKeys = [ForeignKey(
        entity = Bus::class,
        childColumns = ["busId"],
        parentColumns = ["busId"]
    )])
data class SeatInformation(

    @PrimaryKey(autoGenerate = true) val seatInfoId: Int,
    @ColumnInfo(index = true) var busId: Int,
    var seatName: String
    
)