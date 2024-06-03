package com.yapps.mobilodev2.questionAnswerModule.question

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.yapps.mobilodev2.Report

@Dao
interface QuestionDao {
    @Insert
    suspend fun insert(question: Question)

    @Insert
    suspend fun insertAll(question: List<Question>)

    @Update
    suspend fun update(question: Question)

    @Delete
    suspend fun delete(question: Question)

    @Query("SELECT * FROM question_table WHERE quizId = :quizId")
    fun getQuestions(quizId: Long): LiveData<List<Question>>

    @Query("SELECT * FROM question_table WHERE id = :id")
    fun getQuestionById(id: Long): LiveData<List<Question>>
}