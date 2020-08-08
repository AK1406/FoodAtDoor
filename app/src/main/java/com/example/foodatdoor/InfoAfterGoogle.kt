package com.example.foodatdoor

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*


/***  InfoAfterGoogle (class) for authentication of user and taking information from user ***/
class InfoAfterGoogle : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var submitBtn: Button
    private lateinit var name: EditText
    private lateinit var address: EditText
    private lateinit var pinCode: EditText
    private lateinit var phnNo: EditText
    private lateinit var emailEt: TextView

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        val isGoogleSignIn = sharedPreferences.getBoolean("isGoogleSignIn", false)

        setContentView(R.layout.information_after_google)

/**To check whether user submitted his/her information or not according to which will be directed to home page**/
        if (isGoogleSignIn) {
            val intent = Intent(this@InfoAfterGoogle, HomePageActivity::class.java)
            startActivity(intent)
            finish()
        }
        val user = FirebaseAuth.getInstance().currentUser

        /*** making reference for view groups of signUp layout ***/
        name = findViewById(R.id.name)
        address = findViewById(R.id.address)
        pinCode = findViewById(R.id.pin)
        phnNo = findViewById(R.id.phnNo)
        emailEt = findViewById(R.id.email_edt_text)
        submitBtn = findViewById(R.id.submitBtn)

        emailEt.text=user?.email

        auth = FirebaseAuth.getInstance() // making instance of firebaseAuth
        submitBtn.setOnClickListener {
            /**Taking information from user and assign them to variables to hold them **/
            val name: String = name.text.toString().trim()
            val address: String = address.text.toString()
            val pinCode: String = pinCode.text.toString()
            val phnNo: String = phnNo.text.toString()

            if (name.isEmpty() || address.isEmpty() || pinCode.isEmpty() || phnNo.isEmpty()) { //checking email and password not to be empty
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_LONG).show()
            } else {
                if (phnNo.length != 10) {  // checking no. of digits in phn no.
                        Toast.makeText(this, "Phone no. is incorrect", Toast.LENGTH_LONG).show()
                    }
                    if (pinCode.length != 6) {   // checking no. of digits in pin code
                        Toast.makeText(this, "Pin code must be of 6 digit", Toast.LENGTH_LONG).show()
                    }
                }

            Toast.makeText(this, "Successfully Registered", Toast.LENGTH_LONG).show()
            /**calling saveInfo (function) to save information of user to database**/
            savePreferences()
            saveInfo(name, address,pinCode, phnNo) //passing registered email id and dob
        }
    }


    /** save user information to profile Database **/
    private fun saveInfo(name: String, address: String, pinCode: String, phnNo: String) {
        val emailId: String

        val myRef = FirebaseDatabase.getInstance().getReference("profile") // making reference for the object of profile
        val user = FirebaseAuth.getInstance().currentUser
        emailId = user?.email.toString()
        val userId: String? = user?.uid
        val subEmail = emailId.substringBefore("@")  //abc123@gmail.com -> abc123(substring of email id)
        val profileId = myRef.push().key //generating random key
        val profileInfo = profileId?.let { ProfileModel(subEmail, name, phnNo, address, pinCode, emailId) } //passing taken information to a class constructor of ProfileModel
        if (profileId != null) {
            //set the taken information
            myRef.child(userId!!).setValue(profileInfo).addOnCompleteListener {
                  Toast.makeText(this, "Your profile is saved successfully ", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@InfoAfterGoogle, HomePageActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

    }
    /** When user submit information once will be directed to home page always **/
    private fun savePreferences() {
        sharedPreferences.edit().putBoolean("isGoogleSignIn", true).apply()
    }
}