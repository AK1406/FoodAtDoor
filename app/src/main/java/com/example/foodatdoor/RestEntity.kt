package com.example.foodatdoor

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "restaurants")
data class RestEntity(
    @PrimaryKey val book_id: Int,
    @ColumnInfo(name = "rest_name") val bookName: String,
    @ColumnInfo(name = "rest_price") val bookPrice: String,
    @ColumnInfo(name = "rest_rating") val bookRating: String,
    @ColumnInfo(name = "rest_desc") val bookDesc: String,
    @ColumnInfo(name = "rest_image") val bookImage: String
)

