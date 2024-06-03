package com.yapps.mobilodev2.questionAnswerModule.answer

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
@Dao
interface AnswerDao {
    @Insert
    suspend fun insert(answer:Answer)

    @Update
    suspend fun update(answer:Answer)

    @Delete
    suspend fun delete(answer:Answer)

    @Query("SELECT * FROM answer_table")
    fun getAnswers(): LiveData<List<Answer>>

    @Query("SELECT * FROM answer_table WHERE questionId = :questionId")
    fun getAnswersByQuestion(questionId: Long): LiveData<List<Answer>>

    @Query("SELECT * FROM answer_table WHERE questionId IN (:questionId)")
    fun getAnswersByQuiz(questionId: List<Long>): LiveData<List<Answer>>
}