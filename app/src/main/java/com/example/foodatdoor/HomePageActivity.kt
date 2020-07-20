package com.example.foodatdoor

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_home_page.*
import kotlinx.android.synthetic.main.nav_header_home_page.view.*

/**navigation Drawer and Main Page of app**/
class HomePageActivity : AppCompatActivity() {

    private var navigationPosition: Int = 0
    private val DEFAULT_IMAGE_URL = "https://picsum.photos/200"
    private lateinit var imageUri: Uri
    private val REQUEST_IMAGE_CAPTURE = 100

    private val user = FirebaseAuth.getInstance().currentUser
    private lateinit var myRef: DatabaseReference
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        initView()
    }

    private fun initView() {
        setSupportActionBar(toolbar)   //set toolbar
        setUpDrawerLayout()  //calling function to set drawer layout
        //bottom menu bar
        val bottomNavigation: BottomNavigationView = findViewById(R.id.navigationView)

        //Load Home fragment first
        navigationPosition = R.id.dashboard
        navigateToFragment(DashboardFragment.newInstance())
        nav_view.setCheckedItem(navigationPosition)
        toolbar.title = "Dashboard"

        /**Listener for Select different options from the drawer**/
        nav_view.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.dashboard -> {
                    toolbar.title = "Dashboard"
                    navigationPosition = R.id.dashboard
                    navigateToFragment(DashboardFragment.newInstance()) //navigate to fragment
                }
                R.id.profile -> {
                    toolbar.title = getString(R.string.profile)
                    navigationPosition = R.id.profile
                    navigateToFragment(ProfileFragment.newInstance())
                }
                R.id.faq-> {
                    toolbar.title ="FAQ"
                    navigationPosition = R.id.faq
                    navigateToFragment(FaqFragment.newInstance())
                }
                R.id.aboutApp -> {
                    toolbar.title = "About"
                    navigationPosition =R.id.aboutApp
                    navigateToFragment(AboutAppFragment.newInstance())
                }
                R.id.share -> {
                    toolbar.title = "Share"
                    navigationPosition =R.id.share
                    //navigateToFragment(AboutAppFragment.newInstance())
                }
                R.id.rate -> {
                    toolbar.title = "Rate"
                    navigationPosition =R.id.rate
                    //navigateToFragment(AboutAppFragment.newInstance())
                }
                R.id.updatePassword -> {
                    toolbar.title = getString(R.string.update_password)
                    navigationPosition = R.id.updatePassword
                    val intent = Intent(this,UpdatePasswordActivity::class.java) //navigate to Activity
                    startActivity(intent)
                }
                R.id.logout -> {
                    toolbar.title = getString(R.string.login)
                    navigationPosition = R.id.logout
                    // navigateToFragment(SettingsFragment.newInstance())
                    val intent = Intent(this,LoginActivity::class.java)
                    startActivity(intent)
                }

            }
            // set item as selected to persist highlight
            menuItem.isChecked = true
            // close drawer when item is tapped
            drawer_layout.closeDrawers()
            true
        }
/*
        //bottom menu bar listener
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_cart -> {
                    toolbar.title = "My Cart"
                    navigationPosition =R.id.navigation_cart
                    navigateToFragment(CartFragment.newInstance())
                }
                R.id.navigation_my_orders-> {
                    toolbar.title = "My Orders"
                    navigationPosition =R.id.navigation_my_orders
                    navigateToFragment(MyOrderFragment.newInstance())
                }
                R.id.navigation_favourites -> {
                    toolbar.title = "My Favourites"
                    navigationPosition =R.id.navigation_favourites
                    navigateToFragment(FavouritesFragment.newInstance())
                }
            }
            false
        }*/

        //Change navigation header information
        changeNavigationHeaderInfo()

        drawer_layout.addDrawerListener(object: DrawerLayout.DrawerListener{
            override fun onDrawerStateChanged(p0: Int) {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDrawerSlide(p0: View, p1: Float) {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDrawerClosed(p0: View) {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDrawerOpened(p0: View) {
                //To change body of created functions use File | Settings | File Templates.
            }
        })

    }

    private fun changeNavigationHeaderInfo() {
        val headerView = nav_view.getHeaderView(0)
        headerView.UserEmail.text = user?.email.toString() //retrieve email of user
        //retrieve name
        myRef = FirebaseDatabase.getInstance().getReference("profile")
        userId = user?.uid
        // User data change listener
        myRef.child(userId!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userInfo = dataSnapshot.getValue(ProfileModel::class.java)
                headerView.UsersName.text = userInfo?.name.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                //  Log.e(ProfileFragment.TAG, "Failed to read user", error.toException())
            }
        })
        /*  val baos = ByteArrayOutputStream()
          val storageRef = FirebaseStorage.getInstance()
                  .reference
                  .child("pics/${FirebaseAuth.getInstance().currentUser?.uid}")
          val bitmap:Bitmap?=null
          bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
          storageRef.downloadUrl.addOnCompleteListener { urlTask ->
              urlTask.result?.let {
                  imageUri = it
                  //   Toast.makeText(activity,imageUri.toString(),Toast.LENGTH_LONG).show()
                  headerView.UserPic.setImageBitmap(bitmap)
              }
          }*/

    }

    //define function to set drawer layout
    private fun setUpDrawerLayout() {
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.drawerOpen, R.string.drawerClose)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
    }
    //function used when there is a need to navigate to a fragment
    private fun navigateToFragment(fragmentToNavigate: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragmentToNavigate)
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    //Back to Home Fragment directly from any fragment when back button of phn is pressed

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(Gravity.RIGHT)) {
            drawer_layout.closeDrawer(Gravity.LEFT)
        }
        if (navigationPosition == R.id.dashboard) {
            finish()
        } else {
            //Navigate to Home Fragment
            navigationPosition = R.id.dashboard
            navigateToFragment(DashboardFragment.newInstance())
            nav_view.setCheckedItem(navigationPosition)
            toolbar.title = "Dashboard"
        }
    }

}