package com.example.mobiletravel

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mobiletravel.databinding.ActivityMainBinding
import com.example.mobiletravel.viewmodel.ProfileViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView




class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        application

        val bottomNavBar: BottomNavigationView = binding.bottomNavigation
        val navController = supportFragmentManager.findFragmentById(R.id.customNavContainer)?.findNavController()


        bottomNavBar.setupWithNavController(navController!!)
    }

    override fun onStart() {
        super.onStart()
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101)
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.customNavContainer)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

}



