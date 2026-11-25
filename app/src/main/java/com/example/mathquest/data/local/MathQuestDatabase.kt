
package com.example.mathquest.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Defines the main database used by MathQuest.
@Database(
    entities = [QuizResult::class],
    version = 1,
    exportSchema = true
)
abstract class MathQuestDatabase : RoomDatabase() {

    abstract fun quizResultDao(): QuizResultDao

    companion object {

        @Volatile
        private var INSTANCE: MathQuestDatabase? = null

        // Creates a single shared database instance.
        fun getDatabase(context: Context): MathQuestDatabase {
            return INSTANCE ?: synchronized(this) {

                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    MathQuestDatabase::class.java,
                    "mathquest_database"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}
