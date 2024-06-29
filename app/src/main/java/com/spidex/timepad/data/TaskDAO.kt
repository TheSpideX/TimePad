package com.spidex.timepad.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface TaskDao {
    // -------- Task Operations --------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task) : Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Long)

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long) : Task

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE title = :title")
    suspend fun getTaskByTitle(title: String): Task?

    @Query("SELECT * FROM tasks t WHERE NOT EXISTS (SELECT 1 FROM task_instances ti WHERE ti.parent_task_id = t.id)")
    suspend fun getTasksWithNoInstances(): List<Task>

    @Transaction
    suspend fun deleteTasksWithNoInstances() {
        val tasksToDelete = getTasksWithNoInstances()
        for (task in tasksToDelete) {
            deleteTask(task)
        }
    }


    // -------- TaskInstance Operations --------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskInstance(taskInstance: TaskInstance) : Long

    @Update
    suspend fun updateTaskInstance(taskInstance: TaskInstance)

    @Delete
    suspend fun deleteTaskInstance(taskInstance: TaskInstance)

    @Query("DELETE FROM task_instances WHERE parent_task_id = :id AND scheduled_date >= :date AND isCompleted = 0")
    suspend fun deleteFutureTaskInstancesByTaskId(id : Long, date : LocalDate)

    // Delete all task instances associated with a specific task
    @Query("DELETE FROM task_instances WHERE parent_task_id = :taskId")
    suspend fun deleteTaskInstancesByTaskId(taskId: Long)

    @Query("DELETE FROM task_instances WHERE scheduled_date <= :date")
    fun deleteMonthOldDeletedInstances(date : LocalDate)

    @Query("DELETE FROM task_instances WHERE scheduled_date <= :date AND is_deleted = 1")
    fun deletePastDeletedInstance(date: LocalDate)

    // Get task instances for a specific task within a date range (lazy loading)
    @Query("""
        SELECT * FROM task_instances 
        WHERE parent_task_id = :taskId 
          AND scheduled_date BETWEEN :startDate AND :endDate
    """)
    fun getTaskInstancesForTask(taskId: Long, startDate: LocalDate, endDate: LocalDate): List<TaskInstance>

    // Get all task instances
    @Query("SELECT * FROM task_instances")
    fun getAllTaskInstances(): Flow<List<TaskInstance>>

    // Get a task instance by its ID
    @Query("SELECT * FROM task_instances WHERE id = :taskInstanceId")
    suspend fun getTaskInstanceById(taskInstanceId: Long): TaskInstance?

    @Query("SELECT * FROM task_instances WHERE scheduled_date = :date AND is_deleted = 0")
    fun getInstancesForDate(date : LocalDate) : Flow<List<TaskInstance>>

    @Query("""
        SELECT * FROM task_instances 
        WHERE scheduled_date BETWEEN :startDate AND :endDate 
        AND is_deleted = 0
    """)
    fun getTaskInstancesForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<TaskInstance>>

    // Get incomplete task instances for a specific date, with their parent tasks
    @Query("""
        SELECT * FROM task_instances 
        WHERE scheduled_date = :date AND (status != 'COMPLETED' OR isCompleted = 0)
    """)
    fun getIncompleteTaskInstancesForDate(date: LocalDate = LocalDate.now()): Flow<List<TaskInstance>>

    // Get task instances due on a specific date
    @Query("""
        SELECT * FROM task_instances 
        WHERE scheduled_date = :date
    """)
    fun getTaskInstancesForDate(date: LocalDate): Flow<List<TaskInstance>>

    //Get Task Instance based on date and id
    @Query(
        """
        SELECT * FROM task_instances
        WHERE parent_task_id = :parentId AND scheduled_date = :date AND isCompleted = 0
    """
    )
    fun getTaskInstancesForTaskByDate(parentId : Long, date : LocalDate) : List<TaskInstance>

    @Query(
        """
        SELECT * FROM task_instances
        WHERE parent_task_id = :parentId AND scheduled_date = :date
    """
    )
    fun getAllTaskInstanceForTaskForDate(parentId : Long, date : LocalDate) : List<TaskInstance>
}