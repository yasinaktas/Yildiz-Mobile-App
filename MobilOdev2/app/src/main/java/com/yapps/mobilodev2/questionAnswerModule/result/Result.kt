package com.yapps.mobilodev2.questionAnswerModule.result

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "result_table")
data class Result (
    @PrimaryKey(autoGenerate = true)
    val id:Long = 0L,
    var studentId: Long,
    var quizId: Long,
    var questionId: Long,
    var answerId: Long,
    var answer: Boolean
)