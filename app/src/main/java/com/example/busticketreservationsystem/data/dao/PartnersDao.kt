package com.example.busticketreservationsystem.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.busticketreservationsystem.data.entity.Partners

@Dao
interface PartnersDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPartnerData(partner: Partners)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPartnerData(partner: List<Partners>)

    @Query("SELECT * FROM partners_table")
    fun getPartnersData(): List<Partners>

    @Query("SELECT partnerName FROM partners_table WHERE partnerId LIKE :partnerId")
    fun getPartnerName(partnerId: Int): String

    @Query("select * from partners_table where partnerId like :partnerId")
    fun getPartnerDetails(partnerId: Int): Partners

    @Query("update partners_table set partnerName=:partnerName, partnerEmailId=:partnerEmailId, partnerMobile=:partnerMobile where partnerId like :partnerId")
    fun updatePartnerDetails(partnerId: Int, partnerName: String, partnerEmailId: String, partnerMobile: String)

    @Query("update partners_table set noOfBusesOperated = noOfBusesOperated + 1 where partnerId = :partnerId")
    fun updateBusCount(partnerId: Int)

}