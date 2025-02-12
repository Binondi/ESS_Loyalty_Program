package com.essloyaltyprogram.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.essloyaltyprogram.dataClasses.Users
import com.essloyaltyprogram.databinding.ActivitySplashBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val users = FirebaseDatabase.getInstance().reference.child("users")
    private lateinit var sp : SharedPreferences
    private var isUserExists = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sp = getSharedPreferences("users", MODE_PRIVATE)
        checkUserExistance()
    }

    private fun checkUserExistance() {
        if (sp.contains("phone_no")){
            if (sp.getString("phone_no","") != ""){
                users.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (data in snapshot.children){
                            val user = data.getValue(Users::class.java)
                            if (user != null){
                                if (user.uid == sp.getString("phone_no","")){
                                    isUserExists = true
                                    break
                                }
                                else isUserExists = false
                            }
                        }
                        if (isUserExists){
                            openMainPage()
                        }else openLoginPage()
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
            }else openLoginPage()

        }else openLoginPage()
    }
    private fun openLoginPage(){
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        },1000)
    }
    private fun openMainPage(){
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        },1000)
    }
}