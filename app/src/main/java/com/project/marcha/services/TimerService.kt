package com.project.marcha.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.project.marcha.R

class TimerService : Service() {

    private var startTime = 0L
    private val handler = Handler(Looper.getMainLooper())

    private val timerRunnable = object : Runnable {
        override fun run() {
            val elapsed = SystemClock.elapsedRealtime() - startTime

            val intent = Intent(ACTION_TIMER_UPDATE)
            intent.putExtra(EXTRA_ELAPSED_TIME, elapsed)
            LocalBroadcastManager.getInstance(this@TimerService).sendBroadcast(intent)

            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannelIfNeeded()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val notification = NotificationCompat.Builder(this, "tracking_channel")
            .setContentTitle("Trilha em andamento")
            .setContentText("Contando tempo...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(2, notification)

        startTime = SystemClock.elapsedRealtime()
        handler.post(timerRunnable)

        return START_STICKY
    }

    override fun onDestroy() {
        handler.removeCallbacks(timerRunnable)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "tracking_channel",
                "Monitoramento da Trilha",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    companion object{
        const val ACTION_TIMER_UPDATE = "com.seuapp.ACTION_TIMER_UPDATE"
        const val EXTRA_ELAPSED_TIME = "com.seuapp.EXTRA_ELAPSED_TIME"
    }


}