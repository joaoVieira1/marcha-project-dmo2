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

class HomeActivity : AppCompatActivity(), StepCounterHelper.Callback {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var stepCounter: StepCounterHelper
    private var counting = false
    val firebaseAuth = FirebaseAuth.getInstance()

    private val stepReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == StepCounterService.ACTION_UPDATE_STEP_COUNT) {
                val passos = intent.getIntExtra(StepCounterService.EXTRA_STEP_COUNT, 0)
                binding.textViewCounter.text = "Passos = $passos"
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermissions()

        stepCounter = StepCounterHelper(this, this)

        if (!stepCounter.hasSensor()) {
            Toast.makeText(this, getString(R.string.sensor_error), Toast.LENGTH_LONG).show()
            binding.buttonStart.isEnabled = false
        }

        configListeners()
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
        binding.textViewCounter.text = "Passos = $passos"
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            stepReceiver,
            IntentFilter(StepCounterService.ACTION_UPDATE_STEP_COUNT)
        )
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(stepReceiver)
    }

    private fun startCounter(){
        counting = !counting
        if (counting) {
            val intent = Intent(this, StepCounterService::class.java)
            ContextCompat.startForegroundService(this, intent)
            binding.buttonStart.text = "PARAR"
        } else {
            val intent = Intent(this, StepCounterService::class.java)
            stopService(intent)
            binding.buttonStart.text = "INICIAR"
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


}