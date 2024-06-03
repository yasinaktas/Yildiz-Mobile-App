package com.yapps.mobilodev2

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.location.Location
import android.location.LocationManager
import android.net.ParseException
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.yapps.mobilodev2.questionAnswerModule.question.Question
import com.yapps.mobilodev2.questionAnswerModule.question.QuestionRecyclerAdapter
import com.yapps.mobilodev2.questionAnswerModule.quiz.Quiz
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale


class AddQuizActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager

    private lateinit var back:ImageView
    private lateinit var question:TextInputEditText
    private lateinit var addQuestion:MaterialButton
    private lateinit var rvQuestions:RecyclerView
    private lateinit var courseId:TextInputEditText
    private lateinit var location:TextView
    private lateinit var updateLocation:ImageView
    private lateinit var date:TextInputEditText
    private lateinit var time:TextInputEditText
    private lateinit var add:MaterialButton

    private var lastLocation:LocationData? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_quiz)

        back = findViewById(R.id.back)
        question = findViewById(R.id.question)
        addQuestion = findViewById(R.id.addQuestion)
        rvQuestions = findViewById(R.id.rvQuestions)
        courseId = findViewById(R.id.courseId)
        location = findViewById(R.id.location)
        updateLocation = findViewById(R.id.updateLocation)
        date = findViewById(R.id.date)
        time = findViewById(R.id.time)
        add = findViewById(R.id.add)

        val intent = intent
        val instructorId = intent.getLongExtra("instructorId", -1L)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val questionAdapter = QuestionRecyclerAdapter(object : QuestionRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(item: Question) {
                val answerIntent = Intent(this@AddQuizActivity, AddAnswerActivity::class.java)
                answerIntent.putExtra("questionId", item.id)
                startActivity(answerIntent)
            }
        })
        rvQuestions.adapter = questionAdapter



        ThisApplication.questionDao.getQuestions(-1).observe(this, Observer { questions ->
            if(questions != null){
                questionAdapter.data = questions
                questionAdapter.dataCheckBox.clear()
                questionAdapter.dataCheckBox.addAll(questions.map { false })
            }
        })

        getLocation()
        if (lastLocation != null) {
            val latitude = lastLocation!!.latitude
            val longitude = lastLocation!!.longitude
            val formattedLatitude = String.format("%.5f", latitude)
            val formattedLongitude = String.format("%.5f", longitude)
            location.text = "$formattedLatitude - $formattedLongitude"
        }

        back.setOnClickListener {
            finish()
        }

        addQuestion.setOnClickListener {
            val questionText = question.text.toString()
            if(questionText.isEmpty()){
                Toast.makeText(this, "Please enter a question", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val question = Question(question = questionText, quizId = -1, referenceId = -1)
            CoroutineScope(Job()).launch {
                ThisApplication.questionDao.insert(question)
                runOnUiThread {
                    Toast.makeText(this@AddQuizActivity, "Question added successfully", Toast.LENGTH_SHORT).show()
                }
            }
        }

        add.setOnClickListener {
            val dateTimeString: String = date.getText().toString()
            val timeString: String = time.getText().toString()
            val sdf: SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            var ms:Long = 0
            var time:Long = 0
            try {
                val date: Date? = sdf.parse(dateTimeString)
                if (date != null) {
                    ms = date.time
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Please enter a valid date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try{
                time = timeString.toLong()
                if(time <= 0 || time >= 180){
                    Toast.makeText(this, "Please enter a valid time", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                time *= 1000*60
            }catch (e:Exception){
                Toast.makeText(this, "Please enter a valid time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            getLocation()
            var locationStr = ""
            if (lastLocation != null) {
                val latitude = lastLocation!!.latitude
                val longitude = lastLocation!!.longitude
                locationStr = "$latitude $longitude"
            }else{
                Toast.makeText(this, "Please update location", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val courseId = courseId.text.toString()
            CoroutineScope(Job()).launch {
                val courseExist:Int = ThisApplication.courseDao.isCourseExist(courseId)
                if(courseExist <= 0){
                    runOnUiThread {
                        Toast.makeText(this@AddQuizActivity, "Please enter a valid course id", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    val quiz = Quiz(instructorId = instructorId, courseId = courseId, startTime = ms,time = time, location = locationStr)
                    val quizId = ThisApplication.quizDao.insert(quiz)
                    val selectedQuestions = questionAdapter.data.filterIndexed { index, _ -> questionAdapter.dataCheckBox[index] }.map { question ->
                        Question(referenceId = question.id, quizId = quizId, question = question.question)
                    }

                    ThisApplication.questionDao.insertAll(selectedQuestions)

                    runOnUiThread {
                        Toast.makeText(this@AddQuizActivity, "Quiz added successfully +${selectedQuestions.size} ", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }

        updateLocation.setOnClickListener {
            getLocation()
            if (lastLocation != null) {
                val latitude = lastLocation!!.latitude
                val longitude = lastLocation!!.longitude
                val formattedLatitude = String.format("%.5f", latitude)
                val formattedLongitude = String.format("%.5f", longitude)
                location.text = "$formattedLatitude - $formattedLongitude"
                Toast.makeText(this, "Location updated successfully", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun getLocation() {
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
                lastLocation = if (location != null) {
                    LocationData(location.latitude, location.longitude)
                }else{
                    null
                }
            }
    }


}

data class LocationData(
    val latitude: Double,
    val longitude: Double
)