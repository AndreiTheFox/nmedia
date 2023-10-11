package ru.netology.nmedia.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.AppActivity
import ru.netology.nmedia.auth.AppAuth
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class FCMService @Inject constructor(
//    @ApplicationContext
//    private val context: Context
) : FirebaseMessagingService() {
    @Inject
    lateinit var appAuth: AppAuth
    private val channelId: String = "Nmedia Notifications"

    override fun onCreate() {
        super.onCreate()
        val name = getString(R.string.channel_remote_name)
        val descriptionText = getString(R.string.channel_remote_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(channelId, name, importance)
        mChannel.description = descriptionText
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val msgFromServer = Gson().fromJson(message.data["content"], RecivedMsg::class.java)
        val reciever = msgFromServer.recipientId
        val myCurrentId = appAuth.authStateFlow.value.id

        when {
            reciever == null -> handleNewNotification(msgFromServer)
            reciever == myCurrentId -> handleNewNotification(msgFromServer)
            reciever == 0L && reciever != myCurrentId -> appAuth.sendPushToken()
            reciever != 0L && reciever != myCurrentId -> appAuth.sendPushToken()
        }

        when (message.data["action"]) {
            "LIKE" -> handleLike(Gson().fromJson(message.data["content"], Like::class.java))
            "NEW_MESSAGE" -> handleNewMessage(
                Gson().fromJson(
                    message.data["content"],
                    NewMessage::class.java
                )
            )

            "NEW_POST" -> handleNewPost(
                Gson().fromJson(
                    message.data["content"],
                    NewPost::class.java
                )
            )

            else -> return
        }

    }

    private fun handleNewNotification(msg : RecivedMsg) {
        val notificationMessage = getString(R.string.new_notification) + " " + msg.content

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

    private fun handleNewMessage(newMessage: NewMessage) {
        val notificationMessage =
            getString(
                R.string.notification_user_new_message,
                newMessage.senderName,
                newMessage.postContent
            )
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setContentTitle(newMessage.senderName)
            .setContentText(
                notificationMessage
            )
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(notificationMessage)
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

    private fun handleNewPost(newPost: NewPost) {
        val notificationMessage =
            getString(R.string.notification_user_new_post, newPost.authorName, newPost.postContent)
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setContentTitle(newPost.authorName)
            .setContentText(
                notificationMessage
            )
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(notificationMessage)
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

    override fun onNewToken(token: String) {
        appAuth.sendPushToken(token)
    }


    private fun getCurrentPendingIntent(): PendingIntent {
        return PendingIntent.getActivity(
            this, 0,
            Intent(this, AppActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    data class Like(
        val userId: Long,
        val userName: String,
        val postId: Long,
        val postAuthor: String,
    )

    data class NewMessage(
        val senderId: Long,
        val senderName: String,
        val postId: Long,
        val postContent: String,
    )

    data class NewPost(
        val authorId: Long,
        val authorName: String,
        val postId: Long,
        val postContent: String,
    )

    data class RecivedMsg(
        val recipientId: Long?,
        val content: String,
    )
}