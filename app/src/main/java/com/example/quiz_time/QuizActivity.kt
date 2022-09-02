package com.example.quiz_time

import android.content.Intent
import android.graphics.Color
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.quiz_time.DataClasses.Question
import com.example.quiz_time.RetroFitHelperPackage.RetroFitInstance
import com.example.quiz_time.databinding.ActivityQuizBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import java.lang.reflect.Array

class QuizActivity : AppCompatActivity() {
    lateinit var binding: ActivityQuizBinding

    /* Question Index */
    var currQuestion: Int = 0

    /* Other Vars */
    var ques: List<Question>? = null
    var timeLimit: Long = 1
    var markedOptionsSize: Int = 0
    lateinit var markedOptions: ArrayList<IntArray>
    lateinit var timer: CountDownTimer
    var colorMarked = Color.parseColor("#64DD17")
    var colorUnMarked = Color.parseColor("#283593")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()


        // animations
        binding.newLottie.playAnimation()
        binding.newLottie.loop(true)

        // Prev and Next
        binding.nextButton.setOnClickListener {
            nextQuestion()
        }
        binding.prevButton.setOnClickListener {
            prevQuestion()
        }

        lifecycleScope.launchWhenCreated {
            var resultBody = RetroFitInstance().apiInstance.getTodos().body()?.result
            ques = resultBody?.questions // get Questions from @RetrofitGetApi
            timeLimit = ((resultBody?.timeInMinutes!!.toLong())*(60*1000))
            markedOptionsSize = ques!!.size
            withContext(Dispatchers.Main) {
                startQuiz()
            }
        }


    }

    private fun startQuiz() {
        markedOptions = ArrayList()
        for(i in 0..markedOptionsSize-1) {
            markedOptions.add(IntArray(4))
        }
        for(i in 0..markedOptionsSize-1) {
            for(j in 0..3) {
                markedOptions[i][j] = 0
            }
        }

        timer = object: CountDownTimer(timeLimit, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                var secs = (millisUntilFinished/1000);
                var minsLeft = (secs/60)
                var secLeft = (secs%60)
                binding.timerView.text = (minsLeft.toString()) + ": " + (secLeft.toString())
            }
            override fun onFinish() {
                submitQuiz()
            }
        }

        /* start the timer */
        timer.start()

        /* mark all views visible */
        binding.linearLayout3.visibility = View.VISIBLE
        binding.prevButton.visibility = View.VISIBLE
        binding.nextButton.visibility = View.VISIBLE
        binding.quesCard.visibility = View.VISIBLE

        /* Set OnClickListeners on the Options */
        binding.option1.setOnClickListener {
            var flp = flip(markedOptions[currQuestion][0])
            markedOptions[currQuestion][0] = flp
            if(flp == 0) {
                binding.option1text.setTextColor(colorUnMarked)
            }
            else {
                binding.option1text.setTextColor(colorMarked)
            }
        }

        binding.option2.setOnClickListener {
            var flp = flip(markedOptions[currQuestion][1])
            markedOptions[currQuestion][1] = flp
            if(flp == 0) {
                binding.option2text.setTextColor(colorUnMarked)
            }
            else {
                binding.option2text.setTextColor(colorMarked)
            }
        }

        binding.option3.setOnClickListener {
            var flp = flip(markedOptions[currQuestion][2])
            markedOptions[currQuestion][2] = flp
            if(flp == 0) {
                binding.option3text.setTextColor(colorUnMarked)
            }
            else {
                binding.option3text.setTextColor(colorMarked)
            }
        }

        binding.option4.setOnClickListener {
            var flp = flip(markedOptions[currQuestion][3])
            markedOptions[currQuestion][3] = flp
            if(flp == 0) {
                binding.option4text.setTextColor(colorUnMarked)
            }
            else {
                binding.option4text.setTextColor(colorMarked)
            }
        }

        generateIndexedViews()
    }

    private fun nextQuestion() {
        if(currQuestion == markedOptionsSize-1){
            submitQuiz()
            return
        }
        currQuestion++
        generateIndexedViews()
    }

    private fun submitQuiz() {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("totalScoreEarned", calculateScoreForAllQuestion())
        startActivity(intent)
        timer.cancel()
        finish()
    }

    private fun prevQuestion() {
        if(currQuestion == 0) {
            Toast.makeText(this, "First Question!!",Toast.LENGTH_SHORT).show()
            return
        }
        currQuestion--
        generateIndexedViews()
    }

    private fun generateIndexedViews() {
        if(currQuestion == 0){
            binding.prevButton.visibility = View.GONE
        }
        else {
            binding.prevButton.visibility = View.VISIBLE
        }
        if(currQuestion != markedOptionsSize-1){
            binding.nextText.text = "NEXT"
        }
        else if(currQuestion == markedOptionsSize-1){
            binding.nextText.text = "SUBMIT"
        }


        binding.quesText.text = ques?.get(currQuestion)?.lable
        binding.option1text.text = ques?.get(currQuestion)?.options?.get(0)?.lable
        binding.option2text.text = ques?.get(currQuestion)?.options?.get(1)?.lable
        binding.option3text.text = ques?.get(currQuestion)?.options?.get(2)?.lable
        binding.option4text.text = ques?.get(currQuestion)?.options?.get(3)?.lable

        /* Initialize colors on options */
        for(i in 0..3) {
            var temp =  markedOptions[currQuestion][i]
            if(temp == 1) {
                if(i==0) {
                    binding.option1text.setTextColor(colorMarked)
                }
                else if(i==1) {
                    binding.option2text.setTextColor(colorMarked)
                }
                else if(i==2) {
                    binding.option3text.setTextColor(colorMarked)
                }
                else if(i==3) {
                    binding.option4text.setTextColor(colorMarked)
                }
            }
            else {
                if(i==0) {
                    binding.option1text.setTextColor(colorUnMarked)
                }
                else if(i==1) {
                    binding.option2text.setTextColor(colorUnMarked)
                }
                else if(i==2) {
                    binding.option3text.setTextColor(colorUnMarked)
                }
                else if(i==3) {
                    binding.option4text.setTextColor(colorUnMarked)
                }
            }
        }

    }

    private fun flip(bool: Int): Int {
        var flipped = 0;
        if(bool == 0) flipped=1
        else flipped = 0
        return flipped
    }

    private fun calculateScoreForAllQuestion() : Int{
        var totalScore = 0
        for(i in 0..markedOptionsSize-1) {

            var lst = ques?.get(i)!!.correct_answers
            var totalRightAnswers = lst.size
            var deservesHalf = 0
            var numOfAnswersMarked = 0

            // Number of answers marked
            for(x in markedOptions[i]) {
                if(x == 1) {
                    numOfAnswersMarked++
                }
            }

            // check if user has marked one of the correct answer so it deserves half score
            for(x in lst) {
                if(markedOptions[i][x-1] == 1) {
                    deservesHalf = 1
                }
            }

            // check if user has marked any incorrect answer so user will not get even half marks
            for(j in 0..3) {
                if(markedOptions[i][j] == 1) {
                    var cancel = 1
                    for(x in lst) {
                        if(x-1 == j) {
                            cancel = 0;
                            break
                        }
                    }
                    if(cancel == 1) {
                        deservesHalf = 0
                    }
                }


            }

            if(deservesHalf == 1) {
                if(numOfAnswersMarked == totalRightAnswers) totalScore += 2;
                else totalScore += 1
            }
        }
        return totalScore
    }

    override fun onBackPressed() {
        super.onBackPressed()
        timer.cancel()
        finish()
    }
}