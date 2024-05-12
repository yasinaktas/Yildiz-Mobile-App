package com.yapps.mobilodev2

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AdminActivity : AppCompatActivity() {

    private val CHANNEL_ID = "1234"
    private val NOTIFICATION_ID = 1
    private val REQUEST_NOTIFICATION_PERMISSION = 1

    private lateinit var logout: ImageView
    private lateinit var newCourse: MaterialButton
    private lateinit var list: ListView
    private lateinit var reports: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        logout = findViewById(R.id.logout)
        newCourse = findViewById(R.id.newCourse)
        list = findViewById(R.id.list)
        reports = findViewById(R.id.rvReports)

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
                            intentCourse.putExtra("type","admin")
                            intentCourse.putExtra("userId",0L)
                            startActivity(intentCourse)
                        }

                    }
            }
        })

        val adapterReports = ReportRecyclerAdapter()
        reports.adapter = adapterReports

        ThisApplication.reportDao.getReportsWithAdmin().observe(this,
            Observer {  reports ->
                if(reports != null){
                    adapterReports.data = reports
                    for(report in reports){
                        if(!controlNotified(report.notified)){
                            showNotification(report)
                            CoroutineScope(Job()).launch {
                                report.notified = changeNotified(report.notified)
                                ThisApplication.reportDao.update(report)
                            }
                        }
                    }
                }
            })

        newCourse.setOnClickListener {
            val intent = Intent(this,NewCourseActivity::class.java)
            intent.putExtra("userId",0L)
            intent.putExtra("type","admin")
            intent.putExtra("operation","new")
            startActivity(intent)
        }

        logout.setOnClickListener {
            ThisApplication.sharedPrefs.setRemember(false)
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }


    private fun controlNotified(notified:String):Boolean{
        return (notified[0] == '1')
    }

    private fun changeNotified(notified: String): String {
        val charArray = notified.toCharArray()
        charArray[0] = '1'
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
            if (ContextCompat.checkSelfPermission(this@AdminActivity, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@AdminActivity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_NOTIFICATION_PERMISSION)
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