package com.example.foodatdoor

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.paypal.android.sdk.payments.*
import org.json.JSONException
import java.math.BigDecimal


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class PayPalActivity : AppCompatActivity() {

    private lateinit var  buttonPay:Button
    private lateinit var editTextAmount:TextView
    private lateinit var paymentAmount:String

    private var billAmount:String=""
    //Paypal intent request code to track onActivityResult method
    private val PAYPAL_REQUEST_CODE = 123

    //Paypal Configuration Object
    private val config = PayPalConfiguration() // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(PayPalConfig.PAYPAL_CLIENT_ID)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay_pal)

        buttonPay=findViewById(R.id.buttonPay)
        editTextAmount=findViewById((R.id.editTextAmount))



        billAmount = intent.getStringExtra("totalBill")
        editTextAmount.text= billAmount

        buttonPay.setOnClickListener{
            getPayment()
        }

        val intent = Intent(this, PayPalService::class.java)
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
        startService(intent)

    }

    override fun onDestroy() {
        stopService(Intent(this, PayPalService::class.java))
        super.onDestroy()
    }

    private fun getPayment() {
        //Getting the amount from editText
        paymentAmount = editTextAmount.text.toString()

        //Creating a paypalpayment
        val payment = PayPalPayment(
            BigDecimal(paymentAmount), "USD", "Total Order Bill",
            PayPalPayment.PAYMENT_INTENT_SALE
        )

        //Creating Paypal Payment activity intent
        val intent = Intent(this, PaymentActivity::class.java)

        //putting the paypal configuration to the intent
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)

        //Puting paypal payment to the intent
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment)

        //Starting the intent activity for result
        //the request code will be used on the method onActivityResult
        startActivityForResult(intent, PAYPAL_REQUEST_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //If the result is from paypal
        if (requestCode == PAYPAL_REQUEST_CODE) {

            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {
                //Getting the payment confirmation
                val confirm: PaymentConfirmation =
                    data!!.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION)

                //if confirmation is not null
                if (confirm != null) {
                    try {
                        //Getting the payment details
                        val paymentDetails = confirm.toJSONObject().toString(4)
                        Log.i("paymentExample", paymentDetails)

                        //Starting a new activity for the payment details and also putting the payment details with intent
                        startActivity(
                            Intent(this, ConfirmationActivity::class.java)
                                .putExtra("PaymentDetails", paymentDetails)
                                .putExtra("PaymentAmount", paymentAmount)
                        )
                    } catch (e: JSONException) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e)
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.")
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                    "paymentExample",
                    "An invalid Payment or PayPalConfiguration was submitted. Please see the docs."
                )
            }
        }
    }

}