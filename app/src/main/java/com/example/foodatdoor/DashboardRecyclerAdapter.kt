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

class DashboardRecyclerAdapter(val context: Context, val restList: ArrayList<Restaurant>) : RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_dashboard_single_row, parent, false)

        return DashboardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restList.size
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val restaurant = restList[position]
        holder.txtRestName.text = restaurant.restName
        holder.txRestPrice.text = restaurant.restPrice
        holder.txtRestRating.text = restaurant.restRating
        //holder.imgBookImage.setImageResource(book.bookImage)
        Picasso.get().load(restaurant.restImage).error(R.drawable.defaultrest).into(holder.imgRestImage)

        holder.llContent.setOnClickListener {
            val intent = Intent(context, DescriptionActivity::class.java)
           // intent.putExtra("rest_id", restaurant.restId)
            context.startActivity(intent)
        }

    }

    class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRestName: TextView = view.findViewById(R.id.txtRestName)
        val txRestPrice: TextView = view.findViewById(R.id.txtRestPrice)
        val txtRestRating: TextView = view.findViewById(R.id.txtRestRating)
        val imgRestImage: ImageView = view.findViewById(R.id.imgRestImage)
        val llContent: LinearLayout = view.findViewById(R.id.llContent)
    }
}

