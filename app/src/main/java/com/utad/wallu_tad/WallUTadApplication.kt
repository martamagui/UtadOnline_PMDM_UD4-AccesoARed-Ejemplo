package com.utad.wallu_tad


import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

class WallUTadApplication : Application() {
    private val channelId = "WALLUTAD"

    override fun onCreate() {
        super.onCreate()

        //Si el dispositivo es superior a Android 8, necesitaremos crear una canal de notificaciones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel() {
        //Sólo deberá llamarse a esta función una vez al abrirse la app.
        val notificationChannel = NotificationChannel(
            channelId, // ID del canal de  las notificaiones
            "Wallutad-channel", // Nombre del canal
            NotificationManager.IMPORTANCE_DEFAULT //Nivel de prioridad de la notificación
        )

        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //Creamos el canal con nuestras variables
        notificationManager.createNotificationChannel(notificationChannel)
    }
}








