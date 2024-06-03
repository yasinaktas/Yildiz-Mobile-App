package com.yapps.mobilodev2.questionAnswerModule.answer

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Answer::class], version = 1, exportSchema = false)
abstract class AnswerDatabase:RoomDatabase() {
    abstract fun answerDao():AnswerDao
}