package com.example.foodatdoor

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.squareup.picasso.Picasso
import java.util.*

class DashboardRecyclerAdapter(val context: Context, val restList: ArrayList<Restaurant>) : RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_dashboard_single_row, parent, false)

        return DashboardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val restaurant = restList[position]
        holder.txtRestName.text = restaurant.restName
        holder.txtRestPrice.text = "Rs "+ restaurant.restPrice +" /-"
        holder.txtRestRating.text = restaurant.restRating
        //holder.imgRestImage.setImageResource(restaurant.restImage)
        Picasso.get().load(restaurant.restImage).error(R.drawable.defaultrest).into(holder.imgRestImage)

        /*
        holder.addFav.setOnClickListener{
            holder.addFav.setImageResource(R.drawable.ic_favourites)
        }*/

        /*holder.llContent.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("rest_id",restaurant.restId)
            val fragObj = DescriptionFragment()
            fragObj.arguments = bundle
        }*/

        //pass data to entity
        val restEntity = RestEntity(
            restaurant.restId.toInt(),
        restaurant.restName,
        restaurant.restRating,
        restaurant.restPrice,
        restaurant.restImage)

        val checkFav = DashboardFragment.DBAsyncTask(context as Activity, restEntity, 1).execute()

        val isFav = checkFav.get()


        if (isFav) {
            holder.addFav.setImageResource(R.drawable.ic_favourites)
        } else {
            holder.addFav.setImageResource(R.drawable.ic_fav)
        }

        holder.addFav.setOnClickListener {

            if (!DashboardFragment.DBAsyncTask(
                    context as Activity,
                    restEntity,
                    1
                ).execute().get()
            ) {

                val async =
                    DashboardFragment.DBAsyncTask(context , restEntity, 2).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(context,
                        "Restaurant added to favourites",
                        Toast.LENGTH_SHORT
                    ).show()

                    holder.addFav.setImageResource(R.drawable.ic_favourites);
                } else {
                    Toast.makeText(
                        context,
                        "Some error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {

                val async = DashboardFragment.DBAsyncTask(context , restEntity, 3).execute()
                val result = async.get()

                if (result){
                    Toast.makeText(
                        context ,
                        "Restaurant removed from favourites",
                        Toast.LENGTH_SHORT
                    ).show()
                    holder.addFav.setImageResource(R.drawable.ic_fav);
                } else {
                    Toast.makeText(
                        context ,
                        "Some error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }


    }



    class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRestName: TextView = view.findViewById(R.id.txtRestName)
        val txtRestPrice: TextView = view.findViewById(R.id.txtRestPrice)
        val txtRestRating: TextView = view.findViewById(R.id.txtRestRating)
        val imgRestImage: ImageView = view.findViewById(R.id.imgRestImage)
        val llContent: LinearLayout = view.findViewById(R.id.RestContent)
        val addFav :ImageView=view.findViewById(R.id.btnAddToFav)
    }
    /*
class DBAsyncTask(val context: Context, val restEntity: RestEntity, val mode: Int) :
    AsyncTask<Void, Void, Boolean>() {

    *//*
    Mode 1 -> Check DB if the restaurant is favourite or not
    Mode 2 -> Save the restaurant into DB as favourite
    Mode 3 -> Remove the favourite restaurant
    * *//*

    val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "rest-db").build()

    override fun doInBackground(vararg p0: Void?): Boolean {

        when (mode) {

            1 -> {

                // Check DB if the rest is favourite or not
                val book: RestEntity? = db.restDao().getRestById(restEntity.rest_id.toString())
                db.close()
                return book != null

            }

            2 -> {
                // Save the rest into DB as favourite
                db.restDao().insertRest(restEntity)
                db.close()
                return true
            }

            3 -> {
                // Remove the favourite restaurant
                db.restDao().deleteRest(restEntity)
                db.close()
                return true
            }
        }
        return false
    }

}*/
}


