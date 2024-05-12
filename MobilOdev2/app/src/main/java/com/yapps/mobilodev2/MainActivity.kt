package com.yapps.mobilodev2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(){

    private lateinit var etUsername:TextInputEditText
    private lateinit var etPassword:TextInputEditText
    private lateinit var checkRemember:CheckBox
    private lateinit var buttonLogin:MaterialButton
    private lateinit var buttonRegister:TextView

    private lateinit var viewModel: MainViewModel
    private lateinit var factory:MainViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        factory = MainViewModelFactory(applicationContext)
        viewModel = ViewModelProvider(this,factory)[MainViewModel::class.java]

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        checkRemember = findViewById(R.id.checkRemember)
        buttonLogin = findViewById(R.id.button)
        buttonRegister = findViewById(R.id.txtRegister)



        viewModel.loginMessage.observe(this, Observer { message ->
            if(message != null){
                Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.forwardAdminActivity.observe(this, Observer { result ->
            if(result != null){
                val intent = Intent(this,AdminActivity::class.java)
                startActivity(intent)
            }
        })

        /*viewModel.forwardUserActivity.observe(this, Observer { userId ->
            if(userId != null){
                val intent = Intent(this,UserActivity::class.java)
                intent.putExtra("userId",userId)
                startActivity(intent)
                finish()
            }
        })*/

        viewModel.forwardRegisterActivity.observe(this, Observer { result ->
            if(result != null){
                val intent = Intent(this,RegisterActivity::class.java)
                startActivity(intent)
            }
        })

        viewModel.forwardStudentActivity.observe(this, Observer { userId ->
            if(userId != null){
                val intent = Intent(this,UserInfoActivity::class.java)
                intent.putExtra("userId",userId)
                startActivity(intent)
                finish()
            }
        })

        viewModel.forwardInstructorActivity.observe(this, Observer { userId ->
            if(userId != null){
                val intent = Intent(this,InstructorInfoActivity::class.java)
                intent.putExtra("userId",userId)
                startActivity(intent)
                finish()
            }
        })

        viewModel.user.observe(this, Observer { user ->
            if(user != null){
                val check = checkRemember.isChecked
                viewModel.forwardUserActivity(user.id,check,controlStudent(user.email))
            }else{
                viewModel.forwardUserError()
            }
        })

        buttonLogin.setOnClickListener{
            val email = etUsername.text.toString()
            val password = etPassword.text.toString()
            if(email == "admin" && password == "admin"){
                val check = checkRemember.isChecked
                viewModel.saveUser(userId = 0L,type = "admin")
                viewModel.forwardAdminActivity()
            }else{
                viewModel.login(email,password).observe(this, Observer{ user ->
                    if(user != null){
                        val check = checkRemember.isChecked
                        viewModel.saveUser(userId = user.id,type = user.type)
                        viewModel.forwardUserActivity(user.id,check,controlStudent(user.email))
                    }else{
                        viewModel.forwardUserError()
                    }
                })
            }

        }

        buttonRegister.setOnClickListener{
            viewModel.forwardRegisterActivity()
        }

    }

    private fun controlStudent(email:String):Boolean{
        return email.contains("std")
    }

}