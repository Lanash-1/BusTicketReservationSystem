package com.example.busticketreservationsystem.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "bus_amenities_table",
    foreignKeys = [ForeignKey(
        entity = Bus::class,
        childColumns = ["busId"],
        parentColumns = ["busId"]
    )])
data class BusAmenities(
    @PrimaryKey(autoGenerate = true) val busAmenityId: Int,
    @ColumnInfo(index = true) var busId: Int,
    var amenity: String
)