package com.example.foodatdoor


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.recycler_dashboard_single_row.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

class DashboardFragment : Fragment() {

    lateinit var recyclerDashboard: RecyclerView

    lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var recyclerAdapter: DashboardRecyclerAdapter

    lateinit var progressLayout: RelativeLayout

    lateinit var progressBar: ProgressBar




    companion object {
        fun newInstance(): Fragment {
            return DashboardFragment()
        }
    }

    val restInfoList = arrayListOf<Restaurant>()

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

        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        setHasOptionsMenu(true)

        recyclerDashboard = view.findViewById(R.id.recyclerDashboard)

        progressLayout = view.findViewById(R.id.progressLayout)

        progressBar = view.findViewById(R.id.progressBar)

        progressLayout.visibility = View.VISIBLE

        layoutManager = LinearLayoutManager(activity)

        val fav=view.findViewById<ImageView>(R.id.btnAddToFav)


        val queue = Volley.newRequestQueue(activity as Context)

        val url ="http://13.235.250.119/v2/restaurants/fetch_result/"

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
                                val restJsonObject = data.getJSONObject(i)
                                val restObject = Restaurant(
                                    restJsonObject.getString("id"),
                                    restJsonObject.getString("name"),
                                    restJsonObject.getString("rating"),
                                    restJsonObject.getString("cost_for_one"),
                                    restJsonObject.getString("image_url")
                                )
                                restInfoList.add(restObject)
                                recyclerAdapter = DashboardRecyclerAdapter(activity as Context, restInfoList)

                                recyclerDashboard.adapter = recyclerAdapter

                                recyclerDashboard.layoutManager = layoutManager
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


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_dashboard, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if (id == R.id.action_sort){
            Collections.sort(restInfoList, ratingComparator)
            restInfoList.reverse()
        }

        recyclerAdapter.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }


    class DBAsyncTask(val context: Context, val restEntity: RestEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        /*
        Mode 1 -> Check DB if the restaurant is favourite or not
        Mode 2 -> Save the restaurant into DB as favourite
        Mode 3 -> Remove the favourite restaurant
        * */

        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "rest-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {

            when (mode) {

                1 -> {

                    // Check DB if the rest is favourite or not
                    val rest: RestEntity? = db.restDao().getRestById(restEntity.rest_id.toString())
                    db.close()
                    return rest != null

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

    }
}
