package com.yapps.mobilodev2

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.yapps.mobilodev2.questionAnswerModule.answer.AnswerDao
import com.yapps.mobilodev2.questionAnswerModule.answer.AnswerDatabase
import com.yapps.mobilodev2.questionAnswerModule.question.QuestionDao
import com.yapps.mobilodev2.questionAnswerModule.question.QuestionDatabase
import com.yapps.mobilodev2.questionAnswerModule.quiz.QuizDao
import com.yapps.mobilodev2.questionAnswerModule.quiz.QuizDatabase
import com.yapps.mobilodev2.questionAnswerModule.result.ResultDao
import com.yapps.mobilodev2.questionAnswerModule.result.ResultDatabase

class ThisApplication:Application() {

    companion object{
        lateinit var userDao:UserDao
        lateinit var courseDao:CourseDao
        lateinit var reportDao: ReportDao
        lateinit var answerDao: AnswerDao
        lateinit var questionDao: QuestionDao
        lateinit var quizDao: QuizDao
        lateinit var resultDao: ResultDao
        lateinit var sharedPrefs: SharedPrefs
    }

    override fun onCreate() {
        super.onCreate()

        val userDb = Room.databaseBuilder(applicationContext,UserDatabase::class.java,"user_database").build()
        val courseDb = Room.databaseBuilder(applicationContext,CourseDatabase::class.java,"course_database").build()
        val reportDb = Room.databaseBuilder(applicationContext,ReportDatabase::class.java,"report_database").build()
        val answerDb = Room.databaseBuilder(applicationContext,AnswerDatabase::class.java,"answer_database").build()
        val questionDb = Room.databaseBuilder(applicationContext,QuestionDatabase::class.java,"question_database").build()
        val quizDb = Room.databaseBuilder(applicationContext,QuizDatabase::class.java,"quiz_database").build()
        val resultDb = Room.databaseBuilder(applicationContext,ResultDatabase::class.java,"result_database").build()
        userDao = userDb.userDao()
        courseDao = courseDb.courseDao()
        reportDao = reportDb.reportDao()
        answerDao = answerDb.answerDao()
        questionDao = questionDb.questionDao()
        quizDao = quizDb.quizDao()
        resultDao = resultDb.resultDao()

        sharedPrefs = SharedPrefs(this)
    }

}