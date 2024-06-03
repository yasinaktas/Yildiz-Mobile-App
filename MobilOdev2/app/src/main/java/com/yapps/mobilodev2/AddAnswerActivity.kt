package com.yapps.mobilodev2

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Checkable
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.yapps.mobilodev2.questionAnswerModule.answer.Answer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AddAnswerActivity : AppCompatActivity() {

    private lateinit var back:ImageView
    private lateinit var question:TextView
    private lateinit var answer:TextInputEditText
    private lateinit var correctAnswer:CheckBox
    private lateinit var add:MaterialButton
    private lateinit var lv:ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_answer)

        back = findViewById(R.id.back)
        question = findViewById(R.id.question)
        answer = findViewById(R.id.answer)
        correctAnswer = findViewById(R.id.correctAnswer)
        add = findViewById(R.id.add)
        lv = findViewById(R.id.lv)

        val intent = intent
        val questionId = intent.getLongExtra("questionId", -1)

        back.setOnClickListener {
            finish()
        }

        ThisApplication.questionDao.getQuestionById(questionId).observe(this, Observer {questions ->
            if(questions != null){
                this.question.text = questions[0].question
            }else{
                Toast.makeText(this, "No such question", Toast.LENGTH_SHORT).show()
                finish()
            }

        })

        add.setOnClickListener {
            val answerStr = answer.text.toString()
            if (answerStr.isEmpty()) {
                Toast.makeText(this, "Please enter an answer", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(questionId == -1L){
                Toast.makeText(this, "Question error", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val answerInsert = Answer(questionId = questionId, answer = answerStr, isCorrect = correctAnswer.isChecked, checked = false)
            CoroutineScope(Job()).launch {
                ThisApplication.answerDao.insert(answerInsert)
            }
        }

        ThisApplication.answerDao.getAnswersByQuestion(questionId).observe(this, Observer {answers ->
            val myStringArray = ArrayList<String>()
            for(answer in answers){
                myStringArray.add(answer.answer + if(answer.isCorrect) " (correct)" else "")
            }
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myStringArray)
            lv.setAdapter(adapter)
            lv.setOnItemLongClickListener { _, _, position, _ ->
                val selectedAnswer = answers[position]
                CoroutineScope(Job()).launch {
                    ThisApplication.answerDao.delete(selectedAnswer)
                }
                Toast.makeText(this, "Answer deleted", Toast.LENGTH_SHORT).show()
                true
            }
        })

    }
}