package com.example.myfirstapplication

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapplication.ui.my_profile.Favourite
import com.example.myfirstapplication.ui.my_profile.FavouriteRecyclerAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FavouritesActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var toolbar: Toolbar
    private lateinit var recyclerView: RecyclerView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favourites)

        toolbar = findViewById(R.id.myToolBar)
        toolbar!!.title = "Сохраненные рецепты"
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)

        database = Firebase.database.reference
        val user = Firebase.auth.currentUser!!

        database.child("Users").child(user.uid).child("Favourites").get().addOnCompleteListener { task ->
            if (task.isSuccessful){
                val snapshot = task.result

                val favouritesfdb = mutableListOf<Favourite>()
                snapshot.children.forEach{
                        index ->
                    val name = snapshot.child(index.key!!).child("name").getValue(String::class.java)
                    val image = snapshot.child(index.key!!).child("img").getValue(String::class.java)
                    favouritesfdb.add(Favourite(image, name))
                }

                recyclerView = this.findViewById(R.id.recFav2)
                recyclerView.layoutManager = GridLayoutManager(this, 2)
                recyclerView.adapter = FavouriteRecyclerAdapter(favouritesfdb)

            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}