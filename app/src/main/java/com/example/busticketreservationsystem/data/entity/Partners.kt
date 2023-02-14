package com.example.busticketreservationsystem.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "partners_table")
data class Partners(
    @PrimaryKey (autoGenerate = true) val partnerId: Int,
    var partnerName: String,
    var noOfBusesOperated: Int,
    var partnerEmailId: String,
    var partnerMobile: String
)
