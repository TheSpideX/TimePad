package com.spidex.timepad

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class TaskViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    private val _timeSpentLast7Days = MutableStateFlow<List<Double>>(emptyList())
    val timeSpentLast7Days: StateFlow<List<Double>> = _timeSpentLast7Days.asStateFlow()

    private val _todayCompletedTasks = MutableStateFlow<List<Task>>(emptyList())
    val todayCompletedTasks: StateFlow<List<Task>> = _todayCompletedTasks.asStateFlow()

    private val _showDialog = MutableStateFlow(false)
    val showDialog : StateFlow<Boolean> = _showDialog

    private val _last7DaysCompletedTasks = MutableStateFlow<List<Task>>(emptyList())
    val last7DaysCompletedTasks: StateFlow<List<Task>> = _last7DaysCompletedTasks.asStateFlow()

    private val _todayCompletedTime = MutableStateFlow<Long>(0)
    val todayCompletedTime: StateFlow<Long> = _todayCompletedTime.asStateFlow()

    private val _last7DaysCompletedTime = MutableStateFlow<Long>(0)
    val last7DaysCompletedTime: StateFlow<Long> = _last7DaysCompletedTime.asStateFlow()

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth.asStateFlow()

    private val _tasksForDate = MutableStateFlow<List<Task>>(emptyList())
    val tasksForDate: StateFlow<List<Task>> = _tasksForDate.asStateFlow()

    private val _currentTask = MutableStateFlow<Task?>(null)
    val currentTask: StateFlow<Task?> = _currentTask.asStateFlow()

    private val _allTasks = MutableStateFlow<List<Task>>(emptyList())
    val allTasks: StateFlow<List<Task>> = _allTasks.asStateFlow()

    private val _todayTasks = MutableStateFlow<List<Task>>(emptyList())
    val todayTasks: StateFlow<List<Task>> = _todayTasks.asStateFlow()

    private val _selectedDate = MutableStateFlow<LocalDate?>(LocalDate.now())
    val selectedDate: StateFlow<LocalDate?> = _selectedDate.asStateFlow()

    private val _completedTask = MutableStateFlow<List<Task>>(emptyList())
    val completedTask: StateFlow<List<Task>> = _completedTask.asStateFlow()

    private var timerJob: Job? = null
    private val _timerRunning = MutableStateFlow(false)
    val timerRunning: StateFlow<Boolean> = _timerRunning.asStateFlow()

    private val _remTime = MutableStateFlow<Long?>(null)
    val remTime : StateFlow<Long?> = _remTime

    init {
        viewModelScope.launch {
            selectedDate.collectLatest { date ->
                if (date != null) {
                    taskRepository.getTasksForDate(date).collect { tasks ->
                        _tasksForDate.value = tasks
                        if (tasks.isNotEmpty()) {
                            _currentTask.value = tasks[0]
                            _remTime.value = _currentTask.value!!.remainingTimeMillis
                        } else {
                            _currentTask.value = null
                            _remTime.value = 0
                        }
                    }
                } else {
                    _tasksForDate.value = emptyList()
                    _currentTask.value = null
                    _remTime.value = 0
                }
            }
        }

        viewModelScope.launch {
            taskRepository.getTasksByStatus(TaskStatus.COMPLETED).collect(){task->
                _completedTask.value = task
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

    fun getTodayTask(){
        viewModelScope.launch {
            taskRepository.getIncompleteTasksForDate(LocalDate.now()).collect { tasks ->
                _todayTasks.value = tasks
            }
        }
    }

    fun startOrResumeTimer() {
        if (timerJob?.isActive == true) return // Avoid starting if already running
        timerJob = viewModelScope.launch {
            _timerRunning.value = true
            while (_timerRunning.value && currentTask.value != null && currentTask.value!!.remainingTimeMillis > 0) {
                delay(1000)
                _currentTask.value = currentTask.value?.copy(
                    remainingTimeMillis = currentTask.value!!.remainingTimeMillis - 1000
                )
                currentTask.value?.let { taskRepository.update(it) }
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
            _currentTask.value = task.copy(status = TaskStatus.COMPLETED, completedOn = LocalDate.now())
            taskRepository.update(_currentTask.value!!)
        }
    }

    fun setCurrentTask(task: Task) {
        _currentTask.value = task
        _remTime.value = _currentTask.value!!.remainingTimeMillis
    }

    fun calculateTodayCompletedTime() {
        viewModelScope.launch {
            _todayCompletedTime.value = _todayCompletedTasks.value.sumOf { it.durationMinutes }
        }
    }

    fun calculateLast7DaysCompletedTime() {
        viewModelScope.launch {
            _last7DaysCompletedTime.value = _last7DaysCompletedTasks.value.sumOf { it.durationMinutes }
        }
    }

    fun getTasksForDate(date: LocalDate) {
        viewModelScope.launch {
            taskRepository.getTasksForDate(date).collect { tasks ->
                _tasksForDate.value = tasks
            }
        }
    }

    fun checkForTask(date: LocalDate): Boolean {
        return allTasks.value.any {
            // Same logic as in TaskDao.getTasksForDate
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

    fun insertTask(task: Task) = viewModelScope.launch {
        taskRepository.insert(task)
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        taskRepository.update(task)
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        taskRepository.delete(task)
    }
}