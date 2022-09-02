package com.example.quiz_time

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.quiz_time.AuthPackage.LoginActivity
import com.example.quiz_time.DataClasses.Question
import com.example.quiz_time.RetroFitHelperPackage.RetroFitInstance
import com.example.quiz_time.databinding.ActivityStarterScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class StarterScreen : AppCompatActivity() {
    lateinit var binding: ActivityStarterScreenBinding
    lateinit var auth: FirebaseAuth
    private val collReference = Firebase.firestore.collection("users")
    var ques: List<Question>? = null

    // answer work

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStarterScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()


        auth = FirebaseAuth.getInstance()

        // Animation Starter
        binding.lottieAnimationView.playAnimation()
        binding.lottieAnimationView.loop(true)
        binding.startButton.setOnClickListener {
            Toast.makeText(this, "entered",Toast.LENGTH_SHORT).show()
        }


        // Profile edit activity
        binding.editProfile.setOnClickListener {
            val intent = Intent(this, ProfileEditActivity::class.java)
            startActivity(intent)
        }

        // log out init
        binding.logOut.setOnClickListener {

            // sign out just allows to move to different account, for auth-table-deletion, search account deletion
            val eml = auth?.currentUser?.email

            FirebaseAuth.getInstance().signOut()
            startActivity(
                Intent(
                    this,
                    LoginActivity::class.java
                )
            )
            finish()

        }

        // bring data from FireBase and bind
        bindUserDetails()

        // Start Quiz (Retrieving data for questions)
        binding.startButton.setOnClickListener {

            val intent = Intent(this@StarterScreen, QuizActivity::class.java)
            startActivity(intent)
        }

        // fab
        binding.fab.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            //set title for alert dialog
            builder.setTitle("How to play??")
            //set message for alert dialog
            builder.setMessage("Quiz consists of questions (Multiple Choice Type) and can have multiple correct options. All correct gives 2 score, some correct will give 1 and any incorrect option will give 0.")
            builder.setIcon(R.drawable.aboutalert)

            //performing positive action
            builder.setPositiveButton("Ok. Got It!!"){dialogInterface, which ->
                Toast.makeText(applicationContext,"Great",Toast.LENGTH_LONG).show()
            }

            // Create the AlertDialog
            val alertDialog: AlertDialog = builder.create()
            // Set other dialog properties
            alertDialog.setCancelable(false)
            alertDialog.show()
        }
    }


    private fun bindUserDetails () {
        val eml = auth?.currentUser?.email
        var str: String? = null
        var uri: String?
        var hscore: String?
        eml?.let {
            collReference.document(it).get().addOnCompleteListener {
                str = it.result.data?.get("username") as String;
                uri = it.result.data?.get("uri") as String;
                hscore = it.result.data?.get("highestScore").toString()
                binding.userName.text = str
                binding.highScore.text = "Highest Score: " +hscore

                if(uri != "NOT_SET") {
                    binding.userPhoto.setImageURI(Uri.parse(uri))
                }
            }
        }
        if(str == null) {
            binding.userName.text = "..."
        }

    }

    override fun onResume() {
        super.onResume()
        bindUserDetails()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

}

