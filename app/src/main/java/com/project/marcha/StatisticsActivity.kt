package com.project.marcha

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.project.marcha.databinding.ActivityStatisticsBinding
import kotlin.math.max

class StatisticsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatisticsBinding

    private var steps: Int = 0
    private var distance: Float = 0f
    private var time: String = "00:00:00"
    private var maxHeight: Float = 0f
    private var nameUser: String = ""
    private var trailName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configListeners()
        setDatas()
        uxConfigs()

    }

    private fun uxConfigs(){
        binding.buttonSave.backgroundTintList = null
        binding.buttonDiscard.backgroundTintList = null

        binding.textViewSteps.text = "Passos: $steps"
        binding.textViewDistance.text = "Distância: %.2f m".format(distance)
        binding.textViewTime.text = "Tempo: $time"
        binding.textViewMaxHeight.text = "Altitude: %.2f m".format(maxHeight)
    }

    private fun configListeners(){
        binding.buttonSave.setOnClickListener {
            trailName = binding.editTextNameTrail.text.toString()
            saveTrail(steps, distance, time, maxHeight, nameUser, trailName)
        }

        binding.buttonDiscard.setOnClickListener {
            discardTrail()
        }
    }

    private fun setDatas(){
        steps = intent.getIntExtra("steps", 0)
        distance = intent.getFloatExtra("distance", 0f)
        time = intent.getStringExtra("time") ?: "00:00:00"
        maxHeight = intent.getFloatExtra("maxHeight", 0f)
        nameUser = intent.getStringExtra("nameUser") ?: ""

        trailName = binding.editTextNameTrail.text.toString()
    }

    private fun discardTrail(){
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun saveTrail(steps: Int, distance: Float, time: String, maxHeight: Float, nameUser: String, trailName: String){
        val db = FirebaseFirestore.getInstance()
        val email = FirebaseAuth.getInstance().currentUser?.email ?: return

        val trail = mapOf(
            "trailName" to trailName,
            "steps" to steps,
            "distance" to distance,
            "time" to time,
            "maxHeight" to maxHeight,
            "userName" to nameUser,
            "data" to FieldValue.serverTimestamp())

        db.collection("users").document(email)
            .collection("trails")
            .add(trail)
            .addOnSuccessListener {
                Toast.makeText(this, "Trilha salva com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao salvar trilha", Toast.LENGTH_SHORT).show()
            }
    }
}