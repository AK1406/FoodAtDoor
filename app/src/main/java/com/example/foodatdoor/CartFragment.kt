package com.example.foodatdoor

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CartFragment : Fragment(),DescriptionRecyclerAdapter.DataFromAdapterToFragment {


    lateinit var textViewOrderingFrom: TextView
    lateinit var buttonPlaceOrder: Button
    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var menuAdapter: CartAdapter
    private var restaurantId: String=""
    private var restaurantName: String=""
    lateinit var linearLayout: LinearLayout
    lateinit var activity_cart_Progressdialog: RelativeLayout
    private var selectedItemsId: ArrayList<String> =arrayListOf()

    var totalAmount = 0

    var cartListItems = arrayListOf<CartItems>()

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
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonPlaceOrder=view.findViewById(R.id.buttonPlaceOrder)
        textViewOrderingFrom=view.findViewById(R.id.textViewOrderingFrom)
        linearLayout=view.findViewById(R.id.linearLayout)
        activity_cart_Progressdialog=view.findViewById(R.id.activity_cart_Progressdialog)

        //set the restaurant name
        textViewOrderingFrom.text= restaurantName

        val id = restaurantId
        val name =restaurantName
        val item =selectedItemsId

        buttonPlaceOrder.setOnClickListener(View.OnClickListener {

            val sharedPreferencess=context?.getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE)

            if (ConnectionManager().checkConnectivity(activity as Context)) {

                activity_cart_Progressdialog.visibility=View.VISIBLE

                try {

                    val foodJsonArray= JSONArray()

                    for (foodItem in selectedItemsId){
                        val singleItemObject= JSONObject()
                        singleItemObject.put("food_item_id",foodItem)
                        foodJsonArray.put(singleItemObject)

                    }

                    val sendOrder = JSONObject()

                    sendOrder.put("user_id",sharedPreferencess?.getString("user_id","0"))
                    sendOrder.put("restaurant_id",restaurantId.toString())
                    sendOrder.put("total_cost", totalAmount)
                    sendOrder.put("food",foodJsonArray)

                    val queue = Volley.newRequestQueue(activity as Context)

                    val url = "http://13.235.250.119/v2/place_order/fetch_result"

                    val jsonObjectRequest = object : JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        sendOrder,
                        Response.Listener {

                            val responseJsonObjectData = it.getJSONObject("data")

                            val success = responseJsonObjectData.getBoolean("success")

                            if (success) {

                                Toast.makeText(
                                    context,
                                    "Order Placed",
                                    Toast.LENGTH_SHORT
                                ).show()


                                createNotification()

                                val intent =Intent(activity,OrderPlacedActivity::class.java)
                                startActivity(intent)



                            } else {
                                val responseMessageServer =
                                    responseJsonObjectData.getString("errorMessage")
                                Toast.makeText(
                                    context,
                                    responseMessageServer.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                            activity_cart_Progressdialog.visibility=View.INVISIBLE
                        },
                        Response.ErrorListener {

                            println("ssssss"+it)

                            Toast.makeText(
                                context,
                                "Some Error occurred!!!",
                                Toast.LENGTH_SHORT
                            ).show()

                        }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()

                            headers["Content-Type"] = "application/json"
                            headers["token"] = "c3b5e952c8e343"  //c3b5e952c8e343

                            return headers
                        }
                    }
                    jsonObjectRequest.retryPolicy = DefaultRetryPolicy(15000,
                        1,
                        1f
                    )

                    queue.add(jsonObjectRequest)

                } catch (e: JSONException) {
                    Toast.makeText(
                        context,
                        "Some unexpected error occured!!!",
                        Toast.LENGTH_SHORT
                    ).show()

                }

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
        })


        layoutManager = LinearLayoutManager(context)//set the layout manager

        recyclerView = view.findViewById(R.id.recyclerViewCart)
    }
    fun fetchData(){

        if (ConnectionManager().checkConnectivity(activity as Context)) {

            activity_cart_Progressdialog.visibility=View.VISIBLE

            try {

                val queue = Volley.newRequestQueue(activity as Context)

                val url = "http://13.235.250.119/v2/restaurants/fetch_result/fetch_with_restaurant_id?restaurantId=" + restaurantId

                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    Response.Listener {

                        val responseJsonObjectData = it.getJSONObject("data")

                        val success = responseJsonObjectData.getBoolean("success")

                        if (success) {

                            val data = responseJsonObjectData.getJSONArray("data")

                            //old listener of jsonObjectRequest are still listening therefore clear is used
                            cartListItems.clear()//clear all items to get updated values

                            totalAmount=0

                            for (i in 0 until data.length()) {
                                val cartItemJsonObject = data.getJSONObject(i)

                                if(selectedItemsId.contains(cartItemJsonObject.getString("id")))//if the fetched id is present in the selected id save
                                {

                                    val menuObject = CartItems(
                                        cartItemJsonObject.getString("id"),
                                        cartItemJsonObject.getString("name"),
                                        cartItemJsonObject.getString("cost_for_one"),
                                        cartItemJsonObject.getString("restaurant_id"))

                                    totalAmount += cartItemJsonObject.getString("cost_for_one")
                                        .toString().toInt()


                                    cartListItems.add(menuObject)

                                }
                                //progressBar.visibility = View.GONE

                                menuAdapter = CartAdapter(
                                    activity as Context,//pass the relativelayout which has the button to enable it later
                                    cartListItems
                                )//set the adapter with the data

                                recyclerView.adapter =
                                    menuAdapter//bind the  recyclerView to the adapter

                                recyclerView.layoutManager =
                                    layoutManager //bind the  recyclerView to the layoutManager

                            }

                            //set the total on the button
                            buttonPlaceOrder.text= "Place Order(Total:Rs. $totalAmount)"

                        }
                        activity_cart_Progressdialog.visibility=View.INVISIBLE
                    },
                    Response.ErrorListener {

                        Toast.makeText(
                            context,
                            "Some Error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()

                        activity_cart_Progressdialog.visibility=View.INVISIBLE

                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()

                        headers["Content-Type"] = "application/json"
                        headers["token"] = "c3b5e952c8e343"  //c3b5e952c8e343
                        return headers
                    }
                }

                queue.add(jsonObjectRequest)

            } catch (e: JSONException) {
                Toast.makeText(
                    context,
                    "Some Unexpected error occured!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
        else {

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

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            android.R.id.home->{
             //   super.onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }



    override fun onResume() {

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            fetchData()//if internet is available fetch data
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

    fun createNotification(){
        val notificationId=1;
        val channelId="personal_notification"



        val notificationBulider=NotificationCompat.Builder(activity as Context,channelId)
        notificationBulider.setSmallIcon(R.drawable.fud)
        notificationBulider.setContentTitle("Order Placed")
        notificationBulider.setContentText("Your order has been successfully placed!")
        notificationBulider.setStyle(NotificationCompat.BigTextStyle()
            .bigText("Ordered from $restaurantName and amounting to Rs.$totalAmount"))
        notificationBulider.setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManagerCompat= NotificationManagerCompat.from(activity as Context)
        notificationManagerCompat.notify(notificationId,notificationBulider.build())

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O)//less than oreo
        {
            val name ="Order Placed"
            val description="Your order has been successfully placed!"
            val importance=NotificationManager.IMPORTANCE_DEFAULT

            val notificationChannel= NotificationChannel(channelId,name,importance)

            notificationChannel.description=description

            val notificationManager= requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    override fun sendData(rest_id: String?,rest_name:String?,itemSelected_id:ArrayList<String>) {
        if (rest_id!= null){
            restaurantId=rest_id
        }
        if (rest_name!= null){
            restaurantName=rest_name
        }
        selectedItemsId=itemSelected_id
    }
}