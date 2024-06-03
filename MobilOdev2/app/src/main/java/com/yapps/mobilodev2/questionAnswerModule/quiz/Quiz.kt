package com.yapps.mobilodev2.questionAnswerModule.quiz

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_table")
data class Quiz (
    @PrimaryKey(autoGenerate = true)
    val id:Long = 0L,
    var instructorId: Long,
    var courseId: String,
    var startTime: Long,
    var time:Long,
    var location: String,
)