package com.project.marcha

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.project.marcha.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configListeners()
    }

    private fun configListeners(){
        binding.buttonRegister.setOnClickListener {
            createUser()
        }
    }

    private fun createUser(){
        var email = binding.editTextEmail.text.toString()
        var password = binding.editTextPassword.text.toString()
        var confirmPassword = binding.editTextConfirmPassword.text.toString()
        if(email.isNotEmpty() and password.isNotEmpty()){
            if(password == confirmPassword){
                registerUser(email, password)
            }else{
                Toast.makeText(this, getString(R.string.register_error), Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, getString(R.string.register_error), Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerUser(email: String, password: String){
        firebaseAuth
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                }else{
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }


}