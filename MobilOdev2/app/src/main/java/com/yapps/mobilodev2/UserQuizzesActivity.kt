package com.yapps.mobilodev2

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale

class UserQuizzesActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager

    private lateinit var back:ImageView
    private lateinit var lv:ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_quizzes)

        back = findViewById(R.id.back)
        lv = findViewById(R.id.lv)


        val intent = intent
        val userId = intent.getLongExtra("userId",-1L)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        ThisApplication.courseDao.getAllCourses().observe(this, Observer { courses ->
            if(courses != null){
                val userCourseIds = courses.filter {it.students.split(",").contains(userId.toString())}.map { it.courseId }
                ThisApplication.quizDao.getQuizzesByCourses(userCourseIds).observe(this, Observer { quizzes ->
                    if(quizzes != null){
                        val myStringArray = ArrayList<String>()
                        for(quiz in quizzes){
                            val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                            val formattedLatitude = quiz.location.split(" ")[0].substring(0,8)
                            val formattedLongitude = quiz.location.split(" ")[1].substring(0,8)
                            myStringArray.add("\nCourse: ${quiz.courseId}\nDate: ${sdf.format(Date(quiz.startTime))}\nDuration: ${quiz.time / (1000*60)} minutes\nLocation: $formattedLatitude - $formattedLongitude\n")
                        }
                        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myStringArray)
                        lv.adapter = adapter
                        lv.setOnItemClickListener { _, _, position, _ ->
                            val quizId = quizzes[position].id

                            ThisApplication.resultDao.getStudentResults(quizId = quizId, studentId = userId).observe(this,Observer{ results ->
                                if(results!=null){
                                    if(results.isEmpty()){
                                        val now = System.currentTimeMillis()
                                        if(!(now >= quizzes[position].startTime && now <= quizzes[position].startTime + quizzes[position].time)){
                                            Toast.makeText(this,"Quiz time has ended",Toast.LENGTH_SHORT).show()
                                        }else{
                                            val locationA = LocationData(quizzes[position].location.split(" ")[0].toDouble(),
                                                quizzes[position].location.split(" ")[1].toDouble())
                                            getLocation(locationA, quizId = quizId, userId = userId)
                                        }
                                    }else{
                                        Toast.makeText(this,"You have already finished this quiz!",Toast.LENGTH_SHORT).show()
                                        showResultDialog(quizId = quizId, userId = userId)
                                    }
                                }
                            })
                        }
                    }
                })
            }
        })

        back.setOnClickListener {
            finish()
        }

    }

    private fun showResultDialog(quizId:Long, userId:Long) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Quiz Result")

        ThisApplication.questionDao.getQuestions(quizId).observe(this, Observer { questions ->
            if(questions != null){
                val questionIds = questions.map { it.referenceId }
                ThisApplication.answerDao.getAnswersByQuiz(questionIds).observe(this, Observer { answers ->
                    if(answers != null){
                        var a=0
                        var b=0
                        var c=0
                        ThisApplication.resultDao.getStudentResults(quizId = quizId, studentId = userId).observe(this,
                            Observer {
                                results ->
                                if(results != null){
                                    for(question in questions){
                                        var empty = true
                                        var correct = true
                                        for(answer in answers){
                                            if(answer.questionId == question.referenceId){
                                                for(result in results){
                                                    if(result.questionId == question.referenceId && result.answerId == answer.id){
                                                        if(result.answer){
                                                            empty = false
                                                        }
                                                        if(result.answer != answer.isCorrect){
                                                           correct = false
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        if(empty){
                                            a++
                                        }else if(correct){
                                            b++
                                        }else{
                                            c++
                                        }
                                    }
                                    builder.setMessage("Correct : $b\nIncorrect: $c\nEmpty: $a")
                                    builder.setPositiveButton("Ok") { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                    builder.show()
                                }
                            })
                    }
                })
            }
        })
    }

    private fun getLocation(locationA:LocationData, quizId:Long, userId:Long) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                if (location != null) {
                    val locationX = Location("X")
                    locationX.latitude = locationA.latitude
                    locationX.longitude = locationA.longitude
                    val distance = location.distanceTo(locationX)
                    Toast.makeText(this,"Distance: $distance",Toast.LENGTH_SHORT).show()
                    if(distance >= 10){
                        Toast.makeText(this,"You are too far from the quiz location, $distance meters!",Toast.LENGTH_SHORT).show()
                    }else{
                        val intent = Intent(this, UserQuizActivity::class.java)
                        intent.putExtra("quizId", quizId)
                        intent.putExtra("userId", userId)
                        startActivity(intent)
                    }
                }else{
                    Toast.makeText(this,"Your location is not found",Toast.LENGTH_SHORT).show()
                }
            }
    }
}