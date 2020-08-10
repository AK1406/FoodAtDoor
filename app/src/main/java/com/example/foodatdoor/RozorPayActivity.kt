package com.example.foodatdoor


import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject


class RozorPayActivity : AppCompatActivity(), PaymentResultListener {

    private lateinit var  buttonPay:Button
    private lateinit var editTextAmount:TextView
    private lateinit var paymentAmount:String
    private var billAmount:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rozor_pay)


        buttonPay=findViewById(R.id.buttonPay)
        editTextAmount=findViewById((R.id.editTextAmount))

        billAmount = intent.getStringExtra("totalBill")
        editTextAmount.text=billAmount

        buttonPay.setOnClickListener{
            startPayment()
        }
    }



    private fun startPayment(){
        val activity: Activity = this
        val co = Checkout()
        try {
            val options = JSONObject()
            options.put("name", "Razorpay Corp")
            options.put("description", "Demoing Charges")
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://rzp-mobile.s3.amazonaws.com/images/rzp.png")
            options.put("currency", "INR")
            val payment = editTextAmount.text.toString()
            var total = payment.toDouble()
            total = total * 100
            options.put("amount", total)
            val preFill = JSONObject()
            preFill.put("email", "kumarianju.3346@gmail.com")
            preFill.put("contact", "9643720184")
            options.put("prefill", preFill)
            co.open(activity, options)
        } catch (e: java.lang.Exception) {
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(razorpayPaymentID: String) {
        Toast.makeText(this, "Payment successfully done! $razorpayPaymentID", Toast.LENGTH_SHORT)
            .show()
        val intent = Intent(this,OrderPlacedActivity::class.java)
        startActivity(intent)
    }

    override fun onPaymentError(code: Int, response: String?) {
        try {
          //  Toast.makeText(this, "Payment error please try again", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("OnPaymentError", "Exception in onPaymentError", e)
        }
    }
}