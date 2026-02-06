package com.example.carparkingsystem


import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.carparkingsystem.databinding.ActivityMainBinding



class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // binding view inflate
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        // Find the NavHostFragment and NavController
        navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController


//------------************-----------

        // Check "Remember Me" in SharedPreferences
//        val sharedPref = getSharedPreferences("login_prefs", MODE_PRIVATE)
//        val remember = sharedPref.getBoolean("remember_me", false)
//
//        if (remember) {
//            // Navigate to SpotsFragment
//            navController.navigate(R.id.spotsFragment)
//        }
//        else {
//            // Navigate to LoginFragment
//            navController.navigate(R.id.getStartFragment)
//        }

//------------***********-----------------



        // menu item handling
        binding.bottomMenu.setOnItemSelectedListener { i ->
            onOptionsItemSelected(i)
        }

    }


    // Handle menu item clicks
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.profile -> {
                // Navigate to Profile Fragment
                navController.navigate(R.id.profileFragment)
                true
            }

            R.id.home -> {
                // Navigate to Home Fragment
                navController.navigate(R.id.spotsFragment)
                true
            }

            R.id.history -> {
                // Navigate to Exercises Fragment
                navController.navigate(R.id.historyFragment)

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onBackPressed() {
         if (!navController.popBackStack()) {
            // If the back stack is empty, exit the app
            super.onBackPressed()
        }
    }

}