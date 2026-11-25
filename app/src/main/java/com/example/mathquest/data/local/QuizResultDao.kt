
package com.example.mathquest.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// Provides database operations for learning records.
@Dao
interface QuizResultDao {

    // Saves a completed quiz session.
    @Insert
    suspend fun insert(result: QuizResult): Long

    // Observes all learning records, with recent sessions first.
    @Query(
        "SELECT * FROM quiz_results ORDER BY completedAt DESC, id DESC"
    )
    fun observeAllResults(): Flow<List<QuizResult>>

    // Observes the total number of completed sessions.
    @Query("SELECT COUNT(*) FROM quiz_results")
    fun observeSessionCount(): Flow<Int>

    // Calculates the total number of correct answers.
    @Query(
        "SELECT COALESCE(SUM(correctAnswers), 0) FROM quiz_results"
    )
    fun observeCorrectAnswers(): Flow<Int>

    // Calculates the total number of answered questions.
    @Query(
        "SELECT COALESCE(SUM(totalQuestions), 0) FROM quiz_results"
    )
    fun observeTotalQuestions(): Flow<Int>

    // Allows learners to permanently remove their learning history.
    @Query("DELETE FROM quiz_results")
    suspend fun deleteAllResults()
}
