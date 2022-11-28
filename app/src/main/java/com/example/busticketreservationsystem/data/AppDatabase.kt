package com.example.busticketreservationsystem.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.busticketreservationsystem.dao.*
import com.example.busticketreservationsystem.entity.*


@Database(entities = [
            Bookings::class,
            Bus::class,
            BusAmenities::class,
            Partners::class,
            PassengerInformation::class,
            RecentlyViewed::class,
            Reviews::class,
            SeatInformation::class,
            User::class
                     ], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun bookingsDao(): BookingsDao
    abstract fun busAmenitiesDao(): BusAmenitiesDao
    abstract fun busDao(): BusDao
    abstract fun partnersDao(): PartnersDao
    abstract fun passengerInformationDao(): PassengerInformationDao
    abstract fun recentlyViewedDao(): RecentlyViewedDao
    abstract fun reviewsDao(): ReviewsDao
    abstract fun seatInformationDao(): SeatInformationDao
    abstract fun userDao(): UserDao

    companion object{
        @Volatile
        private var INSTANCE : AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "user_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}