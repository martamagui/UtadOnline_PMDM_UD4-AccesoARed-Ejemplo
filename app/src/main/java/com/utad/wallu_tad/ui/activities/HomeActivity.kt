package com.utad.wallu_tad.ui.activities

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.utad.wallu_tad.R
import com.utad.wallu_tad.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityHomeBinding
    private val binding: ActivityHomeBinding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setBottomNavigationView()

        //Si el dispositivo es superior a Android 8, necesitaremos crear una canal de notificaciones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        showWelcomeNotification()
    }

    private fun setBottomNavigationView() {
        val navHostFragment = supportFragmentManager.findFragmentById(binding.fcvHome.id)
        val controller = navHostFragment?.findNavController()
        if (controller != null) {
            binding.bnvHome.setupWithNavController(controller)
        }
    }


    //region ---- Notifications ---

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel() {
        //Sólo deberá llamarse a esta función una vez al abrirse la app.
        val notificationChannel = NotificationChannel(
            channelId, // ID del canal de  las notificaiones
            "Wallutad-channel", // Nombre del canal
            NotificationManager.IMPORTANCE_DEFAULT //Nivel de prioridad de la notificación
        )

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //Creamos el canal con nuestras variables
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private val channelId = "WALLUTAD"

    fun showWelcomeNotification() {
        var notificationBuilder = NotificationCompat.Builder(this, channelId)
            //Podemos elegir el icono de la notificación
            .setSmallIcon(R.drawable.ic_home)
            //Ponemosel título de la notifición
            .setContentTitle("¡Bienvenido!")
            // Texto que irá dentro de la notificación
            .setContentText("¡Qué bueno verte de nuevo! \nNo dejes pasar los últimos anuncios del campus.")

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@HomeActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Si el usuario ha desactivado las notificaiones no se mostrarán
                return
            }
            notify(1, notificationBuilder.build())
        }
    }

    //endregion ---- Notifications ---
}