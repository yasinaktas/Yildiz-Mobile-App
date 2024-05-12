package com.yapps.mobilodev2

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "report_table")
data class Report(
    @PrimaryKey(autoGenerate = true)
    val id:Long = 0L,
    @ColumnInfo(name = "scope")
    var scope:String,
    @ColumnInfo(name = "date")
    var date:Long,
    @ColumnInfo(name = "courseId")
    var courseId:String,
    @ColumnInfo(name = "recipient")
    var recipient:String,
    @ColumnInfo(name = "subject")
    var subject:String,
    @ColumnInfo(name = "body")
    var body:String,
    @ColumnInfo(name = "notified")
    var notified:String,
    @ColumnInfo(name = "instructors")
    var instructors:String

)