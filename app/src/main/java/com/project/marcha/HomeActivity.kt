package com.project.marcha

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.auth.FirebaseAuth
import com.project.marcha.databinding.ActivityHomeBinding
import com.project.marcha.helpers.StepCounterHelper
import com.project.marcha.services.StepCounterService
import android.Manifest
import android.os.Build
import android.util.Log
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore
import com.project.marcha.helpers.GpsHeightHelper
import com.project.marcha.services.TimerService

class HomeActivity : AppCompatActivity(), StepCounterHelper.Callback, GpsHeightHelper.Callback {

    private lateinit var binding: ActivityHomeBinding

    private lateinit var stepCounter: StepCounterHelper
    private lateinit var heightHelper: GpsHeightHelper

    private var counting = false
    private var maxHeight: Float = Float.MIN_VALUE
    private var finalSteps: Int = 0
    private var finalDistance: Float = 0f
    private var finalTime: String = "00:00:00"

    private var heightUser: Int = 0
    private var sexUser: String = ""
    private var nameUser: String = ""
    private var weightUser: Float = 0f

    val firebaseAuth = FirebaseAuth.getInstance()

    private val stepReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == StepCounterService.ACTION_UPDATE_STEP_COUNT) {
                val steps = intent.getIntExtra(StepCounterService.EXTRA_STEP_COUNT, 0)
                binding.textViewCounter.text = "PASSOS: $steps"

                val stepLenght = estimateStrideLength(heightUser, sexUser)
                val distance = steps * stepLenght
                binding.textViewDistance.text = "DISTÂNCIA: %.2f m".format(distance)

                finalSteps = steps
                finalDistance = steps * stepLenght
            }
        }
    }

    private val timerReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val elapsed = intent?.getLongExtra(TimerService.EXTRA_ELAPSED_TIME, 0L) ?: 0L
            binding.textViewTimer.text = formatElapsedTime(elapsed)

            finalTime = formatElapsedTime(elapsed)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userEmail = firebaseAuth.currentUser?.email?.trim()

        checkPermissions()
        configListeners()
        if(userEmail != null){
            fetchUserData(userEmail)
        }
        uxConfigs()

        binding.walkerImage.setImageResource(R.drawable.homem_parado)

        stepCounter = StepCounterHelper(this, this)
        heightHelper = GpsHeightHelper(this, this)
    }

    private fun configListeners(){
        binding.buttonSignOut.setOnClickListener {
            signOut()
        }

        binding.buttonStart.setOnClickListener {
            startCounter()
        }
    }

    private fun signOut(){
        firebaseAuth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onStepsDetected(passos: Int) {
        binding.textViewCounter.text = "PASSOS: $passos"
    }

    override fun onResume() {
        super.onResume()
        val lbm = LocalBroadcastManager.getInstance(this)
        lbm.registerReceiver(stepReceiver, IntentFilter(StepCounterService.ACTION_UPDATE_STEP_COUNT))
        lbm.registerReceiver(timerReceiver, IntentFilter(TimerService.ACTION_TIMER_UPDATE))

    }

    override fun onPause() {
        super.onPause()
        val lbm = LocalBroadcastManager.getInstance(this)
        lbm.unregisterReceiver(stepReceiver)
        lbm.unregisterReceiver(timerReceiver)
    }

    private fun startCounter(){
        counting = !counting
        if (counting) {
            maxHeight = Float.MIN_VALUE

            binding.textViewMaxHeight.text = "ALTITUDE MÁXIMA: 0.00 m"
            binding.textViewHeight.text = "ALTITUDE: 0.00 m"

            val stepIntent = Intent(this, StepCounterService::class.java)
            ContextCompat.startForegroundService(this, stepIntent)

            val timerIntent = Intent(this, TimerService::class.java)
            ContextCompat.startForegroundService(this, timerIntent)

            heightHelper.start()

            if(sexUser.equals("Masculino")){
                binding.walkerImage.setImageResource(R.drawable.homem_andando)
            }

            if(sexUser.equals("Feminino")){
                binding.walkerImage.setImageResource(R.drawable.mulher_andando)
            }

            binding.textViewLetsStart.visibility = View.GONE

            binding.buttonStart.text = "PARAR"
        } else {
            val stepIntent = Intent(this, StepCounterService::class.java)
            stopService(stepIntent)

            val timerIntent = Intent(this, TimerService::class.java)
            stopService(timerIntent)

            heightHelper.stop()

            if(sexUser.equals("Masculino")){
                binding.walkerImage.setImageResource(R.drawable.homem_parado)
            }

            if(sexUser.equals("Feminino")){
                binding.walkerImage.setImageResource(R.drawable.mulher_parada)
            }

            binding.buttonStart.text = "INICIAR"

            val intent = Intent(this, StatisticsActivity::class.java)

            intent.putExtra("steps", finalSteps)
            intent.putExtra("distance", finalDistance)
            intent.putExtra("time", finalTime)
            intent.putExtra("maxHeight", maxHeight)
            intent.putExtra("nameUser", nameUser)

            startActivity(intent)
        }
    }

    private fun checkPermissions() {
        val permissoesNecessarias = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            permissoesNecessarias.add(Manifest.permission.ACTIVITY_RECOGNITION)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissoesNecessarias.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissoesNecessarias.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (permissoesNecessarias.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissoesNecessarias.toTypedArray(), 123)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            val denied = grantResults.indices.any { grantResults[it] != PackageManager.PERMISSION_GRANTED }
            if (denied) {
                Toast.makeText(this, "Permissões negadas. O app pode não funcionar corretamente.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permissões concedidas!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onHeightUpdate(height: Float) {
        runOnUiThread {
            binding.textViewHeight.text = "ALTITUDE: %.2f m".format(height)
            if (height > maxHeight) {
                maxHeight = height
                binding.textViewMaxHeight.text = "ALTITUDE MÁXIMA: %.2f m".format(maxHeight)
            }
        }
    }

    private fun uxConfigs(){
        binding.buttonStart.backgroundTintList = null
        binding.buttonSignOut.backgroundTintList = null
    }

    private fun formatElapsedTime(millis: Long): String {
        val seconds = millis / 1000
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }

    fun fetchUserData(email: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .document(email)  // supondo que o e-mail seja usado como ID do documento
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    heightUser = document.getLong("height")?.toInt() ?: 0
                    sexUser = document.getString("sex") ?: ""
                    weightUser = document.getLong("weight")?.toFloat() ?: 0f
                    nameUser = document.getString("name") ?: ""
                } else {
                    Log.w("HomeActivity", "Documento do usuário não encontrado")
                }
            }
            .addOnFailureListener { e ->
                Log.e("HomeActivity", "Erro ao buscar dados do usuário", e)
            }
    }

    fun estimateStrideLength(alturaCm: Int, sexo: String): Float {
        return when (sexo.lowercase()) {
            "masculino" -> alturaCm * 0.415f / 100f
            "feminino" -> alturaCm * 0.413f / 100f
            else -> alturaCm * 0.414f / 100f
        }
    }
}