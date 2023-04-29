package com.example.myfirstapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapplication.ui.ingredients.Ingredients
import com.example.myfirstapplication.ui.ingredients.IngredientsFragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class FoundingRecipesActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    private lateinit var database: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerView2: RecyclerView
    private lateinit var textView: TextView
    private lateinit var textView2: TextView
    private lateinit var textView3: TextView
    private lateinit var toolbar: Toolbar


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.founding_recipes)

        database = Firebase.database.reference
        recyclerView = findViewById(R.id.recFoundRecipes)
        recyclerView2 = findViewById(R.id.recFoundRecipes2)
        textView = findViewById(R.id.txtxt)
        textView2 = findViewById(R.id.fullMatch)
        textView3 = findViewById(R.id.notfullMatch)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView2.layoutManager = GridLayoutManager(this, 2)
        var selected = intent.getStringArrayListExtra("selected")
        var count = selected!!.count()
        var countIngrInRecipe = 0

        toolbar = findViewById(R.id.myToolBar)
        toolbar!!.title = "Результаты поиска"
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)

        database.child("Users").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                val recipesfdb = mutableListOf<RecipeList>()
                val recipesfdb2 = mutableListOf<RecipeList>()

                snapshot.children.forEach { index ->
                    val snapshot2 = snapshot.child(index.key!!).child("MyRecipes")

                snapshot2.children.forEach{
                    val snapshot3 = snapshot2.child(it.key!!).child("ingredients")
                    var countSovp = 0;
                    snapshot3.children.forEach{ ingr ->
                        if(snapshot3.child(ingr.key!!).child("quantity").getValue(String::class.java) != "по вкусу")
                            countIngrInRecipe++
                        if (selected != null) {
                            selected.forEach{
                                if(snapshot3.child(ingr.key!!).child("name").getValue(String::class.java)==it){
                                    countSovp++;
                                }
                            }

                        }
                    }


                    if(count>=countSovp && countSovp == countIngrInRecipe && countSovp > 0){
                       val element = RecipeList(
                            snapshot2.child(it.key!!).child("img").getValue(String::class.java),
                            snapshot2.child(it.key!!).child("name").getValue(String::class.java)
                        )
                        recipesfdb.add(element)

                    }
                    if(count>=countSovp && countSovp < countIngrInRecipe && countSovp > 0){
                        val element = RecipeList(
                            snapshot2.child(it.key!!).child("img").getValue(String::class.java),
                            snapshot2.child(it.key!!).child("name").getValue(String::class.java)
                        )
                        recipesfdb2.add(element)

                    }
                    countIngrInRecipe = 0

                }

                }

                if(recipesfdb.count()>0 || recipesfdb2.count()>0)
                    textView.text = ""
                if (recipesfdb.count()>0 && recipesfdb2.count()==0)
                    textView3.textSize = 0.1F
                if (recipesfdb.count()==0 && recipesfdb2.count()>0)
                    textView2.textSize = 0.1F
                if (recipesfdb.count()==0 && recipesfdb2.count()==0) {
                    textView2.text = ""
                    textView3.text = ""
                }
                recyclerView.adapter = CustomRecyclerAdapter2(recipesfdb)
                recyclerView2.adapter = CustomRecyclerAdapter2(recipesfdb2)
            }
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

