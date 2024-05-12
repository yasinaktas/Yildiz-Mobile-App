package com.yapps.mobilodev2

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Course::class], version = 1, exportSchema = false)
abstract class CourseDatabase:RoomDatabase() {
    abstract fun courseDao():CourseDao
}