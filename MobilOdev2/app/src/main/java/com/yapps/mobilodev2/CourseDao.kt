package com.yapps.mobilodev2

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Insert
    suspend fun insert(course: Course)

    @Update
    suspend fun update(course: Course)

    @Delete
    suspend fun delete(course: Course)

    @Query("SELECT * FROM course_table")
    fun getAllCourses():LiveData<List<Course>>

    @Query("SELECT * FROM course_table WHERE courseId = :id OR name = :name")
    fun getCourse(id: String, name: String): LiveData<Course>

    @Query("SELECT * FROM course_table WHERE courseId = :id")
    fun getCourseById(id: String): LiveData<Course>

    @Query("SELECT * FROM course_table WHERE courseId = :id")
    suspend fun getCourseByIdSuspend(id: String): Course


}