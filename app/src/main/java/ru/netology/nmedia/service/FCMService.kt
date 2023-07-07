package ru.netology.nmedia.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.AppActivity
import kotlin.random.Random

class FCMService : FirebaseMessagingService() {
    private val channelId: String = "Nmedia Notifications"

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(channelId, name, importance)
            mChannel.description = descriptionText
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        when (message.data["action"]) {
            "LIKE" -> handleLike(Gson().fromJson(message.data["content"], Like::class.java))
            "NEW_MESSAGE"-> handleNewMessage(Gson().fromJson(message.data["content"], NewMessage::class.java))
            "NEW_POST"->handleNewPost(Gson().fromJson(message.data["content"], NewPost::class.java))
            else -> return
        }

    }

    private fun handleLike(like: Like) {
        val notificationMessage =
            getString(R.string.notification_user_liked, like.userName, like.postAuthor)
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setContentTitle(
                notificationMessage
            )
            .setContentIntent(getCurrentPendingIntent())
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(this)
                .notify(Random.nextInt(100_000), notification)
        }
    }

    private fun handleNewMessage (newMessage: NewMessage){
        val notificationMessage =
            getString(R.string.notification_user_new_message, newMessage.senderName, newMessage.postContent)
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setContentTitle (newMessage.senderName)
            .setContentText(
                notificationMessage
            )
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(notificationMessage))
            .setContentIntent(getCurrentPendingIntent())
            .setAutoCancel(true)
            .build()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(this)
                .notify(Random.nextInt(100_000), notification)
        }
    }
    private fun handleNewPost (newPost: NewPost){
        val notificationMessage =
            getString(R.string.notification_user_new_post, newPost.authorName, newPost.postContent)
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setContentTitle (newPost.authorName)
            .setContentText(
                notificationMessage
            )
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(notificationMessage))
            .setContentIntent(getCurrentPendingIntent())
            .setAutoCancel(true)
            .build()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(this)
                .notify(Random.nextInt(100_000), notification)
        }
    }
    override fun onNewToken(token: String) {
        println(token)
    }
    private fun getCurrentPendingIntent(): PendingIntent{

        val notifyPendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, AppActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return notifyPendingIntent
    }

    data class Like(
        val userId: Long,
        val userName: String,
        val postId: Long,
        val postAuthor: String,
    )
    data class NewMessage (
        val senderId: Long,
        val senderName: String,
        val postId: Long,
        val postContent: String,
            )
    data class NewPost (
        val authorId: Long,
        val authorName: String,
        val postId: Long,
        val postContent: String,
    )
}