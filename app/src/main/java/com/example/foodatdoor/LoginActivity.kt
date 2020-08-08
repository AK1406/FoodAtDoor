package com.example.foodatdoor

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() , View.OnClickListener{

    private  var auth: FirebaseAuth?=null
    lateinit var gso: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var emailEt: EditText
    private lateinit var passwordEt: EditText
    private lateinit var signUpBtn: Button
    private lateinit var loginBtn: Button
    private lateinit var hidePass: ImageView
    private lateinit var showPass: ImageView

    private lateinit var resetPasswordTv: TextView

    companion object {
        private val TAG = "GoogleLogin"
        private val RC_SIGN_IN = 9001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        /**Google sign in **/
        //  val signIn = findViewById<View>(R.id.google_login_btn) as Button
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) //web client id containing api
            .requestEmail()
            .build()
        this.mGoogleSignInClient = GoogleSignIn.getClient(this, gso) // passing google sign in option to client

        google_login_btn.setOnClickListener(this)

        FirebaseApp.initializeApp(this) //initializing firebase app
        /**getting instance of firebase auth so that we can compare email and password to login**/
        auth = FirebaseAuth.getInstance()


        emailEt = findViewById(R.id.email_edt_text)
        passwordEt = findViewById(R.id.pass_edt_text)

        signUpBtn = findViewById(R.id.signup_btn)
        loginBtn = findViewById(R.id.login_btn)

        resetPasswordTv = findViewById(R.id.reset_pass_tv)
        hidePass = findViewById(R.id.HideBtn)
        hidePass.visibility = View.VISIBLE
        showPass = findViewById(R.id.showBtn)
        showPass.visibility = View.INVISIBLE

        hidePass.setOnClickListener {
            hidePass.visibility = View.INVISIBLE
            showPass.visibility = View.VISIBLE
            pass_edt_text.transformationMethod = HideReturnsTransformationMethod.getInstance()

        }
        showPass.setOnClickListener {
            showPass.visibility = View.INVISIBLE
            hidePass.visibility = View.VISIBLE
            pass_edt_text.transformationMethod = PasswordTransformationMethod.getInstance()
        }

        loginBtn.setOnClickListener {
            //taking email & password from user
            val email: String = emailEt.text.toString()
            val password: String = passwordEt.text.toString()

            if (TextUtils.isEmpty(email)) { //checking email & password not to be empty
                emailEt.error = "Email Required"
                emailEt.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(password)) { //checking email & password not to be empty
                passwordEt.error = "Password Required"
                passwordEt.requestFocus()
                return@setOnClickListener
            }

            auth!!.signInWithEmailAndPassword(email, password) //passing email and password to sign in with email and password so that it can compare them with registered one
                .addOnCompleteListener(this, OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, HomePageActivity::class.java) //navigate to homepage
                        startActivity(intent)
                        finish()

                    } else {
                        Toast.makeText(
                            this,
                            "Login Failed, incorrect email & password",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })

        }
        /**go to sign up page if user is using app for 1st time**/
        signUpBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        /**to reset password if forgotten **/
        resetPasswordTv.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

    }


    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth!!.currentUser
        if (currentUser != null) {
            Log.d(TAG, "Currently Signed in: " + currentUser.email!!)
            Toast.makeText(this@LoginActivity, "Currently Logged in: " + currentUser.email!!, Toast.LENGTH_LONG).show()
        }
    }
    override fun onClick(v: View) {
        when (v.id) {
            google_login_btn.id -> signIn()
        }
    }

    private fun signIn() {  //function for google sign in
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    /** intent after login**/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
                Toast.makeText(this,"Google SignIn Successful",Toast.LENGTH_LONG).show()
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google Sign in Failed $e", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**authentication for google signin **/
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth!!.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth!!.currentUser
                    val intent = Intent(this, InfoAfterGoogle::class.java)
                    startActivity(intent)
                    Toast.makeText(
                        this@LoginActivity,
                        "Authentication successful :" + user!!.email,
                        Toast.LENGTH_LONG
                    ).show()


                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Authentication failed:" + task.exception!!,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

}