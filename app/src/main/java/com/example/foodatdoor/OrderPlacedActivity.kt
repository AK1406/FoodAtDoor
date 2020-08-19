package com.example.foodatdoor

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

lateinit var buttonOkay: Button
lateinit var orderSuccessfullyPlaced: RelativeLayout
lateinit var phone:TextView
lateinit var btnCall:ImageView

class OrderPlacedActivity : AppCompatActivity() {

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_placed)
        orderSuccessfullyPlaced=findViewById(R.id.orderSuccessfullyPlaced)
        buttonOkay=findViewById(R.id.buttonOkay)
        phone=findViewById(R.id.PhnNo)
        btnCall=findViewById(R.id.buttonPhnNo)

        val phnNo= mutableListOf<String>("9876543278","9867543789","9998765678", "9876589789","9777866567","9999976855"
        ,"9675467856","7292969489","7303456789")
        var no = phnNo.random()
        phone.text=no
        val animator:ObjectAnimator= ObjectAnimator.ofInt(
            btnCall,"backgroundColor",Color.BLUE,
            Color.RED, Color.GREEN

        );
        animator.duration = 500;
        animator.setEvaluator(ArgbEvaluator())
        animator.repeatMode = Animation.REVERSE
        animator.repeatCount = Animation.INFINITE
        animator.start()

        btnCall.setOnClickListener{
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$no")
            startActivity(intent)
        }
        buttonOkay.setOnClickListener(View.OnClickListener {
            val intent = Intent(this,HomePageActivity::class.java)
            startActivity(intent)
            finishAffinity()//finish all the activities
        })
    }

    override fun onBackPressed() {
        //force user to press okay button to take him to dashboard screen
        //user can't go back
    }

}
