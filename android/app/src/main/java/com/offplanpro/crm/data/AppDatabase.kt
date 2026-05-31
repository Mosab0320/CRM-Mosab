package com.offplanpro.crm.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.offplanpro.crm.data.dao.*
import com.offplanpro.crm.data.entity.*

@Database(
    entities = [
        Lead::class,
        Deal::class,
        Project::class,
        CrmUnit::class,
        CrmTask::class,
        FollowUp::class,
        Goal::class,
        ActivityItem::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun leadDao(): LeadDao
    abstract fun dealDao(): DealDao
    abstract fun projectDao(): ProjectDao
    abstract fun unitDao(): UnitDao
    abstract fun taskDao(): TaskDao
    abstract fun followUpDao(): FollowUpDao
    abstract fun goalDao(): GoalDao
    abstract fun activityDao(): ActivityDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "offplanpro_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
