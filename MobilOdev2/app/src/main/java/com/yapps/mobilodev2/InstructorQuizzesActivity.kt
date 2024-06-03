package com.yapps.mobilodev2

import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale

class InstructorQuizzesActivity : AppCompatActivity() {

    private val REQUEST_LOCATION_PERMISSION = 1

    private lateinit var back:ImageView
    private lateinit var add:ImageView
    private lateinit var lvQuizzes:ListView
    private lateinit var lvResults:ListView
    private var instructorId:Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instructor_quizzes)

        back = findViewById(R.id.back)
        add = findViewById(R.id.add)
        lvQuizzes = findViewById(R.id.lv_quizzes)
        lvResults = findViewById(R.id.lv_results)

        val intent = intent
        instructorId = intent.getLongExtra("instructorId",-1L)

        ThisApplication.quizDao.getQuizzesByInstructor(instructorId).observe(this, Observer { quizzes ->
            if(quizzes != null){
                val myStringArray = ArrayList<String>()
                for(quiz in quizzes){
                    val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                    val formattedLatitude = quiz.location.split(" ")[0].substring(0,8)
                    val formattedLongitude = quiz.location.split(" ")[1].substring(0,8)
                    myStringArray.add("\nCourse: ${quiz.courseId}\nDate: ${sdf.format(Date(quiz.startTime))}\nDuration: ${quiz.time / (1000*60)}\nLocation: $formattedLatitude - $formattedLongitude\n")
                }
                val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myStringArray)
                lvQuizzes.setAdapter(adapter)
                lvQuizzes.setOnItemClickListener { _, _, position, _ ->
                    ThisApplication.questionDao.getQuestions(quizzes[position].id).observe(this,Observer{ questions ->
                        if(questions != null){
                            val questionIds = questions.map { it.referenceId }
                            ThisApplication.answerDao.getAnswersByQuiz(questionIds).observe(this,
                                Observer { answers ->
                                    if(answers != null){
                                        CoroutineScope(Job()).launch {
                                            val myStringArrayResult = ArrayList<String>()
                                            for(question in questions){
                                                var str = "${question.question}\n\n"
                                                for(answer in answers){
                                                    if(answer.questionId == question.referenceId){
                                                        str += "(${ThisApplication.resultDao.getResultsByCount(quizId = quizzes[position].id,questionId = question.referenceId,answerId = answer.id)}) ${answer.answer}\n\n"
                                                    }
                                                }
                                                myStringArrayResult.add(str)
                                            }
                                            runOnUiThread{
                                                val adapterResult = ArrayAdapter<String>(this@InstructorQuizzesActivity, android.R.layout.simple_list_item_1, myStringArrayResult)
                                                lvResults.setAdapter(adapterResult)
                                            }
                                        }
                                    }
                                })
                        }
                    })
                }
            }
        })

        back.setOnClickListener {
            finish()
        }

        add.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION)
            } else {
                val addIntent = Intent(this, AddQuizActivity::class.java)
                addIntent.putExtra("instructorId",instructorId)
                startActivity(addIntent)
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val addIntent = Intent(this, AddQuizActivity::class.java)
                    addIntent.putExtra("instructorId",instructorId)
                    startActivity(addIntent)
                }
                else {

                }
            }
        }
    }

}