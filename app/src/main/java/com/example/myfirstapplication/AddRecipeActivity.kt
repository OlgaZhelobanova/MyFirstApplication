package com.example.myfirstapplication

import android.annotation.SuppressLint
import android.app.DownloadManager.Query
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.util.*

data class addRecipes(var name: String?, var category: String?, var portion: String?,
                      var text: String?, var time: String, var ingredients: MutableList<addIngredients>, var reyting: String?)
data class addIngredients(var id: String, var name: String?, var quantity: String?, var measure: String?)
var ingrs = mutableListOf<addIngredients>()
private lateinit var recyclerView: RecyclerView
private lateinit var spinner: Spinner
private lateinit var spinner2: Spinner

class AddRecipeActivity : AppCompatActivity(){

    private lateinit var toolbar: Toolbar
    private lateinit var addButton: TextView
    private lateinit var buttonAddRecipe: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_recipe)

        toolbar = findViewById(R.id.myToolBar)
        toolbar!!.title = "Добавление рецепта"
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)

        spinner = findViewById(R.id.spinnerCategories)
        var adapter:ArrayAdapter<CharSequence>
        adapter = ArrayAdapter<CharSequence> (this, android.R.layout.simple_spinner_item, resources.getTextArray(R.array.categoriesOfRecipes))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.setAdapter(adapter)

        spinner2 = findViewById(R.id.spinnerTime)
        var adapter2:ArrayAdapter<CharSequence>
        adapter2 = ArrayAdapter<CharSequence> (this, android.R.layout.simple_spinner_item, resources.getTextArray(R.array.time))
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner2.setAdapter(adapter2)

        ingrs.clear()
        ingrs.add(addIngredients(UUID.randomUUID().toString(), "","",""))

        recyclerView = findViewById(R.id.recIngredients)
        recyclerView.layoutManager = GridLayoutManager(this, 1)
        recyclerView.adapter = AddIngredientRecyclerAdapter(ingrs)

        addButton = findViewById(R.id.textViewAddIngr)
        addButton.setOnClickListener {
            ingrs.add(addIngredients(UUID.randomUUID().toString(), "","",""))
            recyclerView.adapter = AddIngredientRecyclerAdapter(ingrs)
        }

        buttonAddRecipe = findViewById(R.id.buttonAddRecipe)
        buttonAddRecipe.setOnClickListener {
            addInDB()
            val text = "Новый рецепт добавлен!"
            val duration = Toast.LENGTH_SHORT

            val toast = Toast.makeText(applicationContext, text, duration)
            toast.show()
            finish()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun addInDB()
    {   lateinit var editText: EditText
        lateinit var editText2: EditText
        lateinit var editText3: EditText
        lateinit var editText4: EditText
        lateinit var database: DatabaseReference

        editText = findViewById(R.id.editTextNameOfRecipe)
        spinner = findViewById(R.id.spinnerCategories)
        editText2 = findViewById(R.id.editTextTime)
        spinner2 = findViewById(R.id.spinnerTime)
        editText3 = findViewById(R.id.editTextPortion)
        editText4 = findViewById(R.id.editTextOfRecipe)

        database = Firebase.database.reference
        val user = Firebase.auth.currentUser!!

        var portionString = ""
        when (editText3.text.toString()) {
            "1" -> portionString = editText3.text.toString() + " порция"
            "2" -> portionString = editText3.text.toString() + " порции"
            "3" -> portionString = editText3.text.toString() + " порции"
            "4" -> portionString = editText3.text.toString() + " порции"
            else -> {
                portionString = editText3.text.toString() + " порций"
            }
        }

        val recipe = addRecipes(editText.text.toString(), spinner.selectedItem.toString(), portionString,
            editText4.text.toString(), editText2.text.toString()+" "+spinner2.selectedItem.toString(), ingrs, "0")
        database.child("Users").child(user.uid).child("MyRecipes").child(UUID.randomUUID().toString()).setValue(recipe)

    }


    class AddIngredientRecyclerAdapter(private var names: List<addIngredients>) : RecyclerView
    .Adapter<AddIngredientRecyclerAdapter.MyViewHolder>() {

        class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView: AutoCompleteTextView = itemView.findViewById(R.id.textViewForIngrName)
            val editText: EditText = itemView.findViewById(R.id.editTextForQuantity)
            var spinner: Spinner = itemView.findViewById(R.id.spinnerForMeasure)
            val imageButton: ImageButton = itemView.findViewById(R.id.imageButtonForDel)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.add_ingredient, parent, false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, @SuppressLint("RecyclerView") position: Int) {

            holder.textView.setText(ingrs[position].name)
            holder.editText.setText(ingrs[position].quantity)
            if(ingrs[position].measure != "") {

            }

            ingrs[position].measure = holder.spinner.selectedItem.toString()

            holder.imageButton.setOnClickListener {
                ingrs.remove(names[position])
                recyclerView.adapter = AddIngredientRecyclerAdapter(ingrs)
            }

            holder.textView.setOnKeyListener (View.OnKeyListener { view, i, keyEvent ->
                if((keyEvent.action == KeyEvent.ACTION_DOWN) and (i == KeyEvent.KEYCODE_ENTER))
                {
                    ingrs[position].name = holder.textView.text.toString()
                    true
                }
                false
            })

            holder.editText.setOnKeyListener (View.OnKeyListener { view, i, keyEvent ->
                if((keyEvent.action == KeyEvent.ACTION_DOWN) and (i == KeyEvent.KEYCODE_ENTER))
                {
                    ingrs[position].quantity = holder.editText.text.toString()
                    true
                }
                false
            })

            val itemSelectedListener: AdapterView.OnItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        pos: Int,
                        id: Long
                    ) {
                        ingrs[position].measure = holder.spinner.selectedItem.toString()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            holder.spinner.onItemSelectedListener = itemSelectedListener
        }

        override fun getItemCount() = names.size
    }
}