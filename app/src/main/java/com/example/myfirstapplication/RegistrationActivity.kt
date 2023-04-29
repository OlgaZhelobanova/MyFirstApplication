package com.example.myfirstapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
//import com.ayush.quizapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*


class RegistrationActivity : AppCompatActivity() {

    lateinit var etEmail: EditText
    lateinit var etConfPass: EditText
    private lateinit var etPass: EditText
    private lateinit var etNick: EditText
    private lateinit var btnSignUp: Button
    private lateinit var database: DatabaseReference
    //lateinit var tvRedirectLogin: TextView

    // create Firebase authentication object
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration)

        // View Bindings
        etEmail = findViewById(R.id.etSEmailAddress)
        etConfPass = findViewById(R.id.etSConfPassword)
        etPass = findViewById(R.id.etSPassword)
        etNick = findViewById(R.id.etNickname)
        btnSignUp = findViewById(R.id.btnSSigned)
        database = Firebase.database.reference
        //tvRedirectLogin = findViewById(R.id.tvRedirectLogin)

        // Initialising auth object
        auth = Firebase.auth

        btnSignUp.setOnClickListener {
            signUpUser()
    }

        // switching from signUp Activity to Login Activity
/*        tvRedirectLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }*/
  /*  fun ToHome(view: View) {
        val nav = Intent(this, CategoriesActivity::class.java)
        startActivity(nav)
    }*/
}

   private fun signUpUser() {
        val email = etEmail.text.toString()
        val pass = etPass.text.toString()
        val confirmPassword = etConfPass.text.toString()
        val nickname = etNick.text.toString()

        // check pass
        if (email.isBlank() || pass.isBlank() || confirmPassword.isBlank()) {
            Toast.makeText(this, "Email and Password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }

        if (pass != confirmPassword) {
            Toast.makeText(this, "Password and Confirm Password do not match", Toast.LENGTH_SHORT)
                .show()
            return
        }
        // If all credential are correct
        // We call createUserWithEmailAndPassword
        // using auth object and pass the
        // email and pass in it.
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(this, "Successfully Singed Up", Toast.LENGTH_SHORT).show()
                val user = auth.currentUser!!
                database.child("Users").child(user.uid).child("nickname").setValue(nickname)
                finish()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Singed Up Failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}