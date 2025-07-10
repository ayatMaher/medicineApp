package com.example.medicineapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.medicineapplication.databinding.ActivityNavigationDrawerBinding
import com.google.android.material.navigation.NavigationView

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

//        val navView: BottomNavigationView = binding.navView
//
//         navController = findNavController(R.id.nav_host_fragment_activity_main)
//        navView.setupWithNavController(navController)
//
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        val result = super.onCreateOptionsMenu(menu)
//        val navView: NavigationView? = findViewById(R.id.nav_view)
//        if (navView == null) {
//
//            menuInflater.inflate(R.menu.bottom_nav_menu, menu)
//            Log.e("TAG", "${menu?.get(0)}")
//        }
//        return result
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.navigation_home -> {
//                Log.e("TAG", "onOptionsItemSelected: ")
//                val navController =
//                    findNavController(R.id.nav_host_fragment_activity_main)
//                navController.navigate(R.id.navigation_home)
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }
//
//    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
//        when (menuItem.itemId) {
//            R.id.navigation_home -> {
//                navController.navigate(R.id.navigation_home)
//            }
//
//            R.id.navigation_medicines -> {
//                navController.navigate(R.id.navigation_medicines)
//            }
//
//            R.id.navigation_pharmacies -> {
//                navController.navigate(R.id.navigation_pharmacies)
//            }
//
//        }
//        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        TODO("Not yet implemented")
    }

}