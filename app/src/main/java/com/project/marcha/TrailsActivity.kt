package com.project.marcha

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.project.marcha.adapter.TrailAdapter
import com.project.marcha.databinding.ActivityTrailsBinding
import com.project.marcha.model.Trail
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrailsBinding

    private val trails = ArrayList<Trail>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configListeners()
        uxConfigs()
        loadTrails()

    }

    private fun configListeners(){
        binding.buttonBack.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun uxConfigs(){
        binding.buttonBack.backgroundTintList = null
    }

    private fun loadTrails(){
        val db = Firebase.firestore
        val email = FirebaseAuth.getInstance().currentUser?.email ?: return

        val query = db.collection("users")
            .document(email)
            .collection("trails")
            .orderBy("data", Query.Direction.DESCENDING)

        query.get().addOnCompleteListener {
            task ->
                if(task.isSuccessful){
                    val result = task.result

                    for(document in result.documents){
                        val nameUser = document.data!!["userName"].toString()
                        val nameTrail = document.data!!["trailName"].toString()
                        val time = document.data!!["time"].toString()
                        val data = document.getTimestamp("data")

                        val stepsString = document.data!!["steps"].toString()
                        val distanceString = document.data!!["distance"].toString()
                        val maxHeightString = document.data!!["maxHeight"].toString()

                        val steps = stepsString.toInt()
                        val distance = distanceString.toFloat()
                        val maxHeight = maxHeightString.toFloat()

                        val date: Date? = data?.toDate()
                        val dataFormatada = date?.let {
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
                        } ?: ""

                        trails.add(Trail(nameTrail, nameUser, time, steps, distance, maxHeight, dataFormatada))
                    }

                    binding.recycleView.layoutManager = LinearLayoutManager(this)
                    binding.recycleView.adapter = TrailAdapter(trails.toTypedArray())

                }
        }
    }

}