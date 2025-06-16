package com.project.marcha

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.project.marcha.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configListeners()
        keepLogin()

    }

    private fun configListeners(){
        binding.buttonSignUp.setOnClickListener {
            login()
        }

    }

    private fun login(){
        var email = binding.editTextEmail.text.toString()
        var password = binding.editTextPassword.text.toString()

        if(email.isNotEmpty() && password.isNotEmpty()){
            signUser(email, password)
        }else{
            Toast.makeText(this, getString(R.string.login_error), Toast.LENGTH_SHORT).show()
        }
    }

    private fun signUser(email: String, password: String){
        firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {task ->
                if(task.isSuccessful){
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }else{
                    Toast.makeText(this, getString(R.string.login_error), Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun keepLogin(){
        if(firebaseAuth.currentUser != null){
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

}