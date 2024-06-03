package com.yapps.mobilodev2.questionAnswerModule.quiz

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.yapps.mobilodev2.Report

@Dao
interface QuizDao {
    @Insert
    suspend fun insert(quiz: Quiz):Long

    @Update
    suspend fun update(quiz: Quiz)

    @Delete
    suspend fun delete(quiz: Quiz)

    @Query("SELECT * FROM quiz_table ORDER BY startTime DESC")
    fun getQuizzes(): LiveData<List<Quiz>>

    @Query("SELECT * FROM quiz_table WHERE instructorId = :instructorId ORDER BY startTime DESC")
    fun getQuizzesByInstructor(instructorId:Long): LiveData<List<Quiz>>

    @Query("SELECT * FROM quiz_table WHERE id = :id ORDER BY startTime DESC")
    fun getQuizById(id:Long): LiveData<List<Quiz>>

    @Query("SELECT * FROM quiz_table WHERE courseId IN (:courses) ORDER BY startTime DESC")
    fun getQuizzesByCourses(courses:List<String>): LiveData<List<Quiz>>
}