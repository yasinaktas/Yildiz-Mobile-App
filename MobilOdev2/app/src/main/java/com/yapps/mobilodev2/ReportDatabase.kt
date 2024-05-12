package com.yapps.mobilodev2

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Report::class], version = 1, exportSchema = false)
abstract class ReportDatabase:RoomDatabase() {
    abstract fun reportDao():ReportDao
}