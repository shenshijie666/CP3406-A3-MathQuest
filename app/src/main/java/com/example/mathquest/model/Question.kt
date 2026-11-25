
package com.example.mathquest.model

import kotlin.random.Random

// Defines the three difficulty levels available to learners.
enum class Difficulty {
    Easy,
    Medium,
    Hard
}

// Represents a mathematics question and its possible answers.
data class Question(
    val prompt: String,
    val options: List<String>,
    val answer: String,
    val explanation: String
) {
    init {
        require(
            options.size == 4 &&
                    options.distinct().size == 4 &&
                    answer in options
        )
    }

    fun isCorrect(choice: String): Boolean {
        return choice == answer
    }
}

// Generates mathematics questions for offline learning.
object QuestionFactory {

    fun create(
        difficulty: Difficulty,
        random: Random = Random.Default
    ): Question {

        val a = random.nextInt(2, 13)
        val b = random.nextInt(2, 13)
        val c = random.nextInt(1, 10)

        val (prompt, answer, explanation) = when (difficulty) {

            Difficulty.Easy -> {
                if (random.nextBoolean()) {
                    Triple(
                        "$a + $b = ?",
                        a + b,
                        "Add $a and $b to get ${a + b}."
                    )
                } else {
                    Triple(
                        "${a + b} − $b = ?",
                        a,
                        "Subtract $b from ${a + b} to get $a."
                    )
                }
            }

            Difficulty.Medium -> {
                if (random.nextBoolean()) {
                    Triple(
                        "$a × $b = ?",
                        a * b,
                        "$a groups of $b make ${a * b}."
                    )
                } else {
                    Triple(
                        "${a * b} ÷ $b = ?",
                        a,
                        "Since $a × $b = ${a * b}, the quotient is $a."
                    )
                }
            }

            Difficulty.Hard -> {
                if (random.nextBoolean()) {
                    Triple(
                        "Solve: ${a}x + $c = ${a * b + c}",
                        b,
                        "Subtract $c from both sides, then divide by $a. x = $b."
                    )
                } else {
                    Triple(
                        "$a + $b × $c = ?",
                        a + b * c,
                        "Multiply first: $b × $c = ${b * c}. Then add $a."
                    )
                }
            }
        }

        // Shuffle four unique options to avoid predictable answers.
        val choices = listOf(
            answer,
            answer + 1,
            answer + 2,
            answer - 1
        ).map { it.toString() }.shuffled(random)

        return Question(
            prompt = prompt,
            options = choices,
            answer = answer.toString(),
            explanation = explanation
        )
    }
}

// Stores the current state of a learning session.
data class QuizState(
    val questions: List<Question> = emptyList(),
    val index: Int = 0,
    val selected: String? = null,
    val score: Int = 0,
    val finished: Boolean = false
) {
    val current: Question?
        get() = questions.getOrNull(index)

    // Prevents learners from scoring multiple times on one question.
    fun answer(choice: String): QuizState {
        val question = current ?: return this

        if (
            finished ||
            selected != null ||
            choice !in question.options
        ) {
            return this
        }

        return copy(
            selected = choice,
            score = score + if (question.isCorrect(choice)) 1 else 0
        )
    }

    // Moves to the next question after an answer is selected.
    fun next(): QuizState {
        return when {
            finished || selected == null -> this

            index == questions.lastIndex -> {
                copy(finished = true)
            }

            else -> {
                copy(
                    index = index + 1,
                    selected = null
                )
            }
        }
    }
}

// Calculates the percentage of correctly answered questions.
fun accuracy(correct: Int, total: Int): Int {
    return if (total <= 0) {
        0
    } else {
        (100.0 * correct / total)
            .toInt()
            .coerceIn(0, 100)
    }
}
