package com.example.quiz_time

import android.app.usage.ExternalStorageStats
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.quiz_time.AuthPackage.RegisterActivity
import com.example.quiz_time.DataClasses.User
import com.example.quiz_time.databinding.ActivityProfileEditBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.*
import java.lang.Exception

class ProfileEditActivity : AppCompatActivity() {

    companion object { val IMAGE_REQUEST_CODE = 1_000; }

    private lateinit var binding: ActivityProfileEditBinding
    lateinit var auth: FirebaseAuth
    var currUri: String? = null
    private val collReference = Firebase.firestore.collection("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // onSave
        binding.btnSave.setOnClickListener {
            val newUserName = binding.inputUsername.text.toString().trim()
            if(!newUserName.isEmpty()) {
                var tempUriString = "NOT_SET"
                if(currUri != null) tempUriString= currUri as String
                val currUser = User(newUserName,tempUriString,0)
                saveUser(currUser)
            }
        }

        // photo selection activity
        binding.selectPhoto.setOnClickListener {
            bringPhoto()
        }
    }

    private fun saveUser(user: User) = CoroutineScope(Dispatchers.IO).launch {
        try {
            auth.currentUser?.email?.let { collReference.document(it).set(user) }

            withContext(Dispatchers.Main){
                Toast.makeText(this@ProfileEditActivity, "Reset Applied",Toast.LENGTH_LONG).show()
            }

        }
        catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@ProfileEditActivity, "Some Error Occurred",Toast.LENGTH_SHORT).show()
            }
        }
    }


    /* Photo Initialisation */
    fun bringPhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            currUri = data?.data.toString()
            binding.profileImage.setImageURI(Uri.parse(currUri))
        }
    }

}