package com.example.busticketreservationsystem.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.busticketreservationsystem.entity.*


@Database(entities = [
            Bookings::class,
            Bus::class,
            BusAmenities::class,
            BusTimeInfo::class,
            Partners::class,
            PassengerInformation::class,
            RecentlyViewed::class,
            Reviews::class,
            SeatInformation::class,
            User::class
                     ], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

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