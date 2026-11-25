
package com.example.mathquest

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mathquest.data.local.MathQuestDatabase
import com.example.mathquest.data.local.QuizResult
import com.example.mathquest.data.local.QuizResultDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuizResultDaoTest {

    private lateinit var database: MathQuestDatabase
    private lateinit var dao: QuizResultDao

    // Creates an isolated database before each test.
    @Before
    fun setup() {

        val context =
            ApplicationProvider.getApplicationContext<Context>()

        database = Room.inMemoryDatabaseBuilder(
            context,
            MathQuestDatabase::class.java
        ).build()

        dao = database.quizResultDao()
    }

    // Releases database resources after each test.
    @After
    fun tearDown() {
        database.close()
    }

    // Creates sample learning records for testing.
    private fun createResult(
        correct: Int = 4,
        total: Int = 5,
        difficulty: String = "Easy",
        completedAt: Long = 1000L
    ): QuizResult {

        return QuizResult(
            difficulty = difficulty,
            correctAnswers = correct,
            totalQuestions = total,
            source = "Offline",
            completedAt = completedAt
        )
    }

    // Verifies that a completed quiz can be saved and retrieved.
    @Test
    fun insertAndReadQuizResult() = runBlocking {

        val result = createResult()

        val id = dao.insert(result)

        val records = dao.observeAllResults().first()

        assertTrue(id > 0)
        assertEquals(1, records.size)
        assertEquals(4, records.first().correctAnswers)
        assertEquals(5, records.first().totalQuestions)
        assertEquals("Easy", records.first().difficulty)
        assertEquals("Offline", records.first().source)
    }

    // Verifies the number of completed learning sessions.
    @Test
    fun sessionCountIsCorrect() = runBlocking {

        dao.insert(createResult())
        dao.insert(createResult())

        val count = dao.observeSessionCount().first()

        assertEquals(2, count)
    }

    // Verifies the total number of correct answers.
    @Test
    fun correctAnswersAreCalculatedCorrectly() = runBlocking {

        dao.insert(createResult(correct = 4))
        dao.insert(createResult(correct = 3))

        val correct = dao.observeCorrectAnswers().first()

        assertEquals(7, correct)
    }

    // Verifies the total number of answered questions.
    @Test
    fun totalQuestionsAreCalculatedCorrectly() = runBlocking {

        dao.insert(createResult(total = 5))
        dao.insert(createResult(total = 10))

        val total = dao.observeTotalQuestions().first()

        assertEquals(15, total)
    }

    // Ensures the most recent learning session appears first.
    @Test
    fun learningHistoryIsOrderedByDate() = runBlocking {

        dao.insert(
            createResult(
                difficulty = "Easy",
                completedAt = 1000L
            )
        )

        dao.insert(
            createResult(
                difficulty = "Hard",
                completedAt = 2000L
            )
        )

        val records = dao.observeAllResults().first()

        assertEquals("Hard", records[0].difficulty)
        assertEquals("Easy", records[1].difficulty)
    }

    // Ensures an empty database returns safe default values.
    @Test
    fun emptyDatabaseReturnsZeroStatistics() = runBlocking {

        assertTrue(
            dao.observeAllResults().first().isEmpty()
        )

        assertEquals(
            0,
            dao.observeSessionCount().first()
        )

        assertEquals(
            0,
            dao.observeCorrectAnswers().first()
        )

        assertEquals(
            0,
            dao.observeTotalQuestions().first()
        )
    }

    // Verifies that users can remove all learning records.
    @Test
    fun clearHistoryRemovesAllRecords() = runBlocking {

        dao.insert(createResult())
        dao.insert(createResult())

        dao.deleteAllResults()

        val records = dao.observeAllResults().first()

        assertTrue(records.isEmpty())
        assertEquals(0, dao.observeSessionCount().first())
        assertEquals(0, dao.observeCorrectAnswers().first())
        assertEquals(0, dao.observeTotalQuestions().first())
    }
}
