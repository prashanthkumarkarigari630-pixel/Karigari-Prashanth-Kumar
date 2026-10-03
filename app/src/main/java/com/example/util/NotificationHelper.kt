package com.example.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R

object NotificationHelper {

    const val CHANNEL_ORDER_UPDATES = "pk_arts_order_updates"
    const val CHANNEL_ANNOUNCEMENTS = "pk_arts_announcements"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelOrders = NotificationChannel(
                CHANNEL_ORDER_UPDATES,
                "Order Status Updates",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Real-time updates regarding your design & print order progress"
                enableVibration(true)
            }

            val channelAnnouncements = NotificationChannel(
                CHANNEL_ANNOUNCEMENTS,
                "Studio Offers & Announcements",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Special festival offers, discounts, and studio updates"
                enableVibration(true)
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channelOrders)
            manager.createNotificationChannel(channelAnnouncements)
        }
    }

    fun sendOrderStatusNotification(
        context: Context,
        orderId: String,
        orderTitle: String,
        status: String,
        customMessage: String = ""
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("TARGET_ORDER_ID", orderId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            orderId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val messageText = if (customMessage.isNotBlank()) {
            customMessage
        } else {
            "Your order $orderId ($orderTitle) has moved to: $status. Tap to track progress."
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ORDER_UPDATES)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("PK Arts: $status")
            .setContentText(messageText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationId = (System.currentTimeMillis() % 100000).toInt()
        NotificationManagerCompat.from(context).notify(notificationId, builder.build())
    }

    fun sendPromotionalNotification(
        context: Context,
        title: String,
        message: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            title.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ANNOUNCEMENTS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationId = (System.currentTimeMillis() % 100000).toInt()
        NotificationManagerCompat.from(context).notify(notificationId, builder.build())
    }
}
