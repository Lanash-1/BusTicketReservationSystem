package com.example.busticketreservationsystem.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "bus_table",
    foreignKeys = [ForeignKey(
        entity = Partners::class,
        childColumns = ["partnerId"],
        parentColumns = ["partnerId"]
    )]
)

data class Bus(
    @PrimaryKey(autoGenerate = true) val busId: Int,
    @ColumnInfo(index = true) var partnerId: Int,
    var busName: String,
    var sourceLocation: String,
    var destination: String,
    var perTicketCost: Double,
    var busType: String,
    var totalSeats: Int,
    var availableSeats: Int,
    var startTime: String,
    var reachTime: String,
    var duration: String,
    var ratingOverall: Double,
    var ratingPeopleCount: Int
)
