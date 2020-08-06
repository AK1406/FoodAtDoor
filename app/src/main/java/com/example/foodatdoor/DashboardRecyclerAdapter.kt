package com.example.foodatdoor

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.util.*

class DashboardRecyclerAdapter(val context: Context, var restList: ArrayList<Restaurant>) :
    RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>() {

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


        holder.llContent.setOnClickListener {
            val intent = Intent(context, DescriptionActivity::class.java)
            intent.putExtra("restaurant_id", restaurant.restId)
            intent.putExtra("restaurant_name",restaurant.restName)
            context.startActivity(intent)
            }


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

    fun filterList(filteredList: ArrayList<Restaurant>) {//to update the recycler view depending on the search
        restList = filteredList
        notifyDataSetChanged()
    }

}


