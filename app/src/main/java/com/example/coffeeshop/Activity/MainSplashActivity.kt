package com.example.coffeeshop.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.coffeeshop.databinding.ActivityMainSplashBinding
import com.google.firebase.auth.FirebaseAuth

class MainSplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainSplashBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainSplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        Handler(Looper.getMainLooper()).postDelayed({
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                // Nếu đã đăng nhập → vào trang home
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // Nếu chưa đăng nhập → vào login
                startActivity(Intent(this, SplashActivity::class.java))
            }
            finish()
        }, 1500) // delay 1.5 giây splash
    }
}
