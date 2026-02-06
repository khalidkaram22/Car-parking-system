package com.example.carparkingsystem

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.carparkingsystem.databinding.ActivityLoginMainBinding


class LoginMainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

    private lateinit var binding: ActivityLoginMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityLoginMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        // Find the NavHostFragment and NavController
        navHostFragment = supportFragmentManager
            .findFragmentById(R.id.login_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController


        // Check "Remember Me" in SharedPreferences
        val sharedPref = getSharedPreferences("login_prefs", MODE_PRIVATE)
        val remember = sharedPref.getBoolean("remember_me", false)

        if (remember) {
            // Navigate to SpotsFragment
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
            finish()
        }





    }



    override fun onBackPressed() {
        if (!navController.popBackStack()) {
            // If the back stack is empty, exit the app
            super.onBackPressed()
        }
    }
}