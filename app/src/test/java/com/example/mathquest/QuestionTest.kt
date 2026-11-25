
package com.example.mathquest

import com.example.mathquest.model.Difficulty
import com.example.mathquest.model.Question
import com.example.mathquest.model.QuestionFactory
import com.example.mathquest.model.QuizState
import com.example.mathquest.model.accuracy
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class QuestionTest {

    private val question = Question(
        prompt = "5 + 3 = ?",
        options = listOf("6", "7", "8", "9"),
        answer = "8",
        explanation = "5 + 3 equals 8."
    )

    // Checks whether a correct answer is recognised.
    @Test
    fun correctAnswerIsRecognised() {
        assertTrue(question.isCorrect("8"))
    }

    // Checks whether an incorrect answer is rejected.
    @Test
    fun incorrectAnswerIsRejected() {
        assertFalse(question.isCorrect("7"))
    }

    // Ensures generated questions contain four unique options.
    @Test
    fun generatedQuestionsHaveValidOptions() {
        val random = Random(42)

        for (difficulty in Difficulty.entries) {
            repeat(100) {
                val generated =
                    QuestionFactory.create(difficulty, random)

                assertEquals(4, generated.options.size)
                assertEquals(4, generated.options.distinct().size)
                assertTrue(generated.answer in generated.options)
                assertTrue(generated.prompt.isNotBlank())
                assertTrue(generated.explanation.isNotBlank())
            }
        }
    }

    // Rejects questions with invalid answer options.
    @Test(expected = IllegalArgumentException::class)
    fun invalidQuestionIsRejected() {
        Question(
            prompt = "2 + 2 = ?",
            options = listOf("1", "2", "3", "5"),
            answer = "4",
            explanation = "2 + 2 equals 4."
        )
    }

    // A correct answer increases the score.
    @Test
    fun correctAnswerIncreasesScore() {
        val state = QuizState(
            questions = listOf(question)
        )

        val result = state.answer("8")

        assertEquals(1, result.score)
        assertEquals("8", result.selected)
    }

    // An incorrect answer does not increase the score.
    @Test
    fun incorrectAnswerDoesNotIncreaseScore() {
        val state = QuizState(
            questions = listOf(question)
        )

        val result = state.answer("7")

        assertEquals(0, result.score)
    }

    // Prevents scoring more than once for one question.
    @Test
    fun repeatedAnswerDoesNotIncreaseScore() {
        val state = QuizState(
            questions = listOf(question)
        )

        val result = state.answer("8").answer("8")

        assertEquals(1, result.score)
    }

    // Rejects answers that are not in the options.
    @Test
    fun invalidChoiceIsIgnored() {
        val state = QuizState(
            questions = listOf(question)
        )

        assertEquals(state, state.answer("100"))
    }

    // Prevents skipping unanswered questions.
    @Test
    fun unansweredQuestionCannotBeSkipped() {
        val state = QuizState(
            questions = listOf(question, question)
        )

        assertEquals(state, state.next())
    }

    // Verifies navigation to the next question.
    @Test
    fun nextQuestionUpdatesIndex() {
        val state = QuizState(
            questions = listOf(question, question)
        )

        val result = state.answer("8").next()

        assertEquals(1, result.index)
        assertNull(result.selected)
        assertEquals(1, result.score)
        assertFalse(result.finished)
    }

    // Verifies completion of the final question.
    @Test
    fun quizFinishesAfterLastQuestion() {
        val state = QuizState(
            questions = listOf(question)
        )

        val result = state.answer("8").next()

        assertTrue(result.finished)
        assertEquals(1, result.score)
    }

    // Checks the accuracy calculation.
    @Test
    fun accuracyIsCalculatedCorrectly() {
        assertEquals(80, accuracy(4, 5))
        assertEquals(100, accuracy(5, 5))
        assertEquals(0, accuracy(0, 5))
    }

    // Handles empty learning records safely.
    @Test
    fun zeroQuestionsReturnZeroAccuracy() {
        assertEquals(0, accuracy(0, 0))
    }

    // Keeps accuracy within a valid percentage range.
    @Test
    fun accuracyRemainsWithinValidRange() {
        assertEquals(100, accuracy(10, 5))
        assertEquals(0, accuracy(-1, 5))
    }
}
