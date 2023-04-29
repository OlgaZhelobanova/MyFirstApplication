package com.example.myfirstapplication.ui.ingredients

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.myfirstapplication.FoundingRecipesActivity
import com.example.myfirstapplication.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

data class Ingredients(val img: String?, val name: String?)

private lateinit var recyclerView2: RecyclerView
private lateinit var recyclerView: RecyclerView
private lateinit var database: DatabaseReference
private var SelectedIngr: ArrayList<String> = arrayListOf()

public fun getIngredients(catOfIngr: String) {
    val ingredientsfdb = mutableListOf<Ingredients>()
    database = Firebase.database.reference

    database.child("Ingredients").get().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val snapshot = task.result
            if(catOfIngr == "Все"){
                snapshot.children.forEach { index ->
                    val name = snapshot.child(index.key!!).child("name")
                        .getValue(String::class.java)
                    val image = snapshot.child(index.key!!).child("img")
                        .getValue(String::class.java)
                    ingredientsfdb.add(Ingredients(image, name))
                }
            }
            else {
                snapshot.children.forEach { index ->
                    if (snapshot.child(index.key!!).child("category")
                            .getValue(String::class.java) == catOfIngr
                    ) {
                        val name = snapshot.child(index.key!!).child("name")
                            .getValue(String::class.java)
                        val image = snapshot.child(index.key!!).child("img")
                            .getValue(String::class.java)
                        ingredientsfdb.add(Ingredients(image, name))
                    }

                }

            }
            recyclerView.adapter = IngredientsFragment.IngredientsRecyclerAdapter(ingredientsfdb)
        }

    }
}

class IngredientsFragment : Fragment() {

    private lateinit var button: Button
    private lateinit var textView: TextView
    private var nameOfCurrCat = "Все"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_ingredients, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = Firebase.database.reference
        val catIngrfdb = mutableListOf<String>()
        database.child("IngCategories").get().addOnCompleteListener { category ->
            if (category.isSuccessful) {
                val snapshot = category.result
                snapshot.children.forEach { index ->

                            val name = snapshot.child(index.key!!)
                                .getValue(String::class.java)
                            catIngrfdb.add(name!!)

                        recyclerView2 = view.findViewById(R.id.catIngr)
                        recyclerView2.layoutManager = StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL)
                        recyclerView2.adapter = CatIngrRecyclerAdapter(catIngrfdb, nameOfCurrCat)
                    }
            }
        }

        recyclerView = view.findViewById(R.id.recIngr)
        recyclerView.layoutManager = GridLayoutManager(view.context, 4)
        getIngredients("Все")

        button = view.findViewById(R.id.SearchRec)
        button.setOnClickListener{
            if(SelectedIngr.isEmpty())
                Toast.makeText(view.context, "Ничего не выбрано", Toast.LENGTH_SHORT).show()
            else
                view.context.startActivity(Intent(view.context, FoundingRecipesActivity::class.java).apply {
                    putExtra("selected", SelectedIngr)
                })
            SelectedIngr.clear()
        }
    }

    class IngredientsRecyclerAdapter(private val names: List<Ingredients>) : RecyclerView
    .Adapter<IngredientsRecyclerAdapter.MyViewHolder>() {

        //var SelectedIngr = mutableListOf<String>()
        class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val largeTextView: TextView = itemView.findViewById(R.id.nameOfCat)
            val img: ImageView = itemView.findViewById(R.id.img)
            var flag: Boolean = false
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.ingredient, parent, false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.largeTextView.text = names[position].name
            Picasso.get().load( names[position].img).placeholder( R.drawable.skeleton).into(holder.img)

            for (item in SelectedIngr) {
                if (item == names[position].name) holder.itemView.setBackgroundColor(Color.argb(100, 34,139,34))
                holder.flag = true
            }

            holder.itemView.setOnClickListener {
                if(holder.flag == false) {
                    SelectedIngr.add(names[position].name!!)
                    holder.itemView.setBackgroundColor(Color.argb(100, 34,139,34))
                    holder.flag = true
                }
                else
                {
                    SelectedIngr.remove(names[position].name)
                    holder.itemView.setBackgroundColor(Color.TRANSPARENT)
                    holder.flag = false
                }
            }
        }

        override fun getItemCount() = names.size
    }

    class CatIngrRecyclerAdapter(private val names: List<String>, private var nameOfCurrCat: String) : RecyclerView
    .Adapter<CatIngrRecyclerAdapter.MyViewHolder>() {

        class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val largeTextView: TextView = itemView.findViewById(R.id.nameCatIngr)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.ingredient_category, parent, false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

            holder.largeTextView.text = names[position]
            if (names[position] == nameOfCurrCat)
                holder.largeTextView.setBackground(ContextCompat.getDrawable(holder.itemView.context, R.drawable.field_for_category));

            holder.itemView.setOnClickListener {
                nameOfCurrCat = names[position]
                getIngredients(nameOfCurrCat)
                recyclerView2.adapter = CatIngrRecyclerAdapter(names, nameOfCurrCat)
            }

        }

        override fun getItemCount() = names.size
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
}