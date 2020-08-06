package com.example.foodatdoor

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager

class OrderHistoryAdapter(private val ctx: Context, private val layout1:Int,private val orderList:
List<OrderHistoryModel>)
    : ArrayAdapter<OrderHistoryModel>(ctx,layout1,orderList){

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater:LayoutInflater= LayoutInflater.from(ctx)
        val view:View = layoutInflater.inflate(layout1,null)
        val restName:TextView=view.findViewById(R.id.ResturantName)
        val orderDate:TextView=view.findViewById(R.id.PlaceOrderDate)
        val list :ListView=view.findViewById(R.id.ViewItemsOrdered)

        val order  = orderList[position]
        restName.text= order.restaurantName
        orderDate.text=order.orderPlacedAt
        val listItem = order.orderList
     //   setupRecyclerview(listItem)
        return view
    }

}