package com.spidex.timepad.data

import android.content.Context
import androidx.room.*
import java.time.LocalDate
import java.time.LocalTime


@Database(entities = [Task::class, TaskInstance::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "task_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class Converters {
    // LocalDate Converter
    @TypeConverter
    fun fromTimestampToLocalDate(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    fun localDateToTimestamp(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }

    // RepeatInterval Converter
    @TypeConverter
    fun toRepeatInterval(value: String?): RepeatInterval? {
        return value?.let { enumValueOf<RepeatInterval>(it) }
    }

    @TypeConverter
    fun fromRepeatInterval(repeatInterval: RepeatInterval?): String? {
        return repeatInterval?.name
    }

    // TaskStatus Converter
    @TypeConverter
    fun toTaskStatus(value: String): TaskInstanceStatus {
        return enumValueOf(value)
    }

    @TypeConverter
    fun fromTaskStatus(status: TaskInstanceStatus): String {
        return status.name
    }

    @TypeConverter
    fun toLocalTime(value: Long?): LocalTime? {
        return value?.let { LocalTime.ofSecondOfDay(it) }
    }

    @TypeConverter
    fun fromLocalTime(time: LocalTime?): Long? {
        return time?.toSecondOfDay()?.toLong()
    }
}