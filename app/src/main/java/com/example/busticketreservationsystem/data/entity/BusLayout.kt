package com.example.busticketreservationsystem.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "bus_layout_table",
    foreignKeys = [ForeignKey(
        entity = Bus::class,
        childColumns = ["busId"],
        parentColumns = ["busId"]
    )]
)
data class BusLayout(
    @PrimaryKey (autoGenerate = true) val busLayoutId: Int,
    @ColumnInfo(index = true) var busId: Int,
    var numberOfDecks: Int,
    var lowerDeckSeatType: String,
    var lowerLeftColumnCount: Int,
    var lowerRightColumnCount: Int,
    var upperLeftColumnCount: Int,
    var upperRightColumnCount: Int,
    var lowerLeftSeatCount: Int,
    var lowerRightSeatCount: Int,
    var upperLeftSeatCount: Int,
    var upperRightSeatCount: Int,
)
