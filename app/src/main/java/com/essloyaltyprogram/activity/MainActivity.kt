package com.essloyaltyprogram.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.helper.widget.Grid
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.essloyaltyprogram.R
import com.essloyaltyprogram.adapter.HomeAdapter
import com.essloyaltyprogram.dataClasses.HomeItems
import com.essloyaltyprogram.databinding.ActivityMainBinding
import com.essloyaltyprogram.fragment.HomeFragment
import com.essloyaltyprogram.fragment.TransactionFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var toggle: ActionBarDrawerToggle
    private var bottomNavPosition = 0
    private var exitPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.open,
            R.string.close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Handle Navigation Item Clicks
        loadFragment(HomeFragment())

        binding.bottomNavigation.setOnItemSelectedListener { item ->

            if (item.itemId == R.id.home && bottomNavPosition != 0) {
                loadFragment(HomeFragment())
                bottomNavPosition = 0
            }
            if (item.itemId == R.id.scan && bottomNavPosition != 1) {
                //open camera
            }
            if (item.itemId == R.id.transactions && bottomNavPosition != 2) {
                loadFragment(TransactionFragment())
                bottomNavPosition = 2
            }
            true
        }

    }
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.bell -> {
                startActivity(Intent(this, NotificationActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }
    override fun onBackPressed() {

        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack() // Go back to previous fragment
        } else {
            if (exitPosition == 0){
                Toast.makeText(
                    this,
                    "Press again to exit",
                    Toast.LENGTH_SHORT
                ).show()
                exitPosition++
                Handler(Looper.getMainLooper()).postDelayed({
                    exitPosition = 0
                },1500)
            }else {
                super.onBackPressed()
            }

        }
    }
}