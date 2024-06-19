package com.spidex.timepad

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.LocalDate


@Database(entities = [Task::class], version = 1)
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
                ).build() // No migration needed since version is 1
                INSTANCE = instance
                instance
            }
        }
    }
}

// TypeConverters for LocalDate and RepeatInterval
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }

    @TypeConverter
    fun toRepeatInterval(value: String?): RepeatInterval? {
        return value?.let { enumValueOf<RepeatInterval>(it) }
    }

    @TypeConverter
    fun fromRepeatInterval(repeatInterval: RepeatInterval?): String? {
        return repeatInterval?.name
    }
}