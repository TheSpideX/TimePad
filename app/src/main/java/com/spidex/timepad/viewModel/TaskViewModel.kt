package com.spidex.timepad.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spidex.timepad.SoundHelper
import com.spidex.timepad.data.RepeatInterval
import com.spidex.timepad.data.Task
import com.spidex.timepad.data.TaskInstance
import com.spidex.timepad.data.TaskInstanceStatus
import com.spidex.timepad.data.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.temporal.ChronoUnit

class TaskViewModel(
    private val taskRepository: TaskRepository,
    private val soundHelper: SoundHelper
) : ViewModel() {

    // State Flows for UI elements and dialogs
    private val _showEditDialog = MutableStateFlow(false)
    val showEditDialog: StateFlow<Boolean> = _showEditDialog
    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog : StateFlow<Boolean> = _showAddDialog
    private val _selectedPeriod = MutableStateFlow("day")
    val selectedPeriod: StateFlow<String> = _selectedPeriod.asStateFlow()
    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog
    private val _showBottomNav = MutableStateFlow(true)
    val showBottomNav: StateFlow<Boolean> = _showBottomNav.asStateFlow()



    // Dashboard-related Flows
    private val _todayTaskInstances = MutableStateFlow<List<TaskInstance>>(emptyList())
    private val todayTaskInstances: StateFlow<List<TaskInstance>> = _todayTaskInstances.asStateFlow()

    private val _lastWeekTaskInstances = MutableStateFlow<List<TaskInstance>>(emptyList())
    private val lastWeekTaskInstances: StateFlow<List<TaskInstance>> = _lastWeekTaskInstances.asStateFlow()

    private val _todayProductivity = MutableStateFlow(Pair(0,0L))
    val todayProductivity : StateFlow<Pair<Int,Long>> = _todayProductivity

    private val _lastWeekProductivity = MutableStateFlow(Pair(0,0L))
    val lastWeekProductivity : StateFlow<Pair<Int,Long>> = _lastWeekProductivity

    private val _graphData = MutableStateFlow<List<Double>>(emptyList())
    val graphData : StateFlow<List<Double>> = _graphData





    // Task Management Flows
    private val _allTasks = MutableStateFlow<List<Task>>(emptyList())
    val allTasks: StateFlow<List<Task>> = _allTasks.asStateFlow()

    private val _allTasksInstance = MutableStateFlow<List<TaskInstance>>(emptyList())

    private val _allTasksWithInstance = MutableStateFlow<List<Pair<Task, List<TaskInstance>>>>(emptyList())
    private val allTasksWithInstance: StateFlow<List<Pair<Task, List<TaskInstance>>>> = _allTasksWithInstance.asStateFlow()

    private val _todayTasksWithInstances = MutableStateFlow<List<Pair<Task, TaskInstance>>>(emptyList())
    val todayTasksWithInstances: StateFlow<List<Pair<Task, TaskInstance>>> = _todayTasksWithInstances.asStateFlow()

    private val _currentTaskWithInstances = MutableStateFlow<Pair<Task, TaskInstance>?>(null)
    val currentTaskWithInstances: StateFlow<Pair<Task, TaskInstance>?> = _currentTaskWithInstances.asStateFlow()

    private val _tasksForDateWithInstances = MutableStateFlow<List<Pair<Task, TaskInstance>>>(emptyList())
    val tasksForDateWithInstances: StateFlow<List<Pair<Task, TaskInstance>>> = _tasksForDateWithInstances.asStateFlow()




    private val _editTaskWithInstances = MutableStateFlow<Pair<Task, TaskInstance>?>(null)
    val editTaskWithInstances: StateFlow<Pair<Task, TaskInstance>?> = _editTaskWithInstances.asStateFlow()

    private val _deleteTaskWithInstances = MutableStateFlow<Pair<Task, TaskInstance>?>(null)
    val deleteTaskWithInstances: StateFlow<Pair<Task, TaskInstance>?> = _deleteTaskWithInstances.asStateFlow()




    // Month and Date
    private val _currentMonth = MutableStateFlow(YearMonth.now())
    private val _selectedDate = MutableStateFlow<LocalDate>(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()



    // Timer Management
    private var timerJob: Job? = null
    private val _timerRunning = MutableStateFlow(false)
    val timerRunning: StateFlow<Boolean> = _timerRunning.asStateFlow()
    private val _remTime = MutableStateFlow<Long?>(null)


    init {
        //All task fetching
        viewModelScope.launch {
            taskRepository.getAllTasks().collectLatest{task->
                _allTasks.value = task
            }
        }

        //All instance fetching
        viewModelScope.launch {
            taskRepository.getAllTaskInstances().collectLatest{taskInstance->
                _allTasksInstance.value = taskInstance
            }
        }

        //All task with instance fetching
        viewModelScope.launch {
            taskRepository.getAllTasksWithInstances().collectLatest{taskWithInstances->
                _allTasksWithInstance.value = taskWithInstances

            }
        }

        //today instance fetching for productivity
        viewModelScope.launch {
            taskRepository.getTodayInstances().collectLatest{ todayInstances->
                _todayTaskInstances.value = todayInstances
                _todayProductivity.value = calculateTodayProductivity()
            }
        }

        //last week instance fetching for productivity
        viewModelScope.launch {
            val endDate = LocalDate.now()
            val startDate = LocalDate.now().minusDays(6)
            taskRepository.getInstancesForDateRange(startDate,endDate).collectLatest { instances->
                _lastWeekTaskInstances.value = instances
                _lastWeekProductivity.value = calculateLastWeekProductivity()
            }
        }

        //Graph data fetching
        viewModelScope.launch {
            taskRepository.getLastWeekProductivityForGraph().collect { data->
                _graphData.value = data
            }
        }


        //Get Today Uncompleted Task with Instance
        viewModelScope.launch {
            taskRepository.getTodayTasksWithInstances().collect{ todayTasksWithInstances ->
                _todayTasksWithInstances.value = todayTasksWithInstances
            }
        }

        //fetching task for date
        viewModelScope.launch {
            _selectedDate.collectLatest{ date->
                taskRepository.getTasksWithInstancesOnDate(date).collect{taskForDate->
                    _tasksForDateWithInstances.value = taskForDate
                }
            }
        }

        //inserting task for month
        viewModelScope.launch {
            _currentMonth.collectLatest { month ->
                taskRepository.getAllTasks().first().forEach { task ->
                    if (task.repeatInterval != RepeatInterval.NONE && !task.isDeleted) {
                        val today = LocalDate.now()
                        val startDate = when {
                            month.isAfter(YearMonth.from(today)) && task.repeatInterval == RepeatInterval.WEEKLY -> {
                                var tempDate = month.atDay(1)
                                while (tempDate.dayOfWeek != task.createdAt.dayOfWeek) {
                                    tempDate = tempDate.plusDays(1)
                                }
                                tempDate
                            }
                            month.isAfter(YearMonth.from(today)) && task.repeatInterval == RepeatInterval.MONTHLY -> {
                                val lastDayOfMonth = month.atEndOfMonth().dayOfMonth
                                month.atDay(minOf(task.createdAt.dayOfMonth, lastDayOfMonth))
                            }
                            else -> task.createdAt
                        }
                        val endDate = month.atEndOfMonth()
                        generateTaskInstancesIfNeeded(task, startDate, endDate)
                    }
                }
            }
        }


        //Delete
        deleteMonthOldDeletedInstances()
        deleteTaskWithNoInstances()
    }

    private fun deleteTaskWithNoInstances() = viewModelScope.launch(Dispatchers.IO){
        taskRepository.deleteTaskWithNoInstances()
    }

    private fun deleteMonthOldDeletedInstances() = viewModelScope.launch(Dispatchers.IO) {
        taskRepository.deleteMonthOldDeletedInstances(LocalDate.now())
    }


    // Function to generate task instances (if not already present)
    private suspend fun generateTaskInstancesIfNeeded(task: Task, startDate: LocalDate, endDate: LocalDate) {
        withContext(Dispatchers.IO) {
            var currentDate = startDate

            while (currentDate <= endDate) {
                if(currentDate==LocalDate.now())
                {
                    if (taskRepository.getAllTaskInstanceForTaskForDate(task.id, currentDate)
                            .isEmpty()
                    ) {
                        val taskInstance = task.toTaskInstance(currentDate)
                        taskRepository.insertTaskInstance(taskInstance)
                    }
                }
                else {
                    if (taskRepository.getTaskInstancesForTaskByDate(task.id, currentDate)
                            .isEmpty()
                    ) {
                        val taskInstance = task.toTaskInstance(currentDate)
                        taskRepository.insertTaskInstance(taskInstance)
                    }
                }

                currentDate = when (task.repeatInterval) {
                    RepeatInterval.DAILY -> currentDate.plusDays(1)
                    RepeatInterval.WEEKLY -> currentDate.plusWeeks(1)
                    RepeatInterval.MONTHLY -> currentDate.plusMonths(1)
                    RepeatInterval.YEARLY -> {
                        var nextDate = currentDate.plusYears(1)
                        if (currentDate.dayOfMonth == 29 && currentDate.month == Month.FEBRUARY && !nextDate.isLeapYear) {
                            nextDate = nextDate.withDayOfMonth(28)
                        }
                        nextDate
                    }
                    else -> break
                }
            }
        }
    }


    // Function to calculate productivity metrics for today
    private fun calculateTodayProductivity(): Pair<Int, Long> {
        val completedTasks = todayTaskInstances.value.filter { it.isCompleted }
        val totalCompletedTimeMillis = todayTaskInstances.value.sumOf { it.durationMinutes * 60 * 1000L - it.remainingTimeMillis }
        return Pair(completedTasks.size, totalCompletedTimeMillis)
    }

    // Function to calculate productivity metrics for the last week
    private fun calculateLastWeekProductivity(): Pair<Int, Long> {
        val completedTasks = lastWeekTaskInstances.value.filter { it.isCompleted }
        val totalCompletedTimeMillis = lastWeekTaskInstances.value.sumOf { it.durationMinutes * 60 * 1000L - it.remainingTimeMillis }
        return Pair(completedTasks.size, totalCompletedTimeMillis)
    }


    fun addNewTask(task: Task) = viewModelScope.launch {
        taskRepository.insertTask(task)
        refreshTasksAndInstances()
    }

    private fun refreshTasksAndInstances() {
        getTasksForDate(selectedDate.value)
        getTodayTask()
    }

    //updated
    private fun getTasksForDate(date: LocalDate) {
        viewModelScope.launch{
            taskRepository.getTasksWithInstancesOnDate(date).collect{taskForDate->
                _tasksForDateWithInstances.value = taskForDate
            }
        }
    }


    private fun getTodayTask() {
        viewModelScope.launch{
            taskRepository.getTodayTasksWithInstances().collect{taskForDate->
                _todayTasksWithInstances.value = taskForDate
            }
        }
    }


    //Timer Work
    fun startOrResumeTimer() {
        if (timerJob?.isActive == true) return
        timerJob = viewModelScope.launch {
            _timerRunning.value = true
            while (_timerRunning.value && (currentTaskWithInstances.value?.second?.remainingTimeMillis
                    ?: 0L) > 0
            ) {
                delay(1000)
                val (currentTask, currentTaskInstance) = currentTaskWithInstances.value!!

                val updatedTaskInstance = currentTaskInstance.copy(
                    status = TaskInstanceStatus.IN_PROGRESS,
                    remainingTimeMillis = currentTaskInstance.remainingTimeMillis - 1000
                )
                _currentTaskWithInstances.value = currentTask to updatedTaskInstance
                taskRepository.updateTaskInstance(updatedTaskInstance)
            }
            _timerRunning.value = false
            if (_currentTaskWithInstances.value!!.second.remainingTimeMillis == 0L) {
                soundHelper.playSound()
                markTaskCompleted(_currentTaskWithInstances.value!!.second)
            }
        }
        getTodayTask()
    }

    fun markTaskCompleted(currentTaskInstance: TaskInstance) {
        viewModelScope.launch {
            val updatedInstance = currentTaskInstance.copy(
                status = TaskInstanceStatus.COMPLETED,
                completedOn = LocalDate.now(),
                isCompleted = true
            )

            taskRepository.updateTaskInstance(updatedInstance)
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        timerJob = null
        soundHelper.stopSound()
        _timerRunning.value = false
        getTodayTask()
    }


    fun checkForTaskOnDate(date: LocalDate): Boolean {
        return allTasksWithInstance.value.any { (task, instances) ->
            !task.isDeleted && date >= LocalDate.now() && instances.any { taskInstance->
                !taskInstance.isDeleted &&
                when (task.repeatInterval) {
                    RepeatInterval.NONE -> task.createdAt == date
                    RepeatInterval.DAILY -> true
                    RepeatInterval.WEEKLY ->
                        ChronoUnit.DAYS.between(task.createdAt, date) % 7 == 0L

                    RepeatInterval.MONTHLY -> task.createdAt.dayOfMonth == date.dayOfMonth
                    RepeatInterval.YEARLY -> task.createdAt.dayOfYear == date.dayOfYear
                }
            }
        }
    }


    fun setCurrentTaskWithInstance(task: Pair<Task, TaskInstance>?) {
        _currentTaskWithInstances.value = task
        _remTime.value = _currentTaskWithInstances.value?.second?.remainingTimeMillis ?: 0
    }


    fun setShowBottomNav(value: Boolean) {
        _showBottomNav.value = value
    }
    fun setSelectedPeriod(period: String) {
        _selectedPeriod.value = period
    }


    //set day and month
    fun setDay(date : LocalDate) {
        _selectedDate.value = date
    }
    fun setCurrentMonth(month : YearMonth){
        _currentMonth.value = month
    }


    //for add task
    fun setAddDialog(value : Boolean){
        _showAddDialog.value = value
    }


    //for edit task
    fun setEditDialog(value : Boolean){
        _showEditDialog.value = value
    }
    fun setEditTask(taskInstance: Pair<Task, TaskInstance>?){
        _editTaskWithInstances.value = taskInstance
    }
    fun updateTask(task : Task, editMain : Boolean, taskInstance: TaskInstance) = viewModelScope.launch{
        if(currentTaskWithInstances.value == Pair(task,taskInstance)){
            _currentTaskWithInstances.value = null
        }

        if(editMain)
        {
            taskRepository.updateTask(task)
            refreshTasksAndInstances()
        }
        else{
            taskRepository.updateTaskInstance(taskInstance)
        }
        generateTaskInstancesIfNeeded(task,LocalDate.now(),YearMonth.now().atEndOfMonth())
        refreshTasksAndInstances()
    }
    fun doneEditing(){
        _editTaskWithInstances.value = null
    }



    //For delete Dialog
    fun setShowDeleteDialog(value : Boolean){
        _showDeleteDialog.value = value
    }
    fun setDeleteTask(taskInstance: Pair<Task, TaskInstance>?){
        _deleteTaskWithInstances.value = taskInstance
    }
    fun deleteTask(task : Pair<Task, TaskInstance>) = viewModelScope.launch{
        if(currentTaskWithInstances.value == task){
            _currentTaskWithInstances.value = null
        }
        taskRepository.deleteTask(task.first)
        generateTaskInstancesIfNeeded(task.first,LocalDate.now(),YearMonth.now().atEndOfMonth())
        refreshTasksAndInstances()
    }
    fun deleteTaskPermanently(task : Pair<Task, TaskInstance>) = viewModelScope.launch {
        if(currentTaskWithInstances.value == task){
            _currentTaskWithInstances.value = null
        }
        taskRepository.deleteTaskPermanently(task.first)
        getTasksForDate(LocalDate.now())
        refreshTasksAndInstances()
    }
    fun deleteInstance(taskInstance: TaskInstance) = viewModelScope.launch{
        if(currentTaskWithInstances.value!!.second == taskInstance)
        {
            _currentTaskWithInstances.value = null
        }
        taskRepository.deleteTaskInstance(taskInstance)
    }
    fun onDeleteDone(){
        _deleteTaskWithInstances.value = null
        refreshTasksAndInstances()
    }
}