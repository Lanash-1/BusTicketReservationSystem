package com.example.busticketreservationsystem.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "recently_viewed_table",
    foreignKeys = [ForeignKey(
        entity = Bus::class,
        childColumns = ["busId"],
        parentColumns = ["busId"]
    )]
    )
data class RecentlyViewed(
    @PrimaryKey(autoGenerate = true) val recentId: Int,
    @ColumnInfo(index = true) var busId: Int
)
