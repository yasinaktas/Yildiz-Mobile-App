package com.yapps.mobilodev2

import android.os.Build
import android.os.Bundle
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

class NewCourseActivity : AppCompatActivity() {

    private lateinit var id:TextInputEditText
    private lateinit var name:TextInputEditText
    private lateinit var numbers:TextInputEditText
    private lateinit var dp:DatePicker
    private lateinit var create:MaterialButton
    private lateinit var back:ImageView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_course)

        id = findViewById(R.id.id)
        name = findViewById(R.id.name)
        numbers = findViewById(R.id.numbers)
        create = findViewById(R.id.create)
        dp = findViewById(R.id.dp)
        back = findViewById(R.id.back)

        val intent = intent
        val userId:Long = intent.getLongExtra("userId",0L)
        val type:String = intent.getStringExtra("type").toString()
        val operation:String = intent.getStringExtra("operation").toString()
        var editCourseId = ""
        var course:Course
        if(operation == "edit"){
            create.text = "Update"
            editCourseId = intent.getStringExtra("courseId").toString()
            CoroutineScope(Job()).launch {
                course = ThisApplication.courseDao.getCourseByIdSuspend(editCourseId)
                runOnUiThread {
                    id.setText(course.courseId)
                    name.setText(course.courseName)
                    numbers.setText(course.groupNumbers.toString())
                    val dateStr = course.date
                    val date:Date = SimpleDateFormat("dd.MM.yyyy").parse(dateStr)
                    dp.updateDate(date.year,date.month,date.date)


                    create.setOnClickListener {
                        try{
                            val id = id.text.toString()
                            val name = name.text.toString()
                            val numbers = Integer.parseInt(numbers.text.toString())

                            if(id == "" || name == "" || numbers <= 0){
                                Snackbar.make(it,"Please fill all the fields correctly!",Snackbar.LENGTH_SHORT).show()
                                return@setOnClickListener
                            }

                            val date:String = dp.dayOfMonth.toString() + "." + (dp.month+1).toString() + "." + dp.year.toString()


                            course.courseId = id
                            course.courseName = name
                            course.date = date
                            course.groupNumbers = numbers

                            CoroutineScope(Job()).launch {
                                ThisApplication.courseDao.update(course)
                                runOnUiThread {
                                    Toast.makeText(this@NewCourseActivity,"Course updated!",Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                            }

                        }catch (e:Exception){
                            Snackbar.make(it,"Please fill all the fields",Snackbar.LENGTH_SHORT).show()
                        }

                    }
                }

            }
        }else{
            create.setOnClickListener {
                try{
                    val id = id.text.toString()
                    val name = name.text.toString()
                    val numbers = Integer.parseInt(numbers.text.toString())

                    if(id == "" || name == "" || numbers <= 0){
                        Snackbar.make(it,"Please fill all the fields correctly!",Snackbar.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    val date:String = dp.dayOfMonth.toString() + "." + (dp.month+1).toString() + "." + dp.year.toString()

                    ThisApplication.courseDao.getCourse(id,name).observe(this, Observer { course ->
                        if(course != null){
                            Snackbar.make(it,"Course exists!",Snackbar.LENGTH_SHORT).show()
                        }else{
                            val creator = if(type == "admin"){
                                "admin"
                            }else{
                                userId.toString()
                            }

                            val newCourse = Course(courseName=name, courseId = id,date = date, groupNumbers = numbers, instructors = "", students = "", description = "",creator = creator)
                            CoroutineScope(Job()).launch {
                                ThisApplication.courseDao.insert(newCourse)
                                runOnUiThread {
                                    Toast.makeText(this@NewCourseActivity,"Course created!",Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                            }

                        }
                    })

                }catch (e:Exception){
                    Snackbar.make(it,"Please fill all the fields",Snackbar.LENGTH_SHORT).show()
                }

            }
        }

        back.setOnClickListener{
            finish()
        }

    }
}