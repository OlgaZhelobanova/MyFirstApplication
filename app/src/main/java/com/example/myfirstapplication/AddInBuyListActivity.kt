package com.example.myfirstapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import com.example.myfirstapplication.ui.buy_list.BuyListAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

data class AddProduct(var name: String?)

class AddInBuyListActivity : AppCompatActivity() {

    private var tv: TextView? = null
    private lateinit var database: DatabaseReference
    private lateinit var editText: EditText
    private lateinit var button: Button
    private lateinit var toolbar: Toolbar
    private var mAuth: FirebaseAuth = Firebase.auth
    var user: FirebaseUser = mAuth.currentUser!!


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_in_buy_list)

        toolbar = findViewById(R.id.myToolBar)
        toolbar!!.title = "Добавление продукта"
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)

        database = Firebase.database.reference

        database.child("Users").child(user.uid).child("BuyList").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result

                snapshot.children.forEach { index ->
                    for (i in 0 until BuyListAdapter.public_modelArrayList!!.size) {
                        if (snapshot.child(index.key!!).child("name")
                                .getValue(String::class.java) == BuyListAdapter.public_modelArrayList!!.get(
                                i
                            ).name
                        )
                            if (BuyListAdapter.public_modelArrayList!!.get(i).checked) {
                                database.root.child("Users").child(user.uid).child("BuyList").child(index.key!!).removeValue()
                            }
                    }
                }
            }
        }

        button = findViewById(R.id.buttonAddProduct)

        button.setOnClickListener {
            addProductInDB()
            val text = "Список покупок обновлен!"
            val duration = Toast.LENGTH_SHORT

            val toast = Toast.makeText(applicationContext, text, duration)
            toast.show()
        }

//        for (i in 0 until BuyListAdapter.public_modelArrayList!!.size) {
//            if (BuyListAdapter.public_modelArrayList!!.get(i).checked) {
//                tv!!.text = tv!!.text.toString() + " " + BuyListAdapter.public_modelArrayList!!.get(i).name
//            }
//        }
    }

    fun addProductInDB(){
        editText = findViewById(R.id.editTextProduct)
        val addProduct = AddProduct(editText.text.toString())
        database.child("Users").child(user.uid).child("BuyList").child(UUID.randomUUID().toString()).setValue(addProduct)
        onSupportNavigateUp()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}