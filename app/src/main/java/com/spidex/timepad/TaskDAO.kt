package com.spidex.timepad

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE status = :status")
    fun getTasksByStatus(status: TaskStatus): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE tag = :tag")
    fun getTasksByTag(tag: String): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun getTaskById(id: Int): Flow<Task>

    @Query("SELECT * FROM tasks WHERE scheduled_date = :scheduledDate")
    fun getTasksByDate(scheduledDate: LocalDate): Flow<List<Task>>

    // Optimized recurring task queries:
    @Query("SELECT * FROM tasks WHERE repeat_interval IS NOT NULL")
    fun getRecurringTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE repeat_interval = :repeatInterval")
    fun getRecurringTasksByInterval(repeatInterval: RepeatInterval): Flow<List<Task>>

    // Updated query for tasks on a specific date, including recurring ones
    @Query("""
            SELECT * FROM tasks
            WHERE
            (scheduled_date = :date AND repeat_interval IS NULL)  -- One-time tasks
            OR (
                repeat_interval IS NOT NULL
                        AND (
                            scheduled_date = :date
                            OR (
                                created_at <= :date
                                        AND (
                                            repeat_interval = 'DAILY'
                                                    OR (repeat_interval = 'WEEKLY' AND CAST(strftime('%w', :date) AS INTEGER) = CAST(strftime('%w', created_at) AS INTEGER))
                                                    OR (repeat_interval = 'MONTHLY' AND CAST(strftime('%d', :date) AS INTEGER) = CAST(strftime('%d', created_at) AS INTEGER))
                                                    OR (repeat_interval = 'YEARLY' AND CAST(strftime('%m-%d', :date) AS INTEGER) = CAST(strftime('%m-%d', created_at) AS INTEGER))
                                        )
                            )
                        )
            )
        """)
    fun getTasksForDate(date: LocalDate): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE completed_on = :completedOn")
    fun getTasksCompletedOn(completedOn: LocalDate): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE status = 'COMPLETED' AND completed_on = :today")
    fun getTasksCompletedToday(today: LocalDate): Flow<List<Task>>

    @Query("""
        SELECT * FROM tasks
        WHERE status = 'COMPLETED' AND completed_on BETWEEN :startDate AND :endDate
    """)
    fun getTasksCompletedBetweenDates(startDate: LocalDate, endDate: LocalDate): Flow<List<Task>>

    @Query("""
        SELECT * FROM tasks
        WHERE 
            (scheduled_date = :date AND repeat_interval IS NULL AND status != 'COMPLETED') -- One-time tasks
            OR ( 
                repeat_interval IS NOT NULL 
                AND status != 'COMPLETED'
                AND (
                    scheduled_date = :date
                    OR (
                        created_at <= :date
                        AND (
                            repeat_interval = 'DAILY'
                            OR (repeat_interval = 'WEEKLY' AND CAST(strftime('%w', :date) AS INTEGER) = CAST(strftime('%w', created_at) AS INTEGER))
                            OR (repeat_interval = 'MONTHLY' AND CAST(strftime('%d', :date) AS INTEGER) = CAST(strftime('%d', created_at) AS INTEGER))
                            OR (repeat_interval = 'YEARLY' AND CAST(strftime('%m-%d', :date) AS INTEGER) = CAST(strftime('%m-%d', created_at) AS INTEGER))
                        )
                    )
                )
            )
    """)
    fun getIncompleteTasksForDate(date: LocalDate): Flow<List<Task>>
}