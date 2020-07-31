package com.example.foodatdoor


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

import kotlin.Comparator
import kotlin.collections.HashMap

class DescriptionFragment : Fragment() {

    lateinit var recyclerMenu: RecyclerView

    lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var recyclerAdapter: DescriptionRecyclerAdapter

    lateinit var progressLayout: RelativeLayout

    lateinit var progressBar: ProgressBar

    lateinit var txtDishName: TextView
    lateinit var txtDishRating: TextView
    lateinit var imgDishImage: ImageView
    lateinit var btnAddToCart: Button

    lateinit var toolbar: Toolbar

    var restId: String? = "100"

    companion object {
        fun newInstance(): Fragment {
            return DescriptionFragment()
        }
    }

    val menuInfoList = arrayListOf<Restaurant>()

    var ratingComparator = Comparator<Restaurant>{rest1, rest2 ->

        if (rest1.restRating.compareTo(rest2.restRating, true) == 0) {
            // sort according to name if rating is same
            rest1.restName.compareTo(rest2.restName, true)
        } else {
            rest1.restRating.compareTo(rest2.restRating, true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        restId = requireArguments().getString("rest_id")
        val view = inflater.inflate(R.layout.fragment_description, container, false)

        recyclerMenu = view.findViewById(R.id.recyclerDashboard)

        progressLayout = view.findViewById(R.id.progressLayout)

        progressBar = view.findViewById(R.id.progressBar)

        progressLayout.visibility = View.VISIBLE

        layoutManager = LinearLayoutManager(activity)



        if (restId == "100") {
            Toast.makeText(
                context,
                "Some unexpected error occurred!",
                Toast.LENGTH_SHORT
            ).show()
        }


        val queue = Volley.newRequestQueue(activity as Context)

        val url ="http://13.235.250.119/v2/restaurants/fetch_result/id"

        val jsonParams = JSONObject()
        jsonParams.put("rest_id", restId)

        if (ConnectionManager().checkConnectivity(activity as Context)){
            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener {

                    // Here we will handle the response
                    try {
                        progressLayout.visibility = View.GONE
                        val data1 = it.getJSONObject("data")
                        val success = data1.getBoolean("success")

                        if (success){

                            val data = data1.getJSONArray("data")
                            for (i in 0 until data.length()){
                                val menuJsonObject = data.getJSONObject(i)
                                val menuObject = Restaurant(
                                    menuJsonObject.getString("id"),
                                    menuJsonObject.getString("name"),
                                    menuJsonObject.getString("rating"),
                                    menuJsonObject.getString("cost_for_one"),
                                    menuJsonObject.getString("image_url")
                                )
                                menuInfoList.add(menuObject)
                                recyclerAdapter = DescriptionRecyclerAdapter(activity as Context, menuInfoList)

                                recyclerMenu.adapter = recyclerAdapter

                                recyclerMenu.layoutManager = layoutManager

                            }

                        } else {
                            Toast.makeText(activity as Context, "Some Error Occurred!", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(activity as Context, "Some unexpected error occurred! $e", Toast.LENGTH_SHORT).show()
                    }

                }, Response.ErrorListener {

                    //Here we will handle the errors
                    if (activity != null){
                        Toast.makeText(activity as Context, "Volley error occurred!", Toast.LENGTH_SHORT).show()
                    }

                }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    headers["token"] = "c3b5e952c8e343"  //c3b5e952c8e343
                    return headers
                }
            }


            queue.add(jsonObjectRequest)

        } else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings"){text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }

            dialog.setNegativeButton("Exit") {text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }

        return view
    }

}
