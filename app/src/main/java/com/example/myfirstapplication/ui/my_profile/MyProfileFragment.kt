package com.example.myfirstapplication.ui.my_profile

import android.content.Intent
import android.graphics.Typeface
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.myfirstapplication.*
import com.example.myfirstapplication.ui.ingredients.Ingredients
import com.example.myfirstapplication.ui.ingredients.IngredientsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

data class Favourite(val img: String?, val name: String?)

class MyProfileFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton: Button
    private lateinit var myRecTextView: TextView
    private lateinit var savedRecTextView: TextView
    private lateinit var addTextView: TextView
    private lateinit var lookAllTextView: TextView
    private val favouritefdb = mutableListOf<Favourite>()
    private val myrecfdb = mutableListOf<Favourite>()
    private lateinit var nickTextView: TextView
    private lateinit var myRecCountTextView: TextView
    private lateinit var savedCountTextView: TextView
    private lateinit var refresh: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = Firebase.database.reference
        val user = Firebase.auth.currentUser

        nickTextView = view.findViewById(R.id.nickname)
        database.child("Users").child(user!!.uid).child("nickname").get().addOnCompleteListener { task ->
            if(task.isSuccessful) {
                val snapshot = task.result
                nickTextView.text = snapshot.getValue(String::class.java)
            }
        }

        myRecCountTextView = view.findViewById(R.id.myRecCount)
        savedCountTextView = view.findViewById(R.id.savedCount)

        database.child("Users").child(user!!.uid).child("Favourites").get().addOnCompleteListener { task ->
            if (task.isSuccessful){
                val snapshot = task.result

                var counter = 0
                snapshot.children.forEach{
                        index ->
                    counter++
                    val name = snapshot.child(index.key!!).child("name").getValue(String::class.java)
                    val image = snapshot.child(index.key!!).child("img").getValue(String::class.java)
                    if(counter < 3) {
                        favouritefdb.add(Favourite(image, name))
                    }
                }
                savedCountTextView.text = counter.toString()
            }
        }

        database.child("Users").child(user.uid).child("MyRecipes").get().addOnCompleteListener { task ->
            if (task.isSuccessful){
                val snapshot = task.result

                var counter2 = 0
                snapshot.children.forEach{
                        index ->
                    counter2++
                    val name = snapshot.child(index.key!!).child("name").getValue(String::class.java)
                    val image = snapshot.child(index.key!!).child("img").getValue(String::class.java)
                    if(counter2 < 3) {
                        myrecfdb.add(Favourite(image, name))
                    }
                }
                recyclerView = view.findViewById(R.id.recFav)
                recyclerView.layoutManager = GridLayoutManager(view.context, 2)
                recyclerView.adapter = FavouriteRecyclerAdapter(myrecfdb)

                myRecCountTextView.text = counter2.toString()
            }
        }

        lookAllTextView = requireView().findViewById(R.id.textView9)
        lookAllTextView.setOnClickListener {
            requireContext().startActivity(Intent(context, MyRecipesActivity::class.java))
        }

        refresh = view.findViewById(R.id.refreshLayout2)

        refresh.setOnRefreshListener{
            database.child("Users").child(user!!.uid).child("nickname").get().addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    val snapshot = task.result
                    nickTextView.text = snapshot.getValue(String::class.java)
                }
            }

            myRecCountTextView = view.findViewById(R.id.myRecCount)
            savedCountTextView = view.findViewById(R.id.savedCount)

            database.child("Users").child(user.uid).child("MyRecipes").get().addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val snapshot = task.result
                    myrecfdb.clear()
                    var counter2 = 0
                    snapshot.children.forEach{
                            index ->
                        counter2++
                        val name = snapshot.child(index.key!!).child("name").getValue(String::class.java)
                        val image = snapshot.child(index.key!!).child("img").getValue(String::class.java)
                        if(counter2 < 3) {
                            myrecfdb.add(Favourite(image, name))
                        }
                    }
                    recyclerView = view.findViewById(R.id.recFav)
                    recyclerView.layoutManager = GridLayoutManager(view.context, 2)
                    recyclerView.adapter = FavouriteRecyclerAdapter(myrecfdb)

                    myRecCountTextView.text = counter2.toString()
                }
            }

            refresh.isRefreshing = false
        }


        SetOnClick()
    }

    fun SetOnClick(){
        myRecTextView = requireView().findViewById(R.id.textView7)
        savedRecTextView = requireView().findViewById(R.id.textView8)
        addTextView = requireView().findViewById(R.id.textView10)
        addButton = requireView().findViewById(R.id.addButton)
        lookAllTextView = requireView().findViewById(R.id.textView9)

        myRecTextView.setOnClickListener {
            savedRecTextView.setTextAppearance(context, R.style.normalText)
            myRecTextView.setTextAppearance(context, R.style.boldText)
            addTextView.isVisible = true
            addButton.isVisible = true

            recyclerView = requireView().findViewById(R.id.recFav)
            recyclerView.layoutManager = GridLayoutManager(requireView().context, 2)
            recyclerView.adapter = FavouriteRecyclerAdapter(myrecfdb)

            lookAllTextView.setOnClickListener {
                requireContext().startActivity(Intent(context, MyRecipesActivity::class.java))
            }
        }

        savedRecTextView.setOnClickListener {
            myRecTextView.setTextAppearance(context, R.style.normalText)
            savedRecTextView.setTextAppearance(context, R.style.boldText)
            addTextView.isVisible = false
            addButton.isVisible = false

            recyclerView = requireView().findViewById(R.id.recFav)
            recyclerView.layoutManager = GridLayoutManager(requireView().context, 2)
            recyclerView.adapter = FavouriteRecyclerAdapter(favouritefdb)

            lookAllTextView.setOnClickListener {
                requireContext().startActivity(Intent(context, FavouritesActivity::class.java))
            }
        }

        addButton.setOnClickListener {
            requireView().context.startActivity(Intent(requireView().context, AddRecipeActivity::class.java))
        }
    }
}

    class FavouriteRecyclerAdapter(private val names: List<Favourite>) : RecyclerView
    .Adapter<FavouriteRecyclerAdapter.MyViewHolder>() {

        class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val largeTextView: TextView = itemView.findViewById(R.id.nameOfCat)
            val img: ImageView = itemView.findViewById(R.id.img)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.category, parent, false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.largeTextView.text = names[position].name
            Picasso.get().load( names[position].img).placeholder( R.drawable.skeleton).into(holder.img)
            holder.itemView.setOnClickListener { view ->
                view.context.startActivity(Intent(view.context, RecipeActivity::class.java).apply{
                    putExtra("recName", names[position].name)
                })
            }
        }

        override fun getItemCount() = names.size
    }
