package com.example.myfirstapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class StartPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseAuth.getInstance().signOut()
        val currentUser = Firebase.auth.currentUser
        if (currentUser != null)
            startActivity(Intent(this, NavigationActivity::class.java))
        else
            setContentView(R.layout.start_page)
    }

    fun NextActivity (view: View)
    {
        val randomIntent = Intent(this,LoginActivity::class.java)
        startActivity(randomIntent);
    }
}
