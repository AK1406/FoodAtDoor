package com.example.foodatdoor

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room

class CartFragment : Fragment() {

    companion object {
        fun newInstance(): Fragment {
            return CartFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_cart, container, false)

/*
        recyclerDish = view.findViewById(R.id.recyclerCart)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)

        layoutManager = GridLayoutManager(activity as Context, 2)

        dbDishList = RetrieveDishes(activity as Context).execute().get()

        if (activity != null) {
            progressLayout.visibility = View.GONE
            recyclerAdapter = CartRecyclerAdapter(activity as Context, dbDishList)
            recyclerDish.adapter = recyclerAdapter
            recyclerDish.layoutManager = layoutManager
        }
*/

        return view
    }
/*
    class RetrieveDishes(val context: Context) : AsyncTask<Void, Void, List<DishEntity>>() {

        override fun doInBackground(vararg p0: Void?): List<DishEntity> {
            val db = Room.databaseBuilder(context, DishDatabase::class.java, "dish-db").build()
            return db.dishDao().getAllDish()
        }

    }*/
}
