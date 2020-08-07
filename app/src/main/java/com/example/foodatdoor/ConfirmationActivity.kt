package com.example.foodatdoor



import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONException
import org.json.JSONObject


class ConfirmationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)

        /*done.setOnClickListener{
            val intent=Intent(this,OrderPlacedActivity::class.java)
            startActivity(intent)
        }*/
       //Getting Intent
                val intent = intent;
        try {
             val jsonDetails :JSONObject= JSONObject(intent.getStringExtra("PaymentDetails")!!);

            //Displaying payment details
            showDetails(jsonDetails.getJSONObject("response"), intent.getStringExtra("PaymentAmount")!!);
        } catch (e:JSONException) {
            Toast.makeText(this, "$e", Toast.LENGTH_SHORT).show();
        }
    }

    @Throws(JSONException::class)
    private fun showDetails(
        jsonDetails: JSONObject,
        paymentAmount: String
    ) {
        //Views
        val textViewId:TextView = findViewById(R.id.paymentId)
        val textViewStatus:TextView = findViewById(R.id.paymentStatus)
        val textViewAmount:TextView = findViewById(R.id.paymentAmount)

        //Showing the details from json object
        textViewId.text = jsonDetails.getString("id")
        textViewStatus.text = jsonDetails.getString("state")
        textViewAmount.text = "$paymentAmount USD"
    }
}