package com.example.busticketreservationsystem.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "reviews_table",
    foreignKeys = [ForeignKey(
        entity = Bus::class,
        childColumns = ["busId"],
        parentColumns = ["busId"]
    ),ForeignKey(
        entity = User::class,
        childColumns = ["userId"],
        parentColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )])
data class Reviews(
    @PrimaryKey(autoGenerate = true) val reviewId: Int,
    @ColumnInfo(index = true) var userId: Int,
    @ColumnInfo(index = true) var busId: Int,
    var rating: Int,
    var feedback: String,
    var date: String
)
