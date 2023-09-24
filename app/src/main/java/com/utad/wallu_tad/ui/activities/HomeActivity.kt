package com.utad.wallu_tad.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.utad.wallu_tad.R
import com.utad.wallu_tad.databinding.ActivityHomeBinding
import com.utad.wallu_tad.notifications.NotificationBroadcastReceiver
import java.util.Calendar

class HomeActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityHomeBinding
    private val binding: ActivityHomeBinding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setBottomNavigationView()

        showWelcomeNotification()
        //showWelcomeNotificationWithAction()
        scheduleLaterNotification()
    }

    private fun setBottomNavigationView() {
        val navHostFragment = supportFragmentManager.findFragmentById(binding.fcvHome.id)
        val controller = navHostFragment?.findNavController()
        if (controller != null) {
            binding.bnvHome.setupWithNavController(controller)
        }
    }


    //region ---- Notifications ---

    private val channelId = "WALLUTAD"

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleLaterNotification() {
        //Creamos un intent de nuestra nueva clase
        val intent = Intent(applicationContext, NotificationBroadcastReceiver::class.java)
        //Creamos un pending intent que de la nueva clase.
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            3, //El id debe ser el mismo que el de la notificación de NotificationBroadcastReceiver, no pueden repetirse con los de la Activity
            intent, //Pasamos el intent de NotificationBroadcastReceiver
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notificationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
        val alarmPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM)

        //Comprobamos que ambos permisos esten concedidos
        if (notificationPermission == PackageManager.PERMISSION_GRANTED &&
            alarmPermission == PackageManager.PERMISSION_GRANTED
        ) {
            val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val triggerTime = Calendar.getInstance().timeInMillis + 10000 //
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    }

    private fun showWelcomeNotification() {
        //Creamos un NotificationCompat.Builder para construir nuestra notificación
        var notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_home) //Podemos elegir el icono de la notificación
            .setContentTitle("¡Bienvenido!") //Ponemos el título de la notifición
            .setContentText("¡Qué bueno verte de nuevo! \nNo dejes pasar los últimos anuncios del campus.")
        // Texto que irá dentro de la notificación

        val notificationPermission =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)

        //Si el usuario tiene concedido el permiso de notificaciones, mostraremos nusetra notificación
        if (notificationPermission == PackageManager.PERMISSION_GRANTED) {
            //Con el NotificationManagerCompat usaremos el método  notify() para enviar la notificación
            with(NotificationManagerCompat.from(this)) {
                //Cada notificación debe tener un id único
                notify(1, notificationBuilder.build())
            }
        }
    }

    private fun showWelcomeNotificationWithAction() {
        // Creamos un intent que lleve a esta activity
        val intent = Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)

        //Creamos un NotificationCompat.Builder para construir nuestra notificación
        var notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_home)
            .setContentTitle("¡Notificación con acción!")
            .setContentText("¡Qué bueno verte de nuevo! \nAbre la Home.")
            // Con esto ponemos que la notificación abra esta activity al pulsarse
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationPermission =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
        if (notificationPermission == PackageManager.PERMISSION_GRANTED) {
            with(NotificationManagerCompat.from(this)) {
                notify(2, notificationBuilder.build())
            }
        }
    }

    //endregion ---- Notifications ---
}