package com.example.foodatdoor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Constraints
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

lateinit var buttonOkay: Button
lateinit var orderSuccessfullyPlaced: ConstraintLayout

class OrderPlacedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_placed)
        orderSuccessfullyPlaced=findViewById(R.id.orderPlaceLayout)
        buttonOkay=findViewById(R.id.buttonOkay)

        buttonOkay.setOnClickListener{
            val myFragment: Fragment = DashboardFragment.newInstance()
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.orderPlaceLayout, myFragment)
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    override fun onBackPressed() {
        //force user to press okay button to take him to dashboard screen
        //user can't go back
    }

}
