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
import com.project.marcha.helpers.GpsHeightHelper
import com.project.marcha.services.TimerService

class HomeActivity : AppCompatActivity(), StepCounterHelper.Callback, GpsHeightHelper.Callback {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var stepCounter: StepCounterHelper
    private lateinit var heightHelper: GpsHeightHelper
    private var counting = false
    private var maxHeight: Float = Float.MIN_VALUE
    val firebaseAuth = FirebaseAuth.getInstance()

    private val stepReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == StepCounterService.ACTION_UPDATE_STEP_COUNT) {
                val passos = intent.getIntExtra(StepCounterService.EXTRA_STEP_COUNT, 0)
                binding.textViewCounter.text = "Passos = $passos"
            }
        }
    }

    private val timerReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val elapsed = intent?.getLongExtra(TimerService.EXTRA_ELAPSED_TIME, 0L) ?: 0L
            binding.textViewTimer.text = formatElapsedTime(elapsed)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermissions()
        uxConfigs()
        configListeners()

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
        binding.textViewCounter.text = "Passos = $passos"
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

            binding.textViewMaxHeight.text = "Altitude Máxima: 0.00 m"
            binding.textViewHeight.text = "Altitude: 0.00 m"

            val stepIntent = Intent(this, StepCounterService::class.java)
            ContextCompat.startForegroundService(this, stepIntent)

            val timerIntent = Intent(this, TimerService::class.java)
            ContextCompat.startForegroundService(this, timerIntent)

            heightHelper.start()

            binding.walkerImage.setImageResource(R.drawable.homem_andando)
            binding.buttonStart.text = "PARAR"
        } else {
            val stepIntent = Intent(this, StepCounterService::class.java)
            stopService(stepIntent)

            val timerIntent = Intent(this, TimerService::class.java)
            stopService(timerIntent)

            heightHelper.stop()

            binding.walkerImage.setImageResource(R.drawable.homem_parado)
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

    override fun onHeightUpdate(height: Float) {
        runOnUiThread {
            binding.textViewHeight.text = "Altitude: %.2f m".format(height)
            if (height > maxHeight) {
                maxHeight = height
                binding.textViewMaxHeight.text = "Altitude Máxima: %.2f m".format(maxHeight)
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
}