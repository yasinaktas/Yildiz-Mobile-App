package com.yapps.mobilodev2

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputEditText

class CourseInfoActivity : AppCompatActivity() {

    private lateinit var back:ImageView
    private lateinit var name:TextView
    private lateinit var id:TextView
    private lateinit var groups:TextView
    private lateinit var students:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_info)

        back = findViewById(R.id.back)
        name = findViewById(R.id.name)
        id = findViewById(R.id.id)
        groups = findViewById(R.id.groups)
        students = findViewById(R.id.students)

        val intent = intent
        val courseId = intent.getStringExtra("courseId").toString()

        ThisApplication.courseDao.getCourseById(courseId).observe(this, Observer {course ->
            if(course != null){
                name.text = "Course Name: " + course.courseName
                id.text = "Course Id: " + course.courseId
                groups.text = "Groups: " + course.groupNumbers.toString()
                students.text = "Students Number: " + (course.students.split(",").size-1).toString()
            }
        })

        back.setOnClickListener {
            finish()
        }

    }
}