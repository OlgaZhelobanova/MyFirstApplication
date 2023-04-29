package com.example.myfirstapplication

import android.graphics.text.LineBreaker
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.core.view.View
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.database.ktx.snapshots
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

data class Recipes(val img: String?, val name: String?, val time: String?, val portion: String?, val reyting: String?,
val ingredientstext: String?, val text: String?, val category: String?)

class RecipeActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recipe)

        database = Firebase.database.reference
        val user = Firebase.auth.currentUser!!
        val image: ImageView = findViewById(R.id.imageView4)
        val name: TextView = findViewById(R.id.tw1)
//        val kkal: TextView = findViewById(R.id.tw2)
//        val protein: TextView = findViewById(R.id.tw3)
//        val carb: TextView = findViewById(R.id.tw4)
//        val fat: TextView = findViewById(R.id.tw5)
        val time: TextView = findViewById(R.id.twTime)
        val portion: TextView = findViewById(R.id.twPortion)
        val reyting: TextView = findViewById(R.id.twReyting)
        val ingredients: TextView = findViewById(R.id.twIngredients)
        val text: TextView = findViewById(R.id.twText)

        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener{
            onBackPressed()
        }

//        val recipes = mutableListOf(Recipes("sdf", "Тост с вареным яйцом",
//            "3 мин", "1 порция", "4,6", "2 вареных яйца\n" +
//                    "2 авокадо\n" +
//                    "1/2 лайма\n" +
//                    "1/2 чайные ложки красного перца\n" +
//                    "Соль&перец\n" +
//                    "2 куска зернового хлеба", "Поджарьте 2 ломтика цельнозернового хлеба в тостере до золотистой корочки.\n" +
//                    "В небольшой миске смешайте и разомните авокадо, лайм и соль + перец по вкусу. Намажьте половину смеси на каждый ломтик поджаренного хлеба. По желанию сверху выложите жареное яйцо, яичницу-болтунью или яйцо-пашот.",
//            "Закуски"))
//
//        database.child("Recipes").push().setValue(recipes)

//        var hashMap: HashMap<String, String> = HashMap<String, String>()
//        hashMap.put("ingredientstext", "4 куриных яйца\n" +
//                    "100 мл молока\n" +
//                    "Соль")
//        database.root.child("Recipes").child("1").updateChildren(hashMap as Map<String, Any>)

        database.child("Users").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                val recipelistfdb = mutableListOf<RecipeList>()
                snapshot.children.forEach { it ->
                    val snapshot2 = snapshot.child(it.key!!).child("MyRecipes")

                    snapshot2.children.forEach { index ->
                    if (snapshot2.child(index.key!!).child("name").getValue(String::class.java) ==
                        intent.getStringExtra("recName")
                    ) {
                        name.text =
                            snapshot2.child(index.key!!).child("name").getValue(String::class.java)
//                kkal.text = snapshot.child("0").child("kkal").getValue(String::class.java)+"\nккал"
//                protein.text = snapshot.child("0").child("protein").getValue(String::class.java)+" г"+"\nбелков"
//                carb.text = snapshot.child("0").child("carb").getValue(String::class.java)+" г"+"\nуглеводов"
//                fat.text = snapshot.child("0").child("fat").getValue(String::class.java)+" г"+"\nжиров"
                        time.text =
                            snapshot2.child(index.key!!).child("time").getValue(String::class.java)
                        portion.text =
                            snapshot2.child(index.key!!).child("portion")
                                .getValue(String::class.java)
                        reyting.text =
                            snapshot2.child(index.key!!).child("reyting")
                                .getValue(String::class.java)
//                        ingredients.text = snapshot.child(index.key!!).child("ingredientstext")
//                            .getValue(String::class.java)
                        text.text =
                            snapshot2.child(index.key!!).child("text").getValue(String::class.java)
                        Picasso.get()
                            .load(
                                snapshot2.child(index.key!!).child("img")
                                    .getValue(String::class.java)
                            )
                            .into(image)
                    }

                }

            }
                }
            }
        database.child("Users").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result

                snapshot.children.forEach { it ->
                    val snapshot2 = snapshot.child(it.key!!).child("MyRecipes")

                    snapshot2.children.forEach { index ->
                    if (snapshot2.child(index.key!!).child("name").getValue(String::class.java) ==
                        intent.getStringExtra("recName")
                    ) {
                        val snapshot3 = snapshot2.child(index.key!!).child("ingredients")
                        var mass: MutableList<String>
                        mass = mutableListOf()
                        snapshot3.children.forEach()
                        { index2 ->
                            if (snapshot3.child(index2.key!!).child("quantity")
                                    .getValue(String::class.java) == "по вкусу"
                            ) {
                                mass.add(
                                    snapshot3.child(index2.key!!).child("name")
                                        .getValue(String::class.java) + " - " +
                                            snapshot3.child(index2.key!!).child("quantity")
                                                .getValue(String::class.java)
                                )
                            } else {
                                mass.add(
                                    snapshot3.child(index2.key!!).child("name")
                                        .getValue(String::class.java) + " - " +
                                            snapshot3.child(index2.key!!).child("quantity")
                                                .getValue(String::class.java) + " " +
                                            snapshot3.child(index2.key!!).child("measure")
                                                .getValue(String::class.java)
                                )
                            }
                        }
                        var str: String
                        str = ""
                        var k: Int
                        k = 0
                        for (i in 0..mass.size - 1) {
                            k = i + 1
                            str += "$k. " + mass[i] + "\n"
                        }
                        ingredients.text = str
                    }
                }
            }
            }
        }

    }
}


