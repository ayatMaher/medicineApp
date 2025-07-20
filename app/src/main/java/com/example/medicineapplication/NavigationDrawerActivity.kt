package com.example.medicineapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.medicineapplication.databinding.ActivityNavigationDrawerBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

@Suppress("DEPRECATION")
class NavigationDrawerActivity : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityNavigationDrawerBinding
    private lateinit var navController: NavController

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNavigationDrawerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.nav_host_fragment_activity_main)
        binding.navView.setupWithNavController(navController)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        TODO("Not yet implemented")
    }

}