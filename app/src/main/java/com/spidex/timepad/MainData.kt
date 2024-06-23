package com.spidex.timepad

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String = "",

    @ColumnInfo(name = "duration_minutes")
    val durationMinutes: Long,

    @ColumnInfo(name = "remaining_time_millis")
    val remainingTimeMillis: Long = durationMinutes * 60 * 1000L,

    @ColumnInfo(name = "status")
    val status: TaskStatus = TaskStatus.NOT_STARTED,

    @ColumnInfo(name = "tag")
    val tag: String? = null,

    @ColumnInfo(name = "icon")
    val icon: Int? = null,

    @ColumnInfo(name = "scheduled_date")
    val scheduledDate: LocalDate = LocalDate.now(),

    @ColumnInfo(name = "created_at")
    val createdAt: LocalDate = LocalDate.now(),

    @ColumnInfo(name = "repeat_interval")
    val repeatInterval: RepeatInterval? = null,

    @ColumnInfo(name = "completed_on")
    val completedOn : LocalDate? = null
)

enum class TaskStatus {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED
}

enum class RepeatInterval
{
    NONE,
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
}

sealed class NavigationRoute(val route: String) {
    data object Home : NavigationRoute("home")
    data object Task : NavigationRoute("task")
    data object Dashboard : NavigationRoute("dashboard")
    data object Clock : NavigationRoute("clock")
    data object Splash : NavigationRoute("splash")
}