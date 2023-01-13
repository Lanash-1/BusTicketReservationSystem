package com.example.busticketreservationsystem.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "recently_viewed_table",
    foreignKeys = [ForeignKey(
        entity = Bus::class,
        childColumns = ["busId"],
        parentColumns = ["busId"]
    ),
    ForeignKey(
        entity = User::class,
        childColumns = ["userId"],
        parentColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )]
    )
data class RecentlyViewed(
    @PrimaryKey(autoGenerate = true) val recentId: Int,
    @ColumnInfo(index = true) var busId: Int,
    @ColumnInfo(index = true) var userId: Int,
    var date: String
)
