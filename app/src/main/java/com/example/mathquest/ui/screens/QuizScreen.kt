
package com.example.mathquest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mathquest.model.accuracy
import com.example.mathquest.viewmodel.LearningUiState

// Displays questions, answer feedback and completed quiz results.
@Composable
fun QuizScreen(
    state: LearningUiState,
    largeText: Boolean,
    onAnswer: (String) -> Unit,
    onNext: () -> Unit,
    onRetrySave: () -> Unit,
    onHome: () -> Unit,
    onProgress: () -> Unit
) {
    val quiz = state.quiz

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Learning Activity",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        state.message?.let { message ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        when {

            // Shows feedback while online questions are loading.
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text("Preparing your questions...")
                    }
                }
            }

            // Displays a summary after all questions are answered.
            quiz.finished -> {

                val percentage = accuracy(
                    quiz.score,
                    quiz.questions.size
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Session Complete!",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "$percentage%",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            "${quiz.score} of ${quiz.questions.size} correct"
                        )

                        LinearProgressIndicator(
                            progress = { percentage / 100f },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = when {
                                percentage == 100 ->
                                    "Excellent work!"

                                percentage >= 60 ->
                                    "Good progress. Keep practising!"

                                else ->
                                    "Every practice session helps you learn."
                            }
                        )
                    }
                }

                when {
                    state.isSaving -> {
                        Text("Saving learning record...")
                    }

                    state.isSaved -> {
                        Text("Your learning record has been saved.")
                    }

                    state.saveError != null -> {
                        Text(
                            text = state.saveError,
                            color = MaterialTheme.colorScheme.error
                        )

                        OutlinedButton(
                            onClick = onRetrySave,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Retry Save")
                        }
                    }
                }

                Button(
                    onClick = onProgress,
                    enabled = !state.isSaving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View Learning Progress")
                }

                OutlinedButton(
                    onClick = onHome,
                    enabled = !state.isSaving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Return Home")
                }
            }

            // Handles an empty learning session safely.
            quiz.current == null -> {

                Text("You have not started a learning session.")

                Button(
                    onClick = onHome,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Choose a Learning Mode")
                }
            }

            else -> {

                val question = quiz.current ?: return@Column
                val answered = quiz.selected != null

                Text(
                    text = "${state.source} • ${state.difficulty.name}",
                    style = MaterialTheme.typography.labelLarge
                )

                Text(
                    text = "Question ${quiz.index + 1} of " +
                            "${quiz.questions.size}"
                )

                LinearProgressIndicator(
                    progress = {
                        (quiz.index + if (answered) 1 else 0).toFloat() /
                                quiz.questions.size
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Choose the correct answer",
                            style = MaterialTheme.typography.labelLarge
                        )

                        Text(
                            text = question.prompt,
                            fontSize = if (largeText) 32.sp else 24.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = if (largeText) 40.sp else 32.sp
                        )
                    }
                }

                // Displays four answer options.
                question.options.forEachIndexed { index, option ->

                    val correctOption =
                        answered && option == question.answer

                    val wrongSelection =
                        answered &&
                                option == quiz.selected &&
                                option != question.answer

                    val optionColor = when {
                        correctOption ->
                            MaterialTheme.colorScheme.tertiaryContainer

                        wrongSelection ->
                            MaterialTheme.colorScheme.errorContainer

                        else ->
                            MaterialTheme.colorScheme.surface
                    }

                    OutlinedButton(
                        onClick = { onAnswer(option) },
                        enabled = !answered,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 56.dp)
                            .testTag("answer_$index"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = optionColor,
                            disabledContainerColor = optionColor,
                            disabledContentColor =
                                MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text(
                            text = when {
                                correctOption ->
                                    "$option — Correct answer"

                                wrongSelection ->
                                    "$option — Your answer"

                                else -> option
                            },
                            fontSize = if (largeText) 22.sp else 18.sp
                        )
                    }
                }

                // Provides immediate and supportive learning feedback.
                if (answered) {

                    val correct =
                        quiz.selected == question.answer

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (correct) {
                                MaterialTheme.colorScheme.tertiaryContainer
                            } else {
                                MaterialTheme.colorScheme.secondaryContainer
                            }
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (correct) {
                                    "Correct!"
                                } else {
                                    "Not quite. Keep learning!"
                                },
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = question.explanation,
                                fontSize = if (largeText) 20.sp else 16.sp
                            )
                        }
                    }
                }

                Text(
                    text = "Current score: ${quiz.score}",
                    fontWeight = FontWeight.Medium
                )

                Button(
                    onClick = onNext,
                    enabled = answered &&
                            !state.isSaving &&
                            !state.isClearingHistory,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("next_question")
                ) {
                    Text(
                        if (quiz.index == quiz.questions.lastIndex) {
                            "Finish Quiz"
                        } else {
                            "Next Question"
                        }
                    )
                }
            }
        }
    }
}
