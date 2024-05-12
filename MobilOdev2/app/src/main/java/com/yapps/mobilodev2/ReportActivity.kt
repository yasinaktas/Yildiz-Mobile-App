package com.yapps.mobilodev2

import android.os.Bundle
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

class ReportActivity : AppCompatActivity() {

    private lateinit var scope:Spinner
    private lateinit var name:TextInputEditText
    private lateinit var recipient:Spinner
    private lateinit var subject:TextInputEditText
    private lateinit var body:TextInputEditText
    private lateinit var create:MaterialButton
    private lateinit var back:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        scope = findViewById(R.id.sp_report)
        name = findViewById(R.id.name)
        recipient = findViewById(R.id.sp_recipient)
        subject = findViewById(R.id.subject)
        body = findViewById(R.id.body)
        create = findViewById(R.id.create)
        back = findViewById(R.id.back)

        val intent = intent
        val userId = intent.getLongExtra("userId",0L)

        create.setOnClickListener {
            if(name.text.toString() == ""){
                Toast.makeText(this,"Name is required",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(subject.text.toString() == ""){
                Toast.makeText(this,"Subject is required",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(body.text.toString() == ""){
                Toast.makeText(this,"Body is required",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            ThisApplication.courseDao.getCourseById(name.text.toString()).observe(this, Observer { course ->
                if(course == null){
                    Toast.makeText(this,"No such course",Toast.LENGTH_SHORT).show()
                    return@Observer
                }
                val notified = "00000000000000000000000000000000"
                val report = Report(scope=scope.selectedItem.toString(), courseId=name.text.toString(),recipient=recipient.selectedItem.toString(),subject=subject.text.toString(),body=body.text.toString(),notified=notified,date = System.currentTimeMillis(), instructors = course.instructors + "," + course.creator)
                CoroutineScope(Job()).launch {
                    ThisApplication.reportDao.insert(report)
                    runOnUiThread {
                        Toast.makeText(this@ReportActivity,"Report created",Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            })

        }

        back.setOnClickListener {
            finish()
        }

    }
}