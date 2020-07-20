package com.example.foodatdoor

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.view.*


/*** SignUpActivity (class) for authentication of user and taking information from user ***/
class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var signUpBtn: Button
    private lateinit var loginBtn: TextView

    private lateinit var name: EditText
    private lateinit var phnNo: EditText
    private lateinit var emailEt: EditText
    private lateinit var passwordEt: EditText
    private lateinit var confirmPassword:EditText
    private lateinit var Daddress:EditText

    private var userId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        FirebaseApp.initializeApp(this)

        /*** making reference for view groups of signUp layout ***/
        name = findViewById(R.id.name)
        phnNo = findViewById(R.id.phnNo)
        emailEt = findViewById(R.id.email_edt_text)
        passwordEt = findViewById(R.id.pass_edt_text)
        confirmPassword=findViewById(R.id.confirm_pass_edt_text)
        loginBtn = findViewById(R.id.login_link)
        signUpBtn = findViewById(R.id.signup_btn)
        Daddress=findViewById(R.id.address)


        /*** Authentication Part to authenticate user with his/her email id and password ***/

        auth = FirebaseAuth.getInstance() // making instance of firebaseAuth
        signUpBtn.setOnClickListener {
            /**Taking information from user and assign them to variables to hold them **/
            val name: String = name.text.toString().trim()
            val phnNo: String = phnNo.text.toString()
            val email: String = emailEt.text.toString()
            val password: String = passwordEt.text.toString()
            val confirmPass:String=confirmPassword.text.toString()
            val deliveryAddress:String=Daddress.text.toString()
            if (name.isEmpty() || phnNo.isEmpty()||TextUtils.isEmpty(email) || TextUtils.isEmpty(password)
                ||confirmPass.isEmpty()||deliveryAddress.isEmpty()) { //checking email and password not to be empty
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_LONG).show()
            } else {
                if (password.length < 6) { //password should be of atleast 6 characters
                    Toast.makeText(this, "Password too short! , enter minimum 6 characters", Toast.LENGTH_LONG).show()
                    if (phnNo.length != 10) {  // checking no. of digits in phn no.
                        Toast.makeText(this, "Phone no. is incorrect", Toast.LENGTH_LONG).show()
                    }
                    if(password == confirmPass){
                        Toast.makeText(this,"Password is incorrect!",Toast.LENGTH_LONG).show()
                    }
                }
                auth.createUserWithEmailAndPassword(email, password) //create instance/object with entered email & password
                    .addOnCompleteListener(this, OnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Successfully Registered", Toast.LENGTH_LONG).show()
                            /**calling saveInfo (function) to save information of user to database**/
                            saveInfo(name,phnNo,deliveryAddress) //passing registered email id and dob

                        } else {
                            Toast.makeText(this, "Registration Failed", Toast.LENGTH_LONG).show()
                        }
                    })
            }

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Verification!")
            builder.setMessage("Verify Your Phone No.")
            builder.setIcon(android.R.drawable.ic_dialog_alert)
            builder.setPositiveButton("OK") { dialog, which ->
                val intent =Intent(this,VerifyPhone::class.java)
                startActivity(intent)
            }

            builder.show()

        }
        /** Go back to login page **/
        loginBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    /** save user information to profile Database **/

    private fun saveInfo(name:String,phnNo:String,adr:String) {
        val emailId: String
        val myRef = FirebaseDatabase.getInstance().getReference("profile") // making reference for the object of profile
        val user = FirebaseAuth.getInstance().currentUser
        // add username, email to database
        userId = user!!.uid
        emailId = user.email.toString()
        val subEmail = emailId.substringBefore("@")  //abc123@gmail.com -> abc123(substring of email id)
        val profileId = myRef.push().key //generating random key
        val profileInfo = profileId?.let { ProfileModel(subEmail,name,phnNo,adr,emailId)
        }//passing taken information to a class constructor of ProfileModel
        if (profileId != null) {
            //set the taken information
            myRef.child(userId!!).setValue(profileInfo).addOnCompleteListener {
                Toast.makeText(this, "Your profile is saved successfully ", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

}