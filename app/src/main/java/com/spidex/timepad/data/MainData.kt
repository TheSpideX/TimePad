package com.spidex.timepad.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String = "",
    @ColumnInfo(name = "duration_minutes") val durationMinutes: Long,
    @ColumnInfo(name = "repeat_interval") val repeatInterval: RepeatInterval = RepeatInterval.NONE,
    @ColumnInfo(name = "created_at") val createdAt: LocalDate = LocalDate.now(),
    @ColumnInfo(name = "scheduled_time") val scheduledTime : LocalTime? = null,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
    @ColumnInfo(name = "tag") val tag: String,
    @ColumnInfo(name = "icon") val icon: Int
) {
    fun toTaskInstance(date: LocalDate): TaskInstance {
        return TaskInstance(
            parentTaskId = id,
            scheduledDate = date,
            remainingTimeMillis = durationMinutes * 60 * 1000L,
            durationMinutes = durationMinutes,
            scheduledTime = scheduledTime,
        )
    }
}

@Entity(
    tableName = "task_instances",
    foreignKeys = [ForeignKey(
        entity = Task::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("parent_task_id"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class TaskInstance(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "parent_task_id") val parentTaskId: Long,
    @ColumnInfo(name = "scheduled_date") val scheduledDate: LocalDate,
    @ColumnInfo(name = "scheduled_time") val scheduledTime : LocalTime?,
    @ColumnInfo(name = "status") val status: TaskInstanceStatus = TaskInstanceStatus.NOT_STARTED,
    @ColumnInfo(name = "isCompleted") val isCompleted : Boolean = false,
    @ColumnInfo(name = "completed_on") val completedOn: LocalDate? = null,
    @ColumnInfo(name = "is_deleted") val isDeleted : Boolean = false,
    @ColumnInfo(name = "remaining_time_millis") val remainingTimeMillis: Long,
    @ColumnInfo(name = "duration_minutes") val durationMinutes: Long,
    @ColumnInfo(name = "notes") val notes: String? = null,
)

enum class TaskInstanceStatus {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED
}

enum class RepeatInterval {
    NONE, DAILY, WEEKLY, MONTHLY, YEARLY
}

sealed class NavigationRoute(val route: String) {
    data object Home : NavigationRoute("home")
    data object Task : NavigationRoute("task")
    data object Dashboard : NavigationRoute("dashboard")
    data object Clock : NavigationRoute("clock")
    data object Splash : NavigationRoute("splash")
    data object AllTask : NavigationRoute("allTask")
}