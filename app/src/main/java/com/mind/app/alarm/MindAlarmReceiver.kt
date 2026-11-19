package com.mind.app.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.mind.app.data.local.MindStorage
import com.mind.app.ui.alarm.AlarmOverlayActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MindAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val storage = MindStorage(context)

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.IO).launch {
                if (storage.isAdhanEnabledSync()) {
                    MindAlarmScheduler.scheduleNextAdhan(context)
                }
                storage.getTasksSync().forEach { task ->
                    if (!task.isDone && task.dueAtMillis != null) {
                        MindAlarmScheduler.scheduleTaskAlarm(context, task)
                    }
                }
            }
            return
        }

        val alarmType = intent.getStringExtra(MindAlarmScheduler.EXTRA_ALARM_TYPE) ?: return

        if (alarmType == MindAlarmScheduler.TYPE_ADHAN) {
            // Schedule the subsequent prayer immediately
            MindAlarmScheduler.scheduleNextAdhan(context)
        }

        val overlayIntent = Intent(context, AlarmOverlayActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtras(intent)
        }

        // Show High-Priority FullScreen Notification + Launch Overlay Activity
        showFullScreenNotification(context, alarmType, intent, overlayIntent)
        runCatching { context.startActivity(overlayIntent) }
    }

    private fun showFullScreenNotification(
        context: Context,
        alarmType: String,
        sourceIntent: Intent,
        overlayIntent: Intent
    ) {
        val channelId = "mind_priority_alarms"
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "تنبيهات الأذان والمهام",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setSound(null, null) // Audio handled directly by AlarmOverlayActivity
                enableVibration(true)
            }
            nm.createNotificationChannel(channel)
        }

        val title = if (alarmType == MindAlarmScheduler.TYPE_ADHAN) {
            "حان الآن موعد أذان ${sourceIntent.getStringExtra(MindAlarmScheduler.EXTRA_PRAYER_NAME) ?: "الصلاة"}"
        } else {
            "تذكير بمهمة"
        }

        val body = if (alarmType == MindAlarmScheduler.TYPE_ADHAN) {
            "لا حول ولا قوة إلا بالله"
        } else {
            sourceIntent.getStringExtra(MindAlarmScheduler.EXTRA_TASK_TITLE) ?: ""
        }

        val fullScreenPending = PendingIntent.getActivity(
            context,
            System.currentTimeMillis().toInt(),
            overlayIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPending, true)
            .setAutoCancel(true)
            .build()

        nm.notify(System.currentTimeMillis().toInt(), notification)
    }
}
