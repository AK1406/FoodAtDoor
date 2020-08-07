package com.example.foodatdoor


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat.finishAffinity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

lateinit var order_activity_history_Progressdialog: RelativeLayout
lateinit var order_history_fragment_no_orders: RelativeLayout


private  var orderList:MutableList<OrderHistoryModel> = mutableListOf()
private lateinit var myRef: DatabaseReference
private lateinit var listView: ListView
private  var listViewItem:ListView?=null
private var userId: String? = null
private var itemList:MutableList<CartItems> = mutableListOf()
private var orderItemId = ""

class OrderHistoryFragment: Fragment(), CartActivity.DataFromActivityToFragment {

    companion object {
        fun newInstance(): Fragment {
            return OrderHistoryFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myRef = FirebaseDatabase.getInstance().getReference("Orders")
        val user = FirebaseAuth.getInstance().currentUser
        userId = user!!.uid
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        order_activity_history_Progressdialog=view.findViewById(R.id.order_activity_history_Progressdialog)
        order_history_fragment_no_orders=view.findViewById(R.id.order_history_fragment_no_orders)

        listView=view.findViewById(R.id.listViewAllOrders)
        listViewItem=view.findViewById(R.id.ViewItemsOrdered)

    }

    private fun listOne(){
        myRef.child(userId!!).addValueEventListener(object:
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    orderList.clear()
                    for(i in p0.children){
                        val plan=i.getValue(OrderHistoryModel::class.java)
                        orderList.add(plan!!)
                    }
                    val adapter=OrderHistoryAdapter(context as Activity
                        ,R.layout.order_history_list_view_single_row,
                        orderList)
                    listView.adapter=adapter
                }
            }

        })
    }

    private fun listTwo(){
        myRef.child(userId!!).child(orderItemId).child("orderList").addValueEventListener(object:
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    itemList.clear()
                    for(i in p0.children){
                        val plan=i.getValue(CartItems::class.java)
                        itemList.add(plan!!)
                    }
                    val adapter=OrderedItemListAdapter(context as Activity
                        ,R.layout.order_item_single_row,
                        itemList)
                    listViewItem?.adapter=adapter
                }
            }
        })
    }

    override fun onResume() {

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            listOne()
            listTwo()
        }else
        {

            val alterDialog=androidx.appcompat.app.AlertDialog.Builder(activity as Context)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Internet Connection can't be establish!")
            alterDialog.setPositiveButton("Open Settings"){text,listener->
                val settingsIntent= Intent(Settings.ACTION_SETTINGS)//open wifi settings
                startActivity(settingsIntent)
            }

            alterDialog.setNegativeButton("Exit"){ text,listener->
                finishAffinity(activity as Activity)//closes all the instances of the app and the app closes completely
            }
            alterDialog.setCancelable(false)

            alterDialog.create()
            alterDialog.show()

        }

        super.onResume()
    }
    override fun sendData(data: String?) {
        if (data != null) orderItemId=data
    }
}