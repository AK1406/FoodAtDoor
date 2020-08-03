package com.example.foodatdoor


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_cart.*
import org.json.JSONException
import org.json.JSONObject

class DescriptionActivity : AppCompatActivity() {

    lateinit var recyclerMenu: RecyclerView

    lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var recyclerAdapter: DescriptionRecyclerAdapter

    lateinit var progressLayout: RelativeLayout

    lateinit var progressBar: ProgressBar

    lateinit var addToCartBtn:Button

    lateinit var restId: String
    lateinit var restName:String
    val menuInfoList = arrayListOf<Dish>()
   // lateinit var  proceedToCartLayout : RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        recyclerMenu = findViewById(R.id.recyclerMenu)

        progressLayout = findViewById(R.id.progressLayout)

        progressBar = findViewById(R.id.progressBar)

        progressLayout.visibility = View.VISIBLE

        addToCartBtn = findViewById(R.id.btn_cart)

//         proceedToCartLayout=findViewById(R.id.relativeLayoutProceedToCart)

        layoutManager = LinearLayoutManager(this)

        if (intent != null) {
            restId = intent.getStringExtra("restaurant_id")
            restName=intent.getStringExtra("restaurant_name")
        } else {
            finish()
            Toast.makeText(
                this@DescriptionActivity,
                "Some unexpected error occurred!",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

        // val jsonParams = JSONObject()
        // jsonParams.put("restaurant_id", restId.toString())

        fun fetchData(){
        if (ConnectionManager().checkConnectivity(this@DescriptionActivity)) {


            val queue = Volley.newRequestQueue(this@DescriptionActivity)

            val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restId"

            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.GET, url, null, Response.Listener {

                    // Here we will handle the response
                    try {
                        progressLayout.visibility = View.GONE
                        val data1 = it.getJSONObject("data")
                        Log.d("Data", data1.toString())
                        val success = data1.getBoolean("success")

                        if (success) {

                            val data = data1.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val menuJsonObject = data.getJSONObject(i)
                                val menuObject = Dish(
                                    menuJsonObject.getString("id"),
                                    menuJsonObject.getString("name"),
                                    menuJsonObject.getString("cost_for_one")
                                )
                                menuInfoList.add(menuObject)

                                recyclerAdapter = DescriptionRecyclerAdapter(       this,
                                    restId,//pass the restaurant Id
                                    restName,//pass restaurantName //
                                    addToCartBtn,
                                    menuInfoList)

                                recyclerMenu.adapter = recyclerAdapter

                                recyclerMenu.layoutManager = layoutManager

                            }

                        } else {
                            Toast.makeText(this, "Some Error Occurred!", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            this,
                            "Some unexpected error occurred! $e",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }, Response.ErrorListener {

                    //Here we will handle the errors
                    Toast.makeText(this, "Volley error occurred! $it", Toast.LENGTH_SHORT).show()

                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    headers["token"] = "c3b5e952c8e343"  //c3b5e952c8e343
                    return headers
                }
            }


            queue.add(jsonObjectRequest)

        } else {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }

            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this)
            }
            dialog.create()
            dialog.show()
        }
    }

    override fun onBackPressed() {


        if(recyclerAdapter.getSelectedItemCount()>0) {


            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
            alterDialog.setTitle("Alert!")
            alterDialog.setMessage("Going back will remove everything from cart")
            alterDialog.setPositiveButton("Okay") { text, listener ->
                super.onBackPressed()
            }
            alterDialog.setNegativeButton("No") { text, listener ->

            }
            alterDialog.show()
        }else{
            super.onBackPressed()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id=item.itemId

        when(id){
            android.R.id.home->{
                if(recyclerAdapter.getSelectedItemCount()>0) {

                    val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
                    alterDialog.setTitle("Alert!")
                    alterDialog.setMessage("Going back will remove everything from cart")
                    alterDialog.setPositiveButton("Okay") { text, listener ->
                        super.onBackPressed()
                    }
                    alterDialog.setNegativeButton("No") { text, listener ->

                    }
                    alterDialog.show()
                }else{
                    super.onBackPressed()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onResume() {

        if (ConnectionManager().checkConnectivity(this)) {
            if(menuInfoList.isEmpty())
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

}