package com.project.marcha.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.project.marcha.helpers.NotificationHelper
import com.project.marcha.helpers.StepCounterHelper

class StepCounterService : Service(), StepCounterHelper.Callback {

    private lateinit var helper: StepCounterHelper

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, NotificationHelper.createNotification(this))
        helper = StepCounterHelper(applicationContext, this)
        helper.start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        helper.stop()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStepsDetected(passos: Int) {
        val updateIntent = Intent(ACTION_UPDATE_STEP_COUNT)
        updateIntent.putExtra(EXTRA_STEP_COUNT, passos)
        LocalBroadcastManager.getInstance(this).sendBroadcast(updateIntent)
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val ACTION_UPDATE_STEP_COUNT = "com.project.marcha.STEP_UPDATE"
        const val EXTRA_STEP_COUNT = "step_count"
    }
}