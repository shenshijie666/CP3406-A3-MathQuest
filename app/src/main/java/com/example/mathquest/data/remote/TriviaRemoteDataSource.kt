
package com.example.mathquest.data.remote

import com.example.mathquest.model.Difficulty
import com.example.mathquest.model.Question
import java.io.IOException
import java.util.Base64
import java.util.Locale

// Retrieves and converts online questions into application models.
class TriviaRemoteDataSource(
    private val apiService: TriviaApiService
) {

    suspend fun fetchQuestions(
        difficulty: Difficulty,
        amount: Int = 5
    ): List<Question> {

        require(amount in 1..50)

        val response = apiService.getMathQuestions(
            amount = amount,
            difficulty = difficulty.name.lowercase(Locale.ROOT)
        )

        // Handles unsuccessful API responses.
        if (response.responseCode != 0) {
            throw IOException(
                "Trivia API error: ${response.responseCode}"
            )
        }

        val results = response.results.orEmpty()

        if (results.isEmpty()) {
            throw IOException("No online questions available.")
        }

        val questions = results.mapNotNull { item ->

            val prompt = decode(item.question)
            val correct = decode(item.correctAnswer)

            val incorrect = item.incorrectAnswers.map {
                decode(it)
            }

            val options = (incorrect + correct)
                .distinct()
                .shuffled()

            // Rejects questions containing invalid answer options.
            if (
                prompt.isBlank() ||
                options.size != 4 ||
                options.any { it.isBlank() }
            ) {
                null
            } else {
                Question(
                    prompt = prompt,
                    options = options,
                    answer = correct,
                    explanation = "The correct answer is $correct."
                )
            }
        }

        if (questions.size != amount) {
            throw IOException(
                "Not enough valid online questions available."
            )
        }

        return questions
    }

    // Decodes Base64 strings into readable UTF-8 text.
    private fun decode(value: String): String {
        return String(
            Base64.getDecoder().decode(value),
            Charsets.UTF_8
        ).trim()
    }
}
