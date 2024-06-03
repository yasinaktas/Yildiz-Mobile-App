package com.yapps.mobilodev2.questionAnswerModule.question

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "question_table")
data class Question (
    @PrimaryKey(autoGenerate = true)
    val id:Long = 0L,
    var quizId:Long,
    var referenceId:Long,
    var question:String,

)