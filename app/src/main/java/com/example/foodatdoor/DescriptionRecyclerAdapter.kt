package com.example.foodatdoor

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.util.ArrayList

class DescriptionRecyclerAdapter(val context: Context, val menuList: ArrayList<Restaurant>) : RecyclerView.Adapter<DescriptionRecyclerAdapter.DescriptionViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DescriptionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_menu_single_row, parent, false)

        return DescriptionViewHolder(view)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    override fun onBindViewHolder(holder: DescriptionViewHolder, position: Int) {
        val restaurant = menuList[position]
        holder.txtDishName.text = restaurant.restName
        holder.txDishPrice.text = restaurant.restPrice
        holder.txtDishRating.text = restaurant.restRating
        //holder.imgRestImage.setImageResource(restaurant.restImage)
        Picasso.get().load(restaurant.restImage).error(R.drawable.defaultrest).into(holder.imgDishImage)
/*
        holder.llContent.setOnClickListener {
            val intent = Intent(context, DescriptionFragment::class.java)
            intent.putExtra("rest_id", restaurant.restId)
            context.startActivity(intent)
        }*/

    }

    class DescriptionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtDishName: TextView = view.findViewById(R.id.txtDishName)
        val txDishPrice: TextView = view.findViewById(R.id.txtDishPrice)
        val txtDishRating: TextView = view.findViewById(R.id.txtDishRating)
        val imgDishImage: ImageView = view.findViewById(R.id.imgDishImage)
        val llContent: LinearLayout = view.findViewById(R.id.llContent)
    }
}

