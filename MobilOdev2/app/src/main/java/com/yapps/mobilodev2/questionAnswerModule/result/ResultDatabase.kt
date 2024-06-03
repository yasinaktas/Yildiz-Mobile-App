package com.yapps.mobilodev2.questionAnswerModule.result

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Result::class], version = 2, exportSchema = false)
abstract class ResultDatabase:RoomDatabase() {
    abstract fun resultDao():ResultDao
}