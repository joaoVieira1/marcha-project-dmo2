package com.project.marcha

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.project.marcha.databinding.ActivityHomeBinding
import com.project.marcha.databinding.ActivityLoginBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configListeners()
    }

    private fun configListeners(){
        binding.buttonSignOut.setOnClickListener {
            signOut()
        }
    }

    private fun signOut(){
        firebaseAuth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}