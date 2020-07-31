package com.example.foodatdoor

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class FavouriteRecyclerAdapter(val context: Context, val restList: List<RestEntity>) :
    RecyclerView.Adapter<FavouriteRecyclerAdapter.FavouriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_favourite_single_row, parent, false)
        return FavouriteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restList.size
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {

        val rest = restList[position]

        holder.txtRestName.text = rest.restName
        holder.txtRestPrice.text = "Rs "+rest.restPrice +" /Person"
        holder.txtRestRating.text = rest.restRating
        Picasso.get().load(rest.restImage).error(R.drawable.defaultrest).into(holder.imgRestImage)
    }

    class FavouriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRestName: TextView = view.findViewById(R.id.txtFavRestTitle)
        val txtRestPrice: TextView = view.findViewById(R.id.txtFavRestPrice)
        val txtRestRating: TextView = view.findViewById(R.id.txtFavRestRating)
        val imgRestImage: ImageView = view.findViewById(R.id.imgFavRestImage)
        val llContent: LinearLayout = view.findViewById(R.id.llFavContent)
    }
}

