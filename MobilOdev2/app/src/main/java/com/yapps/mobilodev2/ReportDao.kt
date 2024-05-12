package com.yapps.mobilodev2

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {
    @Insert
    suspend fun insert(report:Report)

    @Update
    suspend fun update(report:Report)

    @Delete
    suspend fun delete(report:Report)

    @Query("SELECT * FROM report_table")
    fun getReports():LiveData<List<Report>>

    @Query("SELECT * FROM report_table WHERE courseId =:id")
    fun getReportsWithCourse(id:String):LiveData<List<Report>>

    @Query("SELECT * FROM report_table WHERE instructors LIKE '%' || :id || '%' AND scope LIKE '%Scope%'")
    fun getReportsWithInstructorId(id:String):LiveData<List<Report>>

    @Query("SELECT * FROM report_table WHERE scope LIKE '%App%'")
    fun getReportsWithAdmin():LiveData<List<Report>>

}