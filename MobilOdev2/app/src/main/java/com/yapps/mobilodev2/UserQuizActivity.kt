package com.yapps.mobilodev2

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.yapps.mobilodev2.questionAnswerModule.answer.Answer
import com.yapps.mobilodev2.questionAnswerModule.answer.AnswerRecyclerAdapter
import com.yapps.mobilodev2.questionAnswerModule.question.Question
import com.yapps.mobilodev2.questionAnswerModule.question.QuestionRecyclerAdapter
import com.yapps.mobilodev2.questionAnswerModule.result.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Locale

class UserQuizActivity : AppCompatActivity() {

    private lateinit var back:ImageView
    private lateinit var start:TextView
    private lateinit var end:TextView
    private lateinit var total:TextView
    private lateinit var questionCount:TextView
    private lateinit var question:TextView
    private lateinit var rvAnswers:RecyclerView
    private lateinit var previous:MaterialButton
    private lateinit var next:MaterialButton
    private lateinit var finish:MaterialButton

    private lateinit var answerAdapter: AnswerRecyclerAdapter
    private val userQuestions = mutableListOf<Question>()
    private val userAnswers = mutableListOf<Answer>()

    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_quiz)

        back = findViewById(R.id.back)
        start = findViewById(R.id.start)
        end = findViewById(R.id.end)
        total = findViewById(R.id.total)
        questionCount = findViewById(R.id.questionCount)
        question = findViewById(R.id.question)
        rvAnswers = findViewById(R.id.rvAnswers)
        previous = findViewById(R.id.previous)
        next = findViewById(R.id.next)
        finish = findViewById(R.id.finish)


        val intent = intent
        val quizId = intent.getLongExtra("quizId",-2)
        val userId = intent.getLongExtra("userId",-2)

        answerAdapter = AnswerRecyclerAdapter(object : AnswerRecyclerAdapter.OnItemClickListener{
            override fun onItemClick(item: Answer,isChecked:Boolean) {
                for(answer in userAnswers){
                    if(answer.id == item.id){
                        answer.checked = isChecked
                    }
                }
            }

        })
        rvAnswers.adapter = answerAdapter

        ThisApplication.quizDao.getQuizById(quizId).observe(this, Observer { quizzes ->
            if(quizzes != null){
                val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                start.text = sdf.format(quizzes[0].startTime)
                end.text = sdf.format(quizzes[0].startTime + quizzes[0].time)
                total.text = (quizzes[0].time / (1000*60)).toString()
            }
        })

        ThisApplication.questionDao.getQuestions(quizId).observe(this, Observer { questions ->
            if(questions != null && questions.isNotEmpty()){
                userQuestions.clear()
                userQuestions.addAll(questions)
                userQuestions.shuffle()
                questionCount.text = "${currentIndex+1} / ${userQuestions.size}"
                question.text = userQuestions[currentIndex].question

                val questionIds = userQuestions.map { it.referenceId }
                ThisApplication.answerDao.getAnswersByQuiz(questionIds).observe(this, Observer { answers ->
                    if(answers != null){
                        userAnswers.clear()
                        userAnswers.addAll(answers)
                        userAnswers.shuffle()
                        val currentAnswers = userAnswers.filter { it.questionId == userQuestions[currentIndex].referenceId }
                        answerAdapter.data = currentAnswers
                    }
                })
            }
        })


        previous.setOnClickListener {
            if(currentIndex - 1 >= 0){
                currentIndex--
                questionCount.text = "${currentIndex+1} / ${userQuestions.size}"
                question.text = userQuestions[currentIndex].question
                val currentAnswers = userAnswers.filter { it.questionId == userQuestions[currentIndex].referenceId }
                answerAdapter.data = currentAnswers
            }
        }

        next.setOnClickListener {
            if(currentIndex + 1 <= userQuestions.size-1){
                currentIndex++
                questionCount.text = "${currentIndex+1} / ${userQuestions.size}"
                question.text = userQuestions[currentIndex].question
                val currentAnswers = userAnswers.filter { it.questionId == userQuestions[currentIndex].referenceId }
                answerAdapter.data = currentAnswers
            }
        }

        finish.setOnClickListener {
            CoroutineScope(Job()).launch {
                for(answer in userAnswers){
                    val result = Result(studentId = userId, quizId = quizId, questionId = answer.questionId, answerId = answer.id, answer = answer.checked)
                    ThisApplication.resultDao.insert(result)
                }
                runOnUiThread {
                    Toast.makeText(this@UserQuizActivity, "Quiz Completed", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }


        back.setOnClickListener {
            finish()
        }

    }
}
