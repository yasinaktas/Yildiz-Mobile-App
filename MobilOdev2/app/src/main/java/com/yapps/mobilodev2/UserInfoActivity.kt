package com.yapps.mobilodev2

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class UserInfoActivity : AppCompatActivity() {

    private val REQUEST_LOCATION_PERMISSION = 1

    private val REQUEST_IMAGE_PICK = 1000

    private lateinit var photo:ImageView
    private lateinit var name:TextView
    private lateinit var email:TextView
    private lateinit var id:TextView
    private lateinit var education:TextView
    private lateinit var phone:TextView
    private lateinit var social:TextView
    private lateinit var edit:MaterialButton
    private lateinit var report:MaterialButton
    private lateinit var quizzes:MaterialButton
    private lateinit var chip_group:ChipGroup
    private lateinit var chip_date:Chip
    private lateinit var chip_completed:Chip
    private lateinit var list:ListView
    private lateinit var logout:ImageView
    private lateinit var call:ImageView
    private lateinit var mail:ImageView
    private lateinit var open:ImageView

    private var courses:List<Course> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        photo = findViewById(R.id.photo)
        name = findViewById(R.id.name)
        email = findViewById(R.id.email)
        id = findViewById(R.id.id)
        education = findViewById(R.id.education)
        phone = findViewById(R.id.phone)
        social = findViewById(R.id.social)
        edit = findViewById(R.id.edit)
        report = findViewById(R.id.report)
        quizzes = findViewById(R.id.quizzes)
        chip_group = findViewById(R.id.chip_group)
        chip_date = findViewById(R.id.chip_date)
        chip_completed = findViewById(R.id.chip_completed)
        list = findViewById(R.id.list)
        logout = findViewById(R.id.logout)
        call = findViewById(R.id.call)
        mail = findViewById(R.id.mail)
        open = findViewById(R.id.open)

        val intent = intent
        val userId:Long = intent.getLongExtra("userId",0L)

        ThisApplication.userDao.getUserWithIdLive(userId).observe(this, Observer { user ->
            if(user != null){
                updateScreen(user)
            }
        })

        list.setOnItemClickListener { parent, view, position, id ->
            val courseId = courses[position].courseId
            val intent = Intent(this,CourseInfoActivity::class.java)
            intent.putExtra("courseId",courseId)
            startActivity(intent)
        }

        ThisApplication.courseDao.getAllCourses().observe(this, Observer { courses ->
            if(courses != null){
                val newCourses = courses.sortedBy { it.courseId }.filter { course -> course.students.split(",").contains(userId.toString()) }
                this.courses = newCourses
                val myStringArray = ArrayList<String>()
                for(course in newCourses){
                    myStringArray.add(course.courseId + " - " + course.date)
                }
                val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myStringArray)
                list.setAdapter(adapter)
            }
        })

        edit.setOnClickListener {
            val intent = Intent(this,UpdateUserActivity::class.java)
            intent.putExtra("userId",userId)
            intent.putExtra("type","student")
            startActivity(intent)
        }

        logout.setOnClickListener {
            ThisApplication.sharedPrefs.setRemember(false)
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        call.setOnClickListener{
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${phone.text}")
            startActivity(intent)
        }

        mail.setOnClickListener{
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:${email.text}")
            startActivity(intent)
        }

        open.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.instagram.com/${social.text}")
            startActivity(intent)
        }

        photo.setOnClickListener {
            val pickIntent = Intent(Intent.ACTION_PICK)
            pickIntent.type = "image/*"

            val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            val chooserIntent = Intent.createChooser(pickIntent, "Select Image")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(captureIntent))

            startActivityForResult(chooserIntent, REQUEST_IMAGE_PICK)
        }

        report.setOnClickListener{
            val intent = Intent(this,ReportActivity::class.java)
            intent.putExtra("userId",userId)
            startActivity(intent)
        }

        val chipGroupListener = ChipGroup.OnCheckedChangeListener { group, checkedId ->
            val chip: Chip? = findViewById(checkedId)
            if(chip == null){
                val newCourses = courses.sortedBy { it.courseId }
                this.courses = newCourses
                val myStringArray = ArrayList<String>()
                for(course in newCourses){
                    myStringArray.add(course.courseId + " - " + course.date)
                }
                val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myStringArray)
                list.setAdapter(adapter)
            }else{
                chip.let {
                    val selectedChipText = it.text
                    if(selectedChipText == "Date"){
                        val dateFormat = SimpleDateFormat("dd.MM.yyyy")
                        val newCourses = courses.sortedBy { course ->
                            dateFormat.parse(course.date)
                        }
                        val myStringArray = ArrayList<String>()
                        for(course in newCourses){
                            myStringArray.add(course.courseId + " - " + course.date)
                        }
                        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myStringArray)
                        list.setAdapter(adapter)
                    }else if(selectedChipText == "Completed"){
                        val dateFormat = SimpleDateFormat("dd.MM.yyyy")
                        val currentDate = Calendar.getInstance().time
                        val calendar = Calendar.getInstance()
                        calendar.time = currentDate
                        calendar.add(Calendar.MONTH, -4)
                        val fourMonthsAgo = calendar.time
                        val sortedList = courses.filter { customObject ->
                            val customObjectDate = dateFormat.parse(customObject.date)
                            customObjectDate.before(fourMonthsAgo)
                        }
                        val myStringArray = ArrayList<String>()
                        for(course in sortedList){
                            myStringArray.add(course.courseId + " - " + course.date)
                        }
                        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myStringArray)
                        list.setAdapter(adapter)
                    }
                }
            }
        }
        chip_group.setOnCheckedChangeListener(chipGroupListener)


        quizzes.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION)
            } else {
                val intent = Intent(this,UserQuizzesActivity::class.java)
                intent.putExtra("userId",userId)
                startActivity(intent)
            }
        }

    }

    private fun updateScreen(user:User){
        name.text = user.name + " " + user.surname
        email.text = user.email
        id.text = user.studentId
        social.text = user.instagram
        phone.text = user.phone
        education.text = resources.getStringArray(R.array.education)[user.education]
        try{
            photo.setImageURI(Uri.parse(user.imagePath))
        }catch (e:Exception){
            photo.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.baseline_account_circle_24))
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            val imageUri = data?.data
            photo.setImageURI(imageUri)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
                else {

                }
            }
        }
    }
}