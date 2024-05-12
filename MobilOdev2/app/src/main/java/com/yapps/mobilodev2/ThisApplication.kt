package com.yapps.mobilodev2

import android.app.Application
import android.content.Context
import androidx.room.Room

class ThisApplication:Application() {

    companion object{
        lateinit var userDao:UserDao
        lateinit var courseDao:CourseDao
        lateinit var reportDao: ReportDao
        lateinit var sharedPrefs: SharedPrefs
    }

    override fun onCreate() {
        super.onCreate()

        val userDb = Room.databaseBuilder(applicationContext,UserDatabase::class.java,"user_database").build()
        val courseDb = Room.databaseBuilder(applicationContext,CourseDatabase::class.java,"course_database").build()
        val reportDb = Room.databaseBuilder(applicationContext,ReportDatabase::class.java,"report_database").build()
        userDao = userDb.userDao()
        courseDao = courseDb.courseDao()
        reportDao = reportDb.reportDao()

        sharedPrefs = SharedPrefs(this)
    }

}