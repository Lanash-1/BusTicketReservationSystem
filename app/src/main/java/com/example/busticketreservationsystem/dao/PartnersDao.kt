package com.example.busticketreservationsystem.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.busticketreservationsystem.entity.Partners

@Dao
interface PartnersDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(partners: Partners)
}