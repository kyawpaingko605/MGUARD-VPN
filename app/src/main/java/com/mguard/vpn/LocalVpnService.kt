package com.mguard.vpn

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.app.NotificationCompat
import java.io.IOException

class LocalVpnService : VpnService(), Runnable {

    private var vpnThread: Thread? = null
    private var vpnInterface: ParcelFileDescriptor? = null
    private var isRunning = false

    companion object {
        const val ACTION_CONNECT = "com.mguard.vpn.START"
        const val ACTION_DISCONNECT = "com.mguard.vpn.STOP"
        private const val NOTIFICATION_ID = 8823
        private const val CHANNEL_ID = "mguard_vpn_channel"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val action = intent.action
            if (ACTION_DISCONNECT == action) {
                stopVpn()
                return START_NOT_STICKY
            } else if (ACTION_CONNECT == action) {
                startVpn()
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        stopVpn()
        super.onDestroy()
    }

    private fun startVpn() {
        if (isRunning) return
        isRunning = true

        createNotificationChannel()
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("MGuard VPN ချိတ်ဆက်ထားသည်")
            .setContentText("သင်၏ အင်တာနက်လိုင်းကို လုံခြုံစွာ ကာကွယ်ပေးထားပါသည်")
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        vpnThread = Thread(this, "MGuardVPNThread").apply {
            start()
        }
    }

    private fun stopVpn() {
        isRunning = false
        if (vpnInterface != null) {
            try {
                vpnInterface?.close()
            } catch (e: IOException) {
                Log.e("MGuardVPN", "Error closing vpn interface", e)
            }
            vpnInterface = null
        }
        vpnThread?.interrupt()
        vpnThread = null
        stopForeground(true)
        stopSelf()
    }

    override fun run() {
        try {
            val builder = Builder()
                .setSession("MGuardVPN")
                .addAddress("10.0.0.2", 24)
                .addRoute("0.0.0.0", 0)
                .addDnsServer("8.8.8.8")

            vpnInterface = builder.establish()

            while (isRunning) {
                Thread.sleep(1000)
            }
        } catch (e: Exception) {
            Log.e("MGuardVPN", "Exception in VPN loop", e)
        } finally {
            stopVpn()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "MGuard VPN Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }
}
