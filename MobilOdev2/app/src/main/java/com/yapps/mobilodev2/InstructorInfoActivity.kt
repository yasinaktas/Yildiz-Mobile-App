package com.yapps.mobilodev2

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class InstructorInfoActivity : AppCompatActivity() {

    private val CHANNEL_ID = "1234"
    private val NOTIFICATION_ID = 1
    private val REQUEST_NOTIFICATION_PERMISSION = 1

    private lateinit var name: TextView
    private lateinit var email: TextView
    private lateinit var phone: TextView
    private lateinit var social: TextView
    private lateinit var edit: MaterialButton
    private lateinit var newCourse: MaterialButton
    private lateinit var list: ListView
    private lateinit var logout: ImageView
    private lateinit var reports: RecyclerView
    private lateinit var call:ImageView
    private lateinit var mail:ImageView
    private lateinit var open:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instructor_info)

        name = findViewById(R.id.name)
        email = findViewById(R.id.email)
        phone = findViewById(R.id.phone)
        social = findViewById(R.id.social)
        edit = findViewById(R.id.edit)
        newCourse = findViewById(R.id.newCourse)
        list = findViewById(R.id.list)
        logout = findViewById(R.id.logout)
        reports = findViewById(R.id.rvReports)
        call = findViewById(R.id.call)
        mail = findViewById(R.id.mail)
        open = findViewById(R.id.open)

        createNotificationChannel()

        val intent = intent
        val userId:Long = intent.getLongExtra("userId",0L)

        ThisApplication.courseDao.getAllCourses().observe(this, Observer { courses ->
            if(courses != null){
                val myStringArray = ArrayList<String>()
                for(course in courses){
                    myStringArray.add(course.courseId)
                }
                val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myStringArray)
                list.setAdapter(adapter)
                list.onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, position, _ ->
                        val intentCourse = Intent(this,CourseActivity::class.java)
                        CoroutineScope(Job()).launch {
                            intentCourse.putExtra("course",ThisApplication.courseDao.getCourseByIdSuspend(myStringArray[position]))
                            intentCourse.putExtra("type","instructor")
                            intentCourse.putExtra("userId",userId)
                            startActivity(intentCourse)
                        }

                    }
            }
        })

        ThisApplication.userDao.getUserWithIdLive(userId).observe(this, Observer { user ->
            if(user != null){
                updateScreen(user)
            }
        })


        val adapterReports = ReportRecyclerAdapter()
        reports.adapter = adapterReports

        ThisApplication.reportDao.getReportsWithInstructorId(userId.toString()).observe(this,
            Observer {  reports ->
                if(reports != null){
                    adapterReports.data = reports
                    for(report in reports){
                        val index = controlNotified(report.instructors,userId.toString(),report.notified)

                        if(index != -1){
                            showNotification(report)
                            CoroutineScope(Job()).launch {
                                report.notified = changeNotified(report.notified,index)
                                ThisApplication.reportDao.update(report)
                            }
                        }
                    }
                }
            })

        edit.setOnClickListener {
            val intent = Intent(this,UpdateUserActivity::class.java)
            intent.putExtra("userId",userId)
            intent.putExtra("type","instructor")
            startActivity(intent)
        }

        newCourse.setOnClickListener {
            val intent = Intent(this,NewCourseActivity::class.java)
            intent.putExtra("userId",userId)
            intent.putExtra("type","instructor")
            intent.putExtra("operation","new")
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

    }

    private fun updateScreen(user:User){
        name.text = user.name + " " + user.surname
        email.text = user.email
        social.text = user.instagram
        phone.text = user.phone
    }

    private fun controlNotified(instructors:String,id:String,notified:String):Int{
        var indexInstructor = -1;
        for((index, instructor) in instructors.split(",").withIndex()){
            if(instructor == id){
                indexInstructor = index
            }
        }
        if(indexInstructor == -1){
            return -1
        }
        if(notified[indexInstructor + 1] == '1'){
            return -1
        }
        return indexInstructor
    }

    private fun changeNotified(notified: String, index:Int): String {
        if(index > notified.length || index < 0){
            return notified
        }
        val charArray = notified.toCharArray()
        charArray[index + 1] = '1'
        return String(charArray)
    }

    private fun showNotification(report:Report){

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(report.subject)
            .setContentText(report.body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            if (ContextCompat.checkSelfPermission(this@InstructorInfoActivity, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@InstructorInfoActivity, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), REQUEST_NOTIFICATION_PERMISSION)
            }else{
                notify(NOTIFICATION_ID, builder.build())
            }

        }
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "YTU MOBIL"
            val descriptionText = "Reports Channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_NOTIFICATION_PERMISSION -> {
                // Kullanıcı kameraya erişim izni isteğine yanıt verdiğinde bu blok çalışır
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Kullanıcı izni kabul etti, kameraya erişim sağlandı
                    // Burada kamera işlemlerini yapabilirsiniz
                } else {
                    // Kullanıcı izni reddetti veya iptal etti, uygun bir geri bildirim sağlayabilirsiniz
                }
            }
            // Diğer izinler için ek bloklar ekleyebilirsiniz
        }
    }
}