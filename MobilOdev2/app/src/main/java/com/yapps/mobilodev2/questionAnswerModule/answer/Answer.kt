package com.yapps.mobilodev2.questionAnswerModule.answer

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "answer_table")
data class Answer (
    @PrimaryKey(autoGenerate = true)
    val id:Long = 0L,
    var questionId:Long,
    var answer:String,
    var isCorrect:Boolean,
    var checked:Boolean

)