package com.yapps.mobilodev2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.transition.Visibility
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class UpdateUserActivity : AppCompatActivity() {

    private lateinit var back: ImageView
    private lateinit var photo: ImageView
    private lateinit var name: TextInputEditText
    private lateinit var email: TextInputEditText
    private lateinit var id: TextInputEditText
    private lateinit var education: Spinner
    private lateinit var phone: TextInputEditText
    private lateinit var social: TextInputEditText
    private lateinit var update: MaterialButton
    private lateinit var changePassword: MaterialButton
    private lateinit var cvId: CardView
    private lateinit var cvEducation: CardView

    private lateinit var user:User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_user)

        back = findViewById(R.id.back)
        photo = findViewById(R.id.photo)
        name = findViewById(R.id.name)
        email = findViewById(R.id.email)
        id = findViewById(R.id.id)
        education = findViewById(R.id.sp_education)
        phone = findViewById(R.id.phone)
        social = findViewById(R.id.social)
        update = findViewById(R.id.update)
        changePassword = findViewById(R.id.changePassword)
        cvId = findViewById(R.id.cv_id)
        cvEducation = findViewById(R.id.cv_education)

        val intent = intent
        val userId:Long = intent.getLongExtra("userId",0L)
        val type:String = intent.getStringExtra("type").toString()

        if(type == "instructor"){
            cvId.visibility = View.GONE
            cvEducation.visibility = View.GONE
            photo.visibility = View.GONE
        }

        runOnUiThread {
            ThisApplication.userDao.getUserWithIdLive(userId).observe(this, Observer { user ->
                if(user != null){
                    updateScreen(user,type)
                    this.user = user
                }
            })
        }

        back.setOnClickListener {
            finish()
        }

        update.setOnClickListener {
            if(name.text.toString().split(" ").size != 2){
                Toast.makeText(this,"Please enter a valid name and surname",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if(email.text.toString().isEmpty()){
                Toast.makeText(this,"Please enter a valid email",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            user.name = name.text.toString().split(" ")[0]
            user.surname = name.text.toString().split(" ")[1]
            user.email = email.text.toString()
            user.phone = phone.text.toString()
            user.instagram = social.text.toString()
            if(type == "student"){
                if(id.text.toString().isEmpty()){
                    Toast.makeText(this,"Please enter a valid student id",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                user.studentId = id.text.toString()
                user.education = education.selectedItemPosition
            }
            CoroutineScope(Job()).launch {
                ThisApplication.userDao.update(user)
                runOnUiThread {
                    Toast.makeText(this@UpdateUserActivity,"User updated",Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        changePassword.setOnClickListener {
            val intent = Intent(this,ChangePasswordActivity::class.java)
            intent.putExtra("userId",userId)
            startActivity(intent)
        }

    }

    private fun updateScreen(user:User,type:String){
        name.setText(user.name + " " + user.surname)
        email.setText(user.email)
        social.setText (user.instagram)
        phone.setText (user.phone)
        if(type == "student"){
            id.setText (user.studentId)
            education.setSelection(user.education)
            try{
                photo.setImageURI(Uri.parse(user.imagePath))
            }catch (e:Exception){
                photo.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.baseline_account_circle_24))
                e.printStackTrace()
            }
        }

    }
}