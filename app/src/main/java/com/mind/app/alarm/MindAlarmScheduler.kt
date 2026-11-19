package com.mind.app.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.mind.app.data.prayer.PrayerEngine
import com.mind.app.domain.model.TaskItem

object MindAlarmScheduler {

    const val EXTRA_ALARM_TYPE = "extra_alarm_type"
    const val TYPE_ADHAN = "TYPE_ADHAN"
    const val TYPE_TASK = "TYPE_TASK"

    const val EXTRA_PRAYER_NAME = "extra_prayer_name"
    const val EXTRA_TASK_ID = "extra_task_id"
    const val EXTRA_TASK_TITLE = "extra_task_title"
    const val EXTRA_TASK_IMAGE = "extra_task_image"

    private const val ADHAN_REQUEST_CODE = 7001

    fun scheduleNextAdhan(context: Context) {
        val state = PrayerEngine.calculateDashboardState()
        val nextPrayer = state.prayersList.firstOrNull { it.isNext } ?: return
        if (nextPrayer.epochMillis <= System.currentTimeMillis()) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MindAlarmReceiver::class.java).apply {
            putExtra(EXTRA_ALARM_TYPE, TYPE_ADHAN)
            putExtra(EXTRA_PRAYER_NAME, nextPrayer.nameAr)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ADHAN_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        scheduleExactSafe(alarmManager, nextPrayer.epochMillis, pendingIntent)
    }

    fun cancelAdhan(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MindAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ADHAN_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun scheduleTaskAlarm(context: Context, task: TaskItem) {
        val triggerAt = task.dueAtMillis ?: return
        if (task.isDone || triggerAt <= System.currentTimeMillis()) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MindAlarmReceiver::class.java).apply {
            putExtra(EXTRA_ALARM_TYPE, TYPE_TASK)
            putExtra(EXTRA_TASK_ID, task.id)
            putExtra(EXTRA_TASK_TITLE, task.title)
            putExtra(EXTRA_TASK_IMAGE, task.imagePath)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        scheduleExactSafe(alarmManager, triggerAt, pendingIntent)
    }

    fun cancelTaskAlarm(context: Context, taskId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MindAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun scheduleExactSafe(alarmManager: AlarmManager, triggerAtMillis: Long, pendingIntent: PendingIntent) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            } else {
                val info = AlarmManager.AlarmClockInfo(triggerAtMillis, pendingIntent)
                alarmManager.setAlarmClock(info, pendingIntent)
            }
        } catch (_: SecurityException) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }
}
