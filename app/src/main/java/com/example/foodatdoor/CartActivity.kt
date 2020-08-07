package com.example.foodatdoor

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


lateinit var textViewOrderingFrom:TextView
lateinit var buttonPlaceOrder: Button
lateinit var recyclerView: RecyclerView
lateinit var layoutManager: RecyclerView.LayoutManager
lateinit var menuAdapter: CartAdapter
lateinit var restaurantId:String
lateinit var restaurantName:String
lateinit var linearLayout:LinearLayout
lateinit var activity_cart_Progressdialog:RelativeLayout
lateinit var selectedItemsId:ArrayList<String>
private var userId: String? = null

var totalAmount=0

var cartListItems = arrayListOf<CartItems>()

class CartActivity : AppCompatActivity() {

    private var dataFromActivityToFragment: DataFromActivityToFragment? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        buttonPlaceOrder=findViewById(R.id.buttonPlaceOrder)
        textViewOrderingFrom=findViewById(R.id.textViewOrderingFrom)
        linearLayout=findViewById(R.id.linearLayout)
        activity_cart_Progressdialog=findViewById(R.id.activity_cart_Progressdialog)

        restaurantId=intent.getStringExtra("restaurantId")
        restaurantName=intent.getStringExtra("restaurantName")
        selectedItemsId= intent.getStringArrayListExtra("selectedItemsId")

        //set the restaurant name
        textViewOrderingFrom.text= restaurantName

        buttonPlaceOrder.setOnClickListener{
            saveOrder()
            val intent =Intent(this,PayPalActivity::class.java)
            intent.putExtra("totalBill", totalAmount.toString())
            startActivity(intent)

        }


        setToolBar()

        layoutManager = LinearLayoutManager(this)//set the layout manager

        recyclerView = findViewById(R.id.recyclerViewCart)


    }

    private fun fetchData(){

        if (ConnectionManager().checkConnectivity(this)) {

            activity_cart_Progressdialog.visibility=View.VISIBLE

            try {

                val queue = Volley.newRequestQueue(this)

                val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId"

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
                                    totalAmount += cartItemJsonObject.getString("cost_for_one").toString().toInt()
                                    cartListItems.add(menuObject)
                                }
                                //progressBar.visibility = View.GONE
                                menuAdapter = CartAdapter(
                                    this,//pass the relativelayout which has the button to enable it later
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
                            this,
                            "Error occurred : $it",
                            Toast.LENGTH_SHORT
                        ).show()
                        activity_cart_Progressdialog.visibility=View.INVISIBLE

                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-Type"] = "application/json"
                        headers["token"] = "c3b5e952c8e343"
                        return headers
                    }
                }

                queue.add(jsonObjectRequest)

            } catch (e: JSONException) {
                Toast.makeText(
                    this,
                    "error :$e",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
        else {

            val alterDialog=androidx.appcompat.app.AlertDialog.Builder(this)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Internet Connection can't be establish!")
            alterDialog.setPositiveButton("Open Settings"){text,listener->
                val settingsIntent= Intent(Settings.ACTION_SETTINGS)//open wifi settings
                startActivity(settingsIntent)
            }

            alterDialog.setNegativeButton("Exit"){ text,listener->
                finishAffinity()//closes all the instances of the app and the app closes completely
            }
            alterDialog.setCancelable(false)

            alterDialog.create()
            alterDialog.show()

        }

    }

    private fun setToolBar(){
        supportActionBar?.title="My Cart"
        supportActionBar?.setHomeButtonEnabled(true)//enables the button on the tool bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)//displays the icon on the button
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)//change icon to custom
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            android.R.id.home->{
                super.onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }



    override fun onResume() {

        if (ConnectionManager().checkConnectivity(this)) {
            fetchData()//if internet is available fetch data
        }else
        {

            val alterDialog=androidx.appcompat.app.AlertDialog.Builder(this)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Internet Connection can't be establish!")
            alterDialog.setPositiveButton("Open Settings"){text,listener->
                val settingsIntent= Intent(Settings.ACTION_SETTINGS)//open wifi settings
                startActivity(settingsIntent)
            }

            alterDialog.setNegativeButton("Exit"){ text,listener->
                finishAffinity()//closes all the instances of the app and the app closes completely
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

        val notificationBulider=NotificationCompat.Builder(this,channelId)
        notificationBulider.setSmallIcon(R.drawable.fud)
        notificationBulider.setContentTitle("Order Placed")
        notificationBulider.setContentText("Your order has been successfully placed!")
        notificationBulider.setStyle(NotificationCompat.BigTextStyle().bigText("Ordered from $restaurantName and amounting to Rs.$totalAmount"))
        notificationBulider.priority = NotificationCompat.PRIORITY_DEFAULT

        val notificationManagerCompat=NotificationManagerCompat.from(this)
        notificationManagerCompat.notify(notificationId,notificationBulider.build())

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)//less than oreo
        {
            val name ="Order Placed"
            val description="Your order has been successfully placed!"
            val importance=NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel=NotificationChannel(channelId,name,importance)
            notificationChannel.description=description
            val notificationManager=  (getSystemService(Context.NOTIFICATION_SERVICE)) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
    private fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }
    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveOrder(){
        val myRef = FirebaseDatabase.getInstance().getReference("Orders") // making reference for the object of profile
        val user = FirebaseAuth.getInstance().currentUser
        userId = user!!.uid
        val date = getCurrentDateTime()
        val dateInString = date.toString("yyyy/MM/dd HH:mm:ss")
        val orderId = myRef.push().key //generating random key
        val orderInfo = orderId?.let { OrderHistoryModel(it, restaurantName,
            totalAmount.toString(),dateInString, cartListItems)
        }
        if (orderId != null) {
            //set the taken information
            myRef.child(userId!!).child(orderId).setValue(orderInfo).addOnCompleteListener {
              //  Toast.makeText(this, "Your Order is placed successfully ", Toast.LENGTH_SHORT) .show()
            }
        }

        val newFragment: Fragment = OrderHistoryFragment()
        dataFromActivityToFragment = newFragment as DataFromActivityToFragment
        val ft =
            supportFragmentManager.beginTransaction()
        ft.replace(R.id.cartLayout, newFragment)
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        ft.addToBackStack(null)
        ft.commit()
        //pass crop name to CropRegCameraFragment fragment
        //pass crop name to CropRegCameraFragment fragment
        val handler = Handler()
        val r =
            Runnable { dataFromActivityToFragment!!.sendData(orderId.toString()) }
        handler.postDelayed(r, 5000)

    }


    interface DataFromActivityToFragment {
        fun sendData(data: String?)
    }
}

