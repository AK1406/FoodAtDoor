package com.example.foodatdoor

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class OrderedItemListAdapter(private val ctx: Context, private val layoutResId:Int, private val itemList:
List<CartItems>)
    : ArrayAdapter<CartItems>(ctx,layoutResId,itemList){

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater:LayoutInflater= LayoutInflater.from(ctx)
        val view:View = layoutInflater.inflate(layoutResId,null)

        val viewOrderItemName: TextView =view.findViewById(R.id.OrderItemName)
        val viewOrderItemPrice: TextView =view.findViewById(R.id.OrderItemPrice)

        val item = itemList[position]
        viewOrderItemName.text=item.itemName
        viewOrderItemPrice.text=item.itemPrice
        return view
    }
}