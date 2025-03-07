package com.example.busticketreservationsystem.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true) val userId: Int,
    var username: String,
    var emailId: String,
    var mobileNumber: String,
    var password: String,
    var dob: String,
    var gender: String
)
