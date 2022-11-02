package com.example.busticketreservationsystem.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "bus_time_info_table",
    foreignKeys = [ForeignKey(
        entity = Bus::class,
        childColumns = ["busId"],
        parentColumns = ["busId"]
    )]
    )
data class BusTimeInfo (
    @PrimaryKey(autoGenerate = true) val busTimeInfoId: Int,
    @ColumnInfo(index = true) var busId: Int,
    var startDate: String,
    var reachDate: String,
    var startTime: String,
    var reachTime: String
)