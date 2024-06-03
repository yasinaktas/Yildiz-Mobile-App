package com.yapps.mobilodev2.questionAnswerModule.quiz

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Quiz::class], version = 1, exportSchema = false)
abstract class QuizDatabase:RoomDatabase() {
    abstract fun quizDao():QuizDao
}