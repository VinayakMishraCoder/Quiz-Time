package com.example.quiz_time

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.quiz_time.DataClasses.User
import com.example.quiz_time.databinding.ActivityResultBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class ResultActivity : AppCompatActivity() {
    lateinit var binding: ActivityResultBinding
    lateinit var auth: FirebaseAuth
    private val collReference = Firebase.firestore.collection("users")
    var currScore = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.dream.playAnimation()
        binding.dream.loop(true)
        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()
        currScore = intent.getIntExtra("totalScoreEarned", 0)
        binding.scoreText.text = currScore.toString()

        bindUserDetails()
    }

    suspend fun saveUser(user: User) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.currentUser?.email?.let { collReference.document(it).set(user) }

//                withContext(Dispatchers.Main) {
//                    Toast.makeText(this@ResultActivity, "Don;t change id", Toast.LENGTH_LONG).show()
//                }

            } catch (e: Exception) {
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(this@ResultActivity, "Failed e", Toast.LENGTH_SHORT).show()
//                }
            }
        }
    }


    private fun bindUserDetails() {
        val eml = auth?.currentUser?.email
        var str: String? = null
        var uri: String?
        eml?.let {
            collReference.document(it).get().addOnCompleteListener {
                str = it.result.data?.get("username") as String;
                uri = it.result.data?.get("uri") as String;
                if (uri != "NOT_SET") {
                }
//                Log.d("iop", str + " "+ uri + currScore.toString())
                CoroutineScope(Dispatchers.Default).launch {
                    saveUser(User(str!!, uri!!,currScore))
                }
            }
        }
        if (str == null) {
        }
        else {

        }

    }
}