package com.example.foodatdoor

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [RestEntity::class], version = 1)
abstract class RestaurantDatabase: RoomDatabase() {

    abstract fun restDao(): RestDao

}

