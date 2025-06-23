package com.project.marcha

import android.content.Intent
import android.os.Bundle
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.project.marcha.databinding.ActivityProfileBinding
import com.project.marcha.databinding.ActivityRegisterBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configListeners()
        uxConfigs()
    }

    private fun configListeners(){
        binding.buttonRegister.setOnClickListener {
            persistenceDatabase()
        }
    }

    private fun uxConfigs(){
        binding.buttonRegister.backgroundTintList = null
    }

    private fun persistenceDatabase(){
        val email = firebaseAuth.currentUser!!.email.toString()
        val name = binding.editTextName.text.toString()
        val heightText = binding.editTextHeight.text.toString()
        val weightText = binding.editTextWeight.text.toString()
        val sex = getSex(binding)

        val height: Int? = heightText.toIntOrNull()
        val weight: Double? = weightText.toDoubleOrNull()

        val db = Firebase.firestore

        val dados = hashMapOf(
            "name" to name,
            "height" to height,
            "weight" to weight,
            "sex" to sex
        )

        db.collection("users")
            .document(email)
            .set(dados)
            .addOnCompleteListener {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
    }

    fun getSex(binding: ActivityProfileBinding): String? {
        return when (binding.radioGroupSex.checkedRadioButtonId) {
            R.id.radioMasculine -> "Masculino"
            R.id.radioFeminine -> "Feminino"
            else -> null
        }
    }
}