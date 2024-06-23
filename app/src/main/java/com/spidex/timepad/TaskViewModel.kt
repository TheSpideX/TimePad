package com.spidex.timepad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class TaskViewModel(private val taskRepository: TaskRepository) : ViewModel() {
    private val _showDialog = MutableStateFlow(false)
    val showDialog : StateFlow<Boolean> = _showDialog

    private val _selectedPeriod = MutableStateFlow("day")
    val selectedPeriod: StateFlow<String> = _selectedPeriod.asStateFlow()

    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog : StateFlow<Boolean> = _showDeleteDialog

    private val _showBottomNav = MutableStateFlow(true)
    val showBottomNav: StateFlow<Boolean> = _showBottomNav.asStateFlow()


    //For Dashboard
    private val _timeSpentLast7Days = MutableStateFlow<List<Double>>(emptyList())
    val timeSpentLast7Days: StateFlow<List<Double>> = _timeSpentLast7Days.asStateFlow()

    private val _todayCompletedTasks = MutableStateFlow<List<Task>>(emptyList())
    val todayCompletedTasks: StateFlow<List<Task>> = _todayCompletedTasks.asStateFlow()

    private val _last7DaysCompletedTasks = MutableStateFlow<List<Task>>(emptyList())
    val last7DaysCompletedTasks: StateFlow<List<Task>> = _last7DaysCompletedTasks.asStateFlow()

    private val _todayCompletedTime = MutableStateFlow<Long>(0)
    val todayCompletedTime: StateFlow<Long> = _todayCompletedTime.asStateFlow()

    private val _last7DaysCompletedTime = MutableStateFlow<Long>(0)
    val last7DaysCompletedTime: StateFlow<Long> = _last7DaysCompletedTime.asStateFlow()




    // Managing Task
    private val _completedTask = MutableStateFlow<List<Task>>(emptyList())
    val completedTask: StateFlow<List<Task>> = _completedTask.asStateFlow()

    private val _allTasks = MutableStateFlow<List<Task>>(emptyList())
    val allTasks: StateFlow<List<Task>> = _allTasks.asStateFlow()

    private val _tasksForDate = MutableStateFlow<List<Task>>(emptyList())
    val tasksForDate: StateFlow<List<Task>> = _tasksForDate.asStateFlow()

    private val _currentTask = MutableStateFlow<Task?>(null)
    val currentTask: StateFlow<Task?> = _currentTask.asStateFlow()

    private val _todayTasks = MutableStateFlow<List<Task>>(emptyList())
    val todayTasks: StateFlow<List<Task>> = _todayTasks.asStateFlow()

    private val _editTask = MutableStateFlow<Task?>(null)
    val editTask : StateFlow<Task?> = _editTask

    private val _deleteTask = MutableStateFlow<Task?>(null)
    val deleteTask : StateFlow<Task?> = _deleteTask





    //Month and Date

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth.asStateFlow()

    private val _selectedDate = MutableStateFlow<LocalDate?>(LocalDate.now())
    val selectedDate: StateFlow<LocalDate?> = _selectedDate.asStateFlow()





    //Timer Management

    private var timerJob: Job? = null
    private var currentJob : Job? = null
    private val _timerRunning = MutableStateFlow(false)
    val timerRunning: StateFlow<Boolean> = _timerRunning.asStateFlow()

    private val _remTime = MutableStateFlow<Long?>(null)
    val remTime : StateFlow<Long?> = _remTime



    val totalTimeSpent: StateFlow<Long> = combine(
        selectedDate,
        allTasks,
        selectedPeriod
    ) { selectedDate, allTasks, selected ->
        val now = LocalDate.now() // Get current date for comparison

        val tasksForPeriod = if (selectedDate == null) {
            emptyList()
        } else if (selected == "day") {
            allTasks.filter { it.scheduledDate == selectedDate }
        } else { // selected == "week"
            allTasks.filter {
                it.scheduledDate != null &&
                        it.scheduledDate.isAfter(now.minusDays(6)) &&
                        it.scheduledDate.isBefore(now.plusDays(1))
            }
        }

        tasksForPeriod.sumOf { it.durationMinutes * 60 * 1000L - it.remainingTimeMillis }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)


    init {
       currentJob = viewModelScope.launch {
            selectedDate.collectLatest { date ->
                if (date != null) {
                    taskRepository.getTasksForDate(date).collect { tasks ->
                        _tasksForDate.value = tasks

                            _currentTask.value = null
                            _remTime.value = 0
                    }
                } else {
                    _tasksForDate.value = emptyList()
                    _currentTask.value = null
                    _remTime.value = 0
                }
            }
        }

        viewModelScope.launch {
            _last7DaysCompletedTime.value = _last7DaysCompletedTasks.value.sumOf { it.durationMinutes }
        }

        viewModelScope.launch {
            _todayCompletedTime.value = _completedTask.value.sumOf { it.durationMinutes }
        }

        viewModelScope.launch {
            taskRepository.getIncompleteTasksForDate(LocalDate.now()).collect { tasks ->
                _todayTasks.value = tasks
            }
        }

        viewModelScope.launch {
            taskRepository.getAllTasks().collect { tasks ->
                _allTasks.value = tasks
            }
        }

        viewModelScope.launch {
            taskRepository.getTasksCompletedToday().collect { tasks ->
                _todayCompletedTasks.value = tasks
            }
        }

        viewModelScope.launch {
            taskRepository.getTasksCompletedLast7Days().collect { tasks ->
                _last7DaysCompletedTasks.value = tasks
            }
        }

        viewModelScope.launch {
            _timeSpentLast7Days.value = taskRepository.calculateTimeSpentForLast7Days()
        }
    }


    //Timer Work
    fun startOrResumeTimer() {
        if (timerJob?.isActive == true) return

        timerJob = viewModelScope.launch {
            _timerRunning.value = true
            while (_timerRunning.value && currentTask.value != null && currentTask.value!!.remainingTimeMillis > 0) {
                delay(1000)
                _currentTask.value = currentTask.value?.copy(
                    status = TaskStatus.IN_PROGRESS,
                    remainingTimeMillis = currentTask.value!!.remainingTimeMillis - 1000
                )
                currentTask.value?.let { taskRepository.update(it) }
                calculateTodayCompletedTime()
            }
            _timerRunning.value = false
            if (currentTask.value?.remainingTimeMillis == 0L) {
                markTaskCompleted(_currentTask.value!!)
            }
        }
        getTodayTask()
    }

    fun pauseTimer() {
        timerJob?.cancel()
        timerJob = null
        _timerRunning.value = false
        getTodayTask()
    }



    fun markTaskCompleted(task: Task) {
        viewModelScope.launch {
            val updatedTask = task.copy(status = TaskStatus.COMPLETED, remainingTimeMillis = 0L, completedOn = LocalDate.now())
            taskRepository.update(updatedTask)

            selectedDate.value?.let { getTasksForDate(it) }
        }
    }

    fun completedByFinish(){
        viewModelScope.launch {
            val updatedTask = _currentTask.value!!.copy(status = TaskStatus.COMPLETED, remainingTimeMillis = 0L, completedOn = LocalDate.now())
            taskRepository.update(updatedTask)

            selectedDate.value?.let { getTasksForDate(it) }
        }
    }


    //Task Work
    fun getTodayTask(){
        viewModelScope.launch {
            taskRepository.getIncompleteTasksForDate(LocalDate.now()).collect { tasks ->
                _todayTasks.value = tasks
            }
        }
    }
    fun setCurrentTask(task: Task?) {
        currentJob?.cancel()
        _currentTask.value = task
        _remTime.value = _currentTask.value?.remainingTimeMillis ?: 0
    }
    fun getTasksForDate(date: LocalDate) {
        viewModelScope.launch {
            taskRepository.getTasksForDate(date).collect { tasks ->
                _tasksForDate.value = tasks
            }
        }
    }


    fun calculateTodayCompletedTime() {
        viewModelScope.launch {
            _todayCompletedTime.value = _todayCompletedTasks.value.sumOf {
                (it.durationMinutes * 1000L * 60 - it.remainingTimeMillis)/(1000L * 60 * 60)
            }
        }
    }
    fun calculateLast7DaysCompletedTime() {
        viewModelScope.launch {
            _last7DaysCompletedTime.value = _last7DaysCompletedTasks.value.sumOf { it.durationMinutes }
        }
    }



    fun checkForTask(date: LocalDate): Boolean {
        return allTasks.value.any {
            (it.scheduledDate == date && it.repeatInterval == null) || (
                    it.repeatInterval != null && (
                            it.scheduledDate == date ||
                                    (
                                            it.createdAt <= date
                                                    && (it.repeatInterval == RepeatInterval.DAILY
                                                    || (it.repeatInterval == RepeatInterval.WEEKLY && date.dayOfWeek == it.createdAt.dayOfWeek)
                                                    || (it.repeatInterval == RepeatInterval.MONTHLY && date.dayOfMonth == it.createdAt.dayOfMonth)
                                                    || (it.repeatInterval == RepeatInterval.YEARLY && date.month == it.createdAt.month && date.dayOfMonth == it.createdAt.dayOfMonth)
                                                    )
                                            )
                            )
                    )
        }
    }


    //Task Room function
    fun insertTask(task: Task) = viewModelScope.launch {
        taskRepository.insert(task)
    }
    fun updateTask(task: Task) = viewModelScope.launch {
        taskRepository.update(task)
    }
    fun deleteTask(task: Task) = viewModelScope.launch {
        taskRepository.delete(task)
        selectedDate.value?.let { getTasksForDate(it) }
    }


    //Bottom Nav
    fun setShowBottomNav(value: Boolean) {
        _showBottomNav.value = value
    }


    //For Editing Task
    fun setEditTask(task : Task){
        _editTask.value = task
    }
    fun setShowDialog(value : Boolean){
        _showDialog.value = value
    }
    fun doneEditing(){
        _editTask.value = null
    }


    //For Deleting Task
    fun setShowDeleteDialog(value : Boolean){
        _showDeleteDialog.value = value
    }
    fun setDeleteTask(task : Task){
        _deleteTask.value = task
    }
    fun shiftTaskToNextInterval(task: Task) = viewModelScope.launch {
        taskRepository.shiftToNextInterval(task)
        selectedDate.value?.let { getTasksForDate(it) }
    }

    fun setSelectedPeriod(period: String) {
        _selectedPeriod.value = period
    }
}