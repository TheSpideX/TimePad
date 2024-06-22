package com.spidex.timepad

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class TaskRepository(private val taskDao: TaskDao) {

    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()
    fun getTasksByStatus(status: TaskStatus): Flow<List<Task>> = taskDao.getTasksByStatus(status)
    fun getTasksByTag(tag: String): Flow<List<Task>> = taskDao.getTasksByTag(tag)
    fun getTasksByDate(scheduledDate: LocalDate): Flow<List<Task>> = taskDao.getTasksByDate(scheduledDate)
    fun getRecurringTasks(): Flow<List<Task>> = taskDao.getRecurringTasks()
    fun getTasksCompletedOn(completedOn: LocalDate): Flow<List<Task>> {
        return taskDao.getTasksCompletedOn(completedOn)
    }
    fun getTasksCompletedToday(today: LocalDate = LocalDate.now()): Flow<List<Task>> {
        return taskDao.getTasksCompletedToday(today)
    }

    fun getTasksCompletedLast7Days(today: LocalDate = LocalDate.now()): Flow<List<Task>> {
        val startDate = today.minusDays(6)
        return taskDao.getTasksCompletedBetweenDates(startDate, today)
    }

    suspend fun calculateTimeSpentForLast7Days(): List<Double> {
        val today = LocalDate.now()
        return (0..6).map { i ->
            val date = today.minusDays(i.toLong())
            val tasks = taskDao.getTasksForDate(date).first()
            val totalMillis = tasks.sumOf { task ->
                task.durationMinutes * 60 * 1000L - task.remainingTimeMillis
            }
            if (tasks.isEmpty()) 0.0
            else totalMillis.toDouble() / (1000 * 60 * 60)
        }.reversed()
    }

    fun getTasksForDate(date: LocalDate): Flow<List<Task>> = taskDao.getTasksForDate(date)
    fun getIncompleteTasksForDate(date: LocalDate): Flow<List<Task>> {
        return taskDao.getIncompleteTasksForDate(date)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTaskById(task.id)
    }

    suspend fun shiftToNextInterval(task: Task) {
        if (task.repeatInterval != null) {
            val nextDate = when (task.repeatInterval) {
                RepeatInterval.DAILY -> task.scheduledDate.plusDays(1)
                RepeatInterval.WEEKLY -> task.scheduledDate.plusWeeks(1)
                RepeatInterval.MONTHLY -> task.scheduledDate.plusMonths(1)
                RepeatInterval.YEARLY -> task.scheduledDate.plusYears(1)
                else -> null // Handle the case where the interval is not set
            }

            nextDate?.let {
                val updatedTask = task.copy(scheduledDate = it, createdAt = it)
                taskDao.updateTask(updatedTask)
            }
        } else {
            // If not recurring, delete the task
            deleteTask(task)
        }
    }

    suspend fun insert(task: Task) = taskDao.insertTask(task)
    suspend fun update(task: Task) = taskDao.updateTask(task)
    suspend fun delete(task: Task) = taskDao.deleteTask(task)
}