package com.example.busticketreservationsystem.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey


@Entity(
    tableName = "chat_table",
    foreignKeys = [ForeignKey(
        entity = User::class,
        childColumns = ["userId"],
        parentColumns = ["userId"],
        onDelete = CASCADE
    )]
)

data class Chat(
    @PrimaryKey(autoGenerate = true) val messageId: Int,
    @ColumnInfo(index = true) var userId: Int,
    var message: String,
    var timestamp: String,
    var messageType: String
)
