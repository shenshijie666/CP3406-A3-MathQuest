
package com.example.mathquest.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Represents a completed learning session stored locally.
@Entity(tableName = "quiz_results")
data class QuizResult(

    // Automatically generates a unique ID for each session.
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Records the selected learning difficulty.
    val difficulty: String,

    // Stores the number of correct answers.
    val correctAnswers: Int,

    // Stores the total number of questions.
    val totalQuestions: Int,

    // Identifies whether the questions were online or offline.
    val source: String,

    // Records when the learning session was completed.
    val completedAt: Long = System.currentTimeMillis()
)

