package com.example.myfirstapplication.ui.buy_list

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapplication.AddInBuyListActivity
import com.example.myfirstapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class BuyList internal constructor(var name: String?, var checked: Boolean)

class BuyListFragment : Fragment() {
    
    private lateinit var adapter: BuyListAdapter
    private lateinit var listView: ListView
    private lateinit var database: DatabaseReference
    private lateinit var button: Button
    private lateinit var mAuth: FirebaseAuth

    override fun onStart() {
        super.onStart()
        database = Firebase.database.reference
        mAuth = Firebase.auth
        var user: FirebaseUser = mAuth.currentUser!!

//        database.child(user.uid).addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val snapshot = dataSnapshot.child("BuyList")
//                snapshot.children.forEach() {
//                        index ->
//                    val name =
//                        snapshot.child(index.key!!).child("name").getValue(String::class.java)
//                    listfdb.add(BuyList(name, false))
//                }
//                listView = requireView().findViewById(R.id.lvforBuyList)
//                adapter = BuyListAdapter(requireView().context, listfdb!!)
//                listView.adapter = adapter
//
//                button = requireView().findViewById(R.id.button)
//                button!!.setOnClickListener {
//                    val intent = Intent(requireView().context, AddInBuyListActivity::class.java)
//                    startActivity(intent)
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.w(TAG, "Failed to read value.", error.toException())
//            }
//        })

////////////////////////////////////////////////////////////////////////////////////////////////////

        database.child("Users").child(user.uid).child("BuyList").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result

                val listfdb = ArrayList<BuyList>()
                snapshot.children.forEach { index ->
                    val name =
                        snapshot.child(index.key!!).child("name").getValue(String::class.java)
                    listfdb.add(BuyList(name, false))
                }

                listView = requireView().findViewById(R.id.lvforBuyList)

                adapter = view?.let { BuyListAdapter(it.context, listfdb!!) }!!
                if(listfdb.size != 0)
                listView.adapter = adapter

                button = requireView().findViewById(R.id.button)
                button!!.setOnClickListener {
                    val intent = Intent(requireView().context, AddInBuyListActivity::class.java)
                    startActivity(intent)

                }
            }
        }
    }

//    private fun updateUI(){
//        listView = requireView().findViewById(R.id.lvforBuyList)
//        adapter = BuyListAdapter(requireView().context, listfdb!!)
//        listView.adapter = adapter
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_buy_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}

public class BuyListAdapter(private val context: Context, modelArrayList: ArrayList<BuyList>) : BaseAdapter() {

    private var modelArrayList: ArrayList<BuyList>

    init {
        this.modelArrayList = modelArrayList
    }

    override fun getViewTypeCount(): Int {
        return count
    }

    override fun getItemViewType(position: Int): Int {

        return position
    }

    override fun getCount(): Int {
        return modelArrayList.size
    }

    override fun getItem(position: Int): Any {
        return modelArrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val holder: ViewHolder

        if (convertView == null) {
            holder = ViewHolder()
            val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.buy_list, null, true)

            holder.checkBox = convertView!!.findViewById(R.id.checkBox) as CheckBox
            holder.name = convertView.findViewById(R.id.txtName) as TextView

            convertView.tag = holder
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = convertView.tag as ViewHolder
        }

        holder.name!!.setText(modelArrayList[position].name)

        holder.checkBox!!.isChecked = modelArrayList[position].checked

        holder.checkBox!!.tag = position
        holder.checkBox!!.setOnClickListener {
            val pos = holder.checkBox!!.tag as Int

            if (modelArrayList[pos].checked) {
                modelArrayList[pos].checked = false
                public_modelArrayList = modelArrayList
            } else {
                modelArrayList[pos].checked = true
                public_modelArrayList = modelArrayList
            }
        }

        return convertView
    }

    private inner class ViewHolder {

        var checkBox: CheckBox? = null
        var name: TextView? = null

    }

    companion object {
        var public_modelArrayList: ArrayList<BuyList> = ArrayList()
    }

}

