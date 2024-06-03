package com.yapps.mobilodev2.questionAnswerModule.result

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.yapps.mobilodev2.Report

@Dao
interface ResultDao {
    @Insert
    suspend fun insert(result: Result)

    @Update
        suspend fun update(result: Result)

    @Delete
    suspend fun delete(result: Result)

    @Query("SELECT * FROM result_table")
    fun getResults(): LiveData<List<Result>>

    @Query("SELECT * FROM result_table WHERE quizId = :quizId")
    fun getResultsByQuiz(quizId:Long): LiveData<List<Result>>

    @Query("SELECT COUNT(*) FROM result_table WHERE quizId = :quizId AND questionId = :questionId AND answerId = :answerId AND answer = 1")
    suspend fun getResultsByCount(quizId:Long,questionId:Long,answerId:Long): Int

    /*@Query("SELECT answer FROM result_table WHERE quizId = :quizId AND questionId = :questionId AND answerId = :answerId AND studentId = :studentId")
    suspend fun getAnswer(quizId:Long,questionId:Long,answerId:Long,studentId:Long): Boolean*/

    @Query("SELECT * FROM result_table WHERE quizId = :quizId AND questionId = :questionId AND answerId = :answerId AND studentId = :studentId")
    fun getAnswer(quizId:Long,questionId:Long,answerId:Long,studentId:Long): LiveData<List<Result>>

    @Query("SELECT * FROM result_table WHERE quizId = :quizId AND studentId = :studentId")
    fun getStudentResults(quizId:Long,studentId:Long): LiveData<List<Result>>
}