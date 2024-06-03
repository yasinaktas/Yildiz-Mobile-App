package com.yapps.mobilodev2.questionAnswerModule.question

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Question::class], version = 1, exportSchema = false)
abstract class QuestionDatabase:RoomDatabase() {
    abstract fun questionDao():QuestionDao
}