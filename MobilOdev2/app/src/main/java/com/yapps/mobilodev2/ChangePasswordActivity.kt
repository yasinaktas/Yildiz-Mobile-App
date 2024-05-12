package com.yapps.mobilodev2

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.w3c.dom.Text

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var change:MaterialButton
    private lateinit var password:TextInputEditText
    private lateinit var newPassword:TextInputEditText
    private lateinit var back:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        change=findViewById(R.id.change)
        password=findViewById(R.id.password)
        newPassword=findViewById(R.id.newPassword)
        back=findViewById(R.id.back)

        val intent = intent
        val userId = intent.getLongExtra("userId",0L)

        change.setOnClickListener{
            ThisApplication.userDao.getUserWithIdLive(userId).observe(this, Observer {  user ->
                if(user!=null){
                    if(user.password!=password.text.toString()){
                        Toast.makeText(this,"Wrong Password",Toast.LENGTH_SHORT).show()
                    }else{
                        if(newPassword.text.toString() == ""){
                            Toast.makeText(this,"Please enter new password",Toast.LENGTH_SHORT).show()
                        }else{
                            user.password = newPassword.text.toString()
                            CoroutineScope(Job()).launch {
                                ThisApplication.userDao.update(user)
                                runOnUiThread {
                                    Toast.makeText(this@ChangePasswordActivity,"Password Changed",Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                            }

                        }
                    }
                }else{
                    Toast.makeText(this,"User not found",Toast.LENGTH_SHORT).show()
                }
            })
        }

        back.setOnClickListener {
            finish()
        }

    }
}