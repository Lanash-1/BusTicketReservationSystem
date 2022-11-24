package com.example.busticketreservationsystem.entity

import android.view.inspector.IntFlagMapping
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "bookings_table",
    foreignKeys = [ForeignKey(
        entity = Bus::class,
        childColumns = ["busId"],
        parentColumns = ["busId"]
    ),
    ForeignKey(
        entity = User::class,
        childColumns = ["userId"],
        parentColumns = ["userId"],
        onDelete = CASCADE
    )])
data class Bookings(
    @PrimaryKey(autoGenerate = true) val bookingId: Int,
    @ColumnInfo(index = true) var userId: Int,
    @ColumnInfo(index = true) var busId: Int,
    var contactEmail: String,
    var contactNumber: String,
    var boardingPoint: String,
    var droppingPoint: String,
    var totalCost: Double,
    var bookedTicketStatus: String,
    var noOfTicketsBooked: Int,
    var date: String
)
