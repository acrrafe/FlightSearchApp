package com.example.flightsearchapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [
    FlightSearchAirportEntity::class,
    FlightSearchFavoriteEntity::class,
    PotentialFlightEntity::class
                     ], version = 3, exportSchema = false)
abstract class FlightSearchDatabase: RoomDatabase() {

    abstract fun flightSearchDao(): FlightSearchDao

    companion object {
        @Volatile
        private var Instance: FlightSearchDatabase? = null

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE IF EXISTS potential_flights")
                database.execSQL("""
            CREATE TABLE IF NOT EXISTS potential_flights (
                departure_code TEXT NOT NULL,
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                destination_code TEXT NOT NULL
            )
        """)
                // Insert data into the updated potential_flights table
                database.execSQL("""
            INSERT INTO potential_flights (id, departure_code, destination_code)
            SELECT NULL, a.iata_code AS departure_code, b.iata_code AS destination_code
            FROM airport a
            CROSS JOIN airport b
            WHERE a.iata_code != b.iata_code
        """)

            }
        }


        fun getDatabase(context: Context) : FlightSearchDatabase {
            return Instance ?: synchronized(this){
                Room.databaseBuilder(
                    context,
                    FlightSearchDatabase::class.java,
                    "flight_search_database"
                )
                    .createFromAsset("database/flight_search.db")
                    .addMigrations(MIGRATION_2_3)
                    .build()
                    .also { Instance = it }
            }
        }
    }

}