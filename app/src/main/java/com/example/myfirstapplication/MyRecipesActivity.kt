package com.example.myfirstapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.myfirstapplication.ui.my_profile.Favourite
import com.example.myfirstapplication.ui.my_profile.FavouriteRecyclerAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MyRecipesActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var toolbar: Toolbar
    private lateinit var button: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var textView: TextView


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_recipes)

        toolbar = findViewById(R.id.myToolBar)
        toolbar!!.title = "Мои рецепты"
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)

        database = Firebase.database.reference
        val user = Firebase.auth.currentUser!!

        refresh = findViewById(R.id.refreshLayout)
        textView = findViewById(R.id.refreshText)
        refresh.setOnRefreshListener{
            database.child("Users").child(user.uid).child("MyRecipes").get().addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val snapshot = task.result

                    val myrecfdb = mutableListOf<Favourite>()
                    snapshot.children.forEach{
                            index ->
                        val name = snapshot.child(index.key!!).child("name").getValue(String::class.java)
                        val image = snapshot.child(index.key!!).child("img").getValue(String::class.java)
                        myrecfdb.add(Favourite(image, name))
                    }

                    recyclerView = this.findViewById(R.id.recMyRec)
                    recyclerView.layoutManager = GridLayoutManager(this, 2)
                    recyclerView.adapter = FavouriteRecyclerAdapter(myrecfdb)

                }
            }
            refresh.isRefreshing = false
        }

        database.child("Users").child(user.uid).child("MyRecipes").get().addOnCompleteListener { task ->
            if (task.isSuccessful){
                val snapshot = task.result

                val myrecfdb = mutableListOf<Favourite>()
                snapshot.children.forEach{
                        index ->
                    val name = snapshot.child(index.key!!).child("name").getValue(String::class.java)
                    val image = snapshot.child(index.key!!).child("img").getValue(String::class.java)
                        myrecfdb.add(Favourite(image, name))
                }

                recyclerView = this.findViewById(R.id.recMyRec)
                recyclerView.layoutManager = GridLayoutManager(this, 2)
                recyclerView.adapter = FavouriteRecyclerAdapter(myrecfdb)

            }
        }

        button = findViewById(R.id.addButton2)
        button.setOnClickListener {
            this.startActivity(Intent(this, AddRecipeActivity::class.java))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}