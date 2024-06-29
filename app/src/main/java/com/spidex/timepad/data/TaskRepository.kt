package com.spidex.timepad.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDate

class TaskRepository(private val taskDao: TaskDao) {

    // -------- Task Operations --------
    suspend fun insertTask(task: Task) {
        val newTaskId = taskDao.insertTask(task)
        val newTask = task.copy(id = newTaskId)

        val initialInstance = newTask.toTaskInstance(newTask.createdAt)
        taskDao.insertTaskInstance(initialInstance)

    }

    suspend fun updateTask(task : Task){
        taskDao.updateTask(task)
        taskDao.deleteFutureTaskInstancesByTaskId(task.id, LocalDate.now())
    }

    suspend fun deleteTask(task : Task){
        val updatedTask = task.copy(
            isDeleted = true
        )

        taskDao.updateTask(updatedTask)
        taskDao.deleteFutureTaskInstancesByTaskId(task.id, LocalDate.now())
    }

    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()

    suspend fun deleteTaskPermanently(task : Task){
        taskDao.deleteTask(task)
    }

    suspend fun deleteTaskWithNoInstances() {
        withContext(Dispatchers.IO) {
            taskDao.deleteTasksWithNoInstances()
        }
    }


    // -------- Task Flows --------


    // -------- Task Instance Flows --------
    fun getAllTaskInstances(): Flow<List<TaskInstance>> = taskDao.getAllTaskInstances()

    suspend fun insertTaskInstance(taskInstance: TaskInstance) : Long {
        return taskDao.insertTaskInstance(taskInstance)
    }

    fun getTaskInstancesForTaskByDate(parentId : Long, date : LocalDate) : List<TaskInstance> {
        return taskDao.getTaskInstancesForTaskByDate(parentId,date)
    }

    fun getAllTaskInstanceForTaskForDate(parentId : Long, date : LocalDate) : List<TaskInstance>{
        return taskDao.getAllTaskInstanceForTaskForDate(parentId,date)
    }

    fun getTodayInstances() : Flow<List<TaskInstance>> {
        return taskDao.getInstancesForDate(LocalDate.now())
    }

    fun getInstancesForDateRange(startDate : LocalDate,endDate : LocalDate) : Flow<List<TaskInstance>>{
        return taskDao.getTaskInstancesForDateRange(startDate,endDate)
    }

    suspend fun updateTaskInstance(updatedTaskInstance: TaskInstance) {
        taskDao.updateTaskInstance(updatedTaskInstance)
    }

    suspend fun deleteTaskInstance(taskInstance: TaskInstance){
        val deletedTaskInstance = taskInstance.copy(
            isDeleted = true
        )

        taskDao.updateTaskInstance(deletedTaskInstance)
    }

    fun deleteMonthOldDeletedInstances(date : LocalDate){
        val lastDate = date.minusMonths(1)
        taskDao.deleteMonthOldDeletedInstances(lastDate)
        taskDao.deletePastDeletedInstance(date)
    }

    // -------- Task Management --------

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAllTasksWithInstances(): Flow<List<Pair<Task, List<TaskInstance>>>> {
        return taskDao.getAllTasks().flatMapLatest { tasks ->
            taskDao.getAllTaskInstances().map { taskInstances ->
                tasks.map { task ->
                    val matchingInstances = taskInstances.filter {
                        it.parentTaskId == task.id
                    }
                    task to matchingInstances
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getTodayTasksWithInstances(): Flow<List<Pair<Task, TaskInstance>>> {
        val date = LocalDate.now()
        return taskDao.getAllTasks().flatMapLatest { tasks ->
            taskDao.getTaskInstancesForDate(date).map { taskInstances ->
                tasks.mapNotNull { task ->
                    val instance = taskInstances.find {
                        it.parentTaskId == task.id && (it.status != TaskInstanceStatus.COMPLETED) && !it.isDeleted
                    }
                    instance?.let { task to it }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getTasksWithInstancesOnDate(date: LocalDate): Flow<List<Pair<Task, TaskInstance>>> {
        return taskDao.getAllTasks().flatMapLatest { tasks ->
            taskDao.getTaskInstancesForDate(date).map { taskInstances ->
                tasks.mapNotNull { task ->
                    val instance = taskInstances.find {
                        it.parentTaskId == task.id && !it.isDeleted
                    }
                    instance?.let { task to it }
                }
            }
        }
    }

    fun getLastWeekProductivityForGraph(): Flow<List<Double>> {
        val today = LocalDate.now()
        val startDate = today.minusDays(6)

        return taskDao.getTaskInstancesForDateRange(startDate, today).map { taskInstances ->
            val productivityByDayOfWeek = MutableList(7) { 0.0 }
            taskInstances
                .forEach { instance ->
                    val dayOfWeekIndex = instance.scheduledDate.dayOfWeek.value - 1
                    productivityByDayOfWeek[dayOfWeekIndex] += ((instance.durationMinutes.toDouble() - instance.remainingTimeMillis/(1000 * 60))/60)
                }

            productivityByDayOfWeek
        }
    }
}