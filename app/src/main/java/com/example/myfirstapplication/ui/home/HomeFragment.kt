package com.example.myfirstapplication.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapplication.Categories
import com.example.myfirstapplication.R
import com.example.myfirstapplication.RecipeActivity
import com.example.myfirstapplication.RecipeListActivity
import com.example.myfirstapplication.databinding.FragmentHomeBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

data class Recommend(val img: String?, val name: String?)

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var database: DatabaseReference
    private lateinit var recyclerView: RecyclerView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            //textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = Firebase.database.reference

        database.child("Recipes").get().addOnCompleteListener { task ->
            if (task.isSuccessful){
                val snapshot = task.result

                val recommendfdb = mutableListOf<Recommend>()
                snapshot.children.forEach{
                        index ->
                    val name = snapshot.child(index.key!!).child("name").getValue(String::class.java)
                    val image = snapshot.child(index.key!!).child("img").getValue(String::class.java)
                    recommendfdb.add(Recommend(image, name))
                }

                recyclerView = view.findViewById(R.id.recRecommend)
                recyclerView.layoutManager = GridLayoutManager(view.context, 2)
                recyclerView.adapter = RecommendRecyclerAdapter(recommendfdb)
            }
        }
    }
}

class RecommendRecyclerAdapter(private val names: List<Recommend>) : RecyclerView
.Adapter<RecommendRecyclerAdapter.MyViewHolder>() {

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