package com.yapps.mobilodev2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CourseActivity : AppCompatActivity() {

    private lateinit var name: TextView
    private lateinit var id: TextView
    private lateinit var description: TextView
    private lateinit var delete:MaterialButton
    private lateinit var edit:MaterialButton
    private lateinit var rvInstructors:RecyclerView
    private lateinit var rvStudents:RecyclerView
    private lateinit var back:ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course)

        name = findViewById(R.id.name)
        id = findViewById(R.id.id)
        description = findViewById(R.id.description)
        delete = findViewById(R.id.delete)
        edit = findViewById(R.id.edit)
        rvInstructors = findViewById(R.id.rvInstructors)
        rvStudents = findViewById(R.id.rvStudents)
        back = findViewById(R.id.back)

        val intent = intent
        val userId:Long = intent.getLongExtra("userId",0L)
        val course:Course = intent.getParcelableExtra<Course>("course")!!
        val type:String = intent.getStringExtra("type").toString()

        name.text = "Name: ${course.courseName}"
        id.text = "Course ID: ${course.courseId}"
        description.text = "Course Description: ${course.description}"

        if(!((userExist(course.instructors,userId.toString()) || type == "admin" || course.creator == userId.toString()))){
            delete.isEnabled = false
            edit.isEnabled = false
        }

        val adapterStudents = UserRecyclerAdapter(editor = (userExist(course.instructors,userId.toString()) || type == "admin" || course.creator == userId.toString()),users = course.students.split(",")){ user, result ->
            if(result){
                course.students = addUser(course.students,user.id.toString())
            }else{
                course.students = deleteUser(course.students,user.id.toString())
            }
            CoroutineScope(Job()).launch {
                ThisApplication.courseDao.update(course)
            }
        }
        rvStudents.adapter = adapterStudents

        val adapterInstructors = UserRecyclerAdapter(editor = (userExist(course.instructors,userId.toString()) || type == "admin" || course.creator == userId.toString()), users = course.instructors.split(",")){ user, result ->
            if(result){
                course.instructors = addUser(course.instructors,user.id.toString())
            }else{
                course.instructors = deleteUser(course.instructors,user.id.toString())
            }
            CoroutineScope(Job()).launch {
                ThisApplication.courseDao.update(course)
            }
        }
        rvInstructors.adapter = adapterInstructors


        ThisApplication.userDao.getUserWithType("student").observe(this, Observer { users ->
            if(users != null){
                if((userExist(course.instructors,userId.toString()) || type == "admin" || course.creator == userId.toString())){
                    adapterStudents.data = users
                }else{
                    val list:MutableList<User> = mutableListOf()
                    val usersStr = course.students.split(",")
                    for(user in users){
                        for(str in usersStr){
                            if(str != ""){
                                if(user.id == str.toLong()){
                                    list.add(user)
                                }
                            }
                        }
                    }
                    adapterStudents.data = list
                }

            }
        })

        ThisApplication.userDao.getUserWithType("instructor").observe(this, Observer { users ->
            if(users != null){
                if((userExist(course.instructors,userId.toString()) || type == "admin" || course.creator == userId.toString())){
                    adapterInstructors.data = users
                }else{
                    val list:MutableList<User> = mutableListOf()
                    val usersStr = course.instructors.split(",")
                    for(user in users){
                        for(str in usersStr){
                            if(str != ""){
                                if(user.id == str.toLong()){
                                    list.add(user)
                                }
                            }
                        }
                    }
                    adapterInstructors.data = list
                }
            }

        })

        delete.setOnClickListener{
            CoroutineScope(Job()).launch {
                ThisApplication.courseDao.delete(course)
                finish()
            }
        }

        edit.setOnClickListener{
            val intent = Intent(this,NewCourseActivity::class.java)
            intent.putExtra("userId",userId)
            intent.putExtra("type","instructor")
            intent.putExtra("operation","edit")
            intent.putExtra("courseId",course.courseId)
            startActivity(intent)
        }

        back.setOnClickListener{
            finish()
        }

    }

    private fun deleteUser(str:String,userId:String):String{
        val users = str.split(",")
        var result = ""
        for(i in users.indices){
            if(users[i] != userId){
                result += users[i]
                if(i != users.size-2){
                    result += ","
                }
            }
        }
        return result
    }

    private fun addUser(str:String,userId:String):String{
        return "$str,$userId"
    }

    private fun userExist(str:String,userId:String):Boolean{
        val users = str.split(",")
        for(i in users.indices){
            if(users[i] == userId){
                return true
            }
        }
        return false
    }

}