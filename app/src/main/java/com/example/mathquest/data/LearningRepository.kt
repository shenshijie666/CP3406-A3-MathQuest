
package com.example.mathquest.data

import com.example.mathquest.data.local.QuizResult
import com.example.mathquest.data.local.QuizResultDao
import com.example.mathquest.data.remote.TriviaRemoteDataSource
import com.example.mathquest.model.Difficulty
import com.example.mathquest.model.Question
import com.example.mathquest.model.QuestionFactory
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow

// Contains the questions and source of a learning session.
data class LearningSession(
    val questions: List<Question>,
    val source: String,
    val message: String? = null
)

// Connects local storage, online questions and learning logic.
class LearningRepository(
    private val quizResultDao: QuizResultDao,
    private val remoteDataSource: TriviaRemoteDataSource
) {

    // Observes learning statistics stored in Room.
    val results: Flow<List<QuizResult>> =
        quizResultDao.observeAllResults()

    val sessionCount: Flow<Int> =
        quizResultDao.observeSessionCount()

    val correctAnswers: Flow<Int> =
        quizResultDao.observeCorrectAnswers()

    val totalQuestions: Flow<Int> =
        quizResultDao.observeTotalQuestions()

    // Generates questions without requiring internet access.
    fun createOfflineSession(
        difficulty: Difficulty,
        amount: Int = 5
    ): LearningSession {

        require(amount in 1..50)

        val questions = List(amount) {
            QuestionFactory.create(difficulty)
        }

        return LearningSession(
            questions = questions,
            source = "Offline"
        )
    }

    // Retrieves online questions and handles connection failures.
    suspend fun createOnlineSession(
        difficulty: Difficulty,
        amount: Int = 5
    ): LearningSession {

        require(amount in 1..50)

        return try {

            val questions = remoteDataSource.fetchQuestions(
                difficulty = difficulty,
                amount = amount
            )

            LearningSession(
                questions = questions,
                source = "Online"
            )

        } catch (exception: CancellationException) {

            // Preserves normal coroutine cancellation behaviour.
            throw exception

        } catch (exception: Exception) {

            // Allows learners to continue practising when offline.
            createOfflineSession(
                difficulty = difficulty,
                amount = amount
            ).copy(
                message = "Online questions are unavailable. " +
                        "Switched to offline practice."
            )
        }
    }

    // Saves the result of a completed learning session.
    suspend fun saveResult(
        difficulty: Difficulty,
        correctAnswers: Int,
        totalQuestions: Int,
        source: String
    ): Long {

        require(totalQuestions > 0)
        require(correctAnswers in 0..totalQuestions)

        val result = QuizResult(
            difficulty = difficulty.name,
            correctAnswers = correctAnswers,
            totalQuestions = totalQuestions,
            source = source
        )

        return quizResultDao.insert(result)
    }

    // Allows users to remove their locally stored learning history.
    suspend fun clearHistory() {
        quizResultDao.deleteAllResults()
    }
}
