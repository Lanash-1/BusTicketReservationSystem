package com.example.busticketreservationsystem.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.busticketreservationsystem.data.entity.BusLayout

@Dao
interface BusLayoutDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertBusLayout(busLayout: BusLayout)

    @Query("select * from bus_layout_table where busId like :busId")
    fun getLayoutOfBus(busId: Int): BusLayout

    @Update
    fun updateBusLayout(busLayout: BusLayout)

}