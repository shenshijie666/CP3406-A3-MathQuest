
package com.example.mathquest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mathquest.data.local.QuizResult
import com.example.mathquest.data.preferences.UserSettings
import com.example.mathquest.model.accuracy
import com.example.mathquest.viewmodel.LearningUiState

// Displays learning options and a summary of student progress.
@Composable
fun HomeScreen(
    settings: UserSettings,
    history: List<QuizResult>,
    state: LearningUiState,
    historyError: String?,
    onStartQuiz: (Boolean) -> Unit,
    onResumeQuiz: () -> Unit
) {
    val totalQuestions = history.sumOf {
        it.totalQuestions
    }

    val correctAnswers = history.sumOf {
        it.correctAnswers
    }

    val hasActiveQuiz =
        state.quiz.questions.isNotEmpty() &&
                !state.quiz.finished

    val busy = state.isLoading ||
            state.isSaving ||
            state.isClearingHistory

    var pendingMode by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    // Confirms before replacing an unfinished learning session.
    fun requestQuiz(online: Boolean) {
        if (hasActiveQuiz) {
            pendingMode = if (online) "Online" else "Offline"
        } else {
            onStartQuiz(online)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        Text(
            text = "MathQuest",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Build your mathematical confidence.",
            style = MaterialTheme.typography.bodyLarge
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Learning Overview",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text("Difficulty: ${settings.difficulty.name}")

                if (historyError == null) {
                    Text("Completed sessions: ${history.size}")
                    Text("Questions answered: $totalQuestions")

                    Text(
                        text = if (totalQuestions == 0) {
                            "Accuracy: Not available yet"
                        } else {
                            "Accuracy: ${
                                accuracy(correctAnswers, totalQuestions)
                            }%"
                        }
                    )
                } else {
                    Text(
                        text = historyError,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Text(
            text = "Choose a learning mode",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        if (hasActiveQuiz || state.isLoading || state.isSaving) {
            OutlinedButton(
                onClick = onResumeQuiz,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Resume learning session")
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Offline Practice",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Practise five randomly generated questions " +
                            "with step-by-step explanations."
                )

                Text(
                    "No internet connection required.",
                    style = MaterialTheme.typography.bodySmall
                )

                Button(
                    onClick = { requestQuiz(false) },
                    enabled = !busy,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("start_offline")
                ) {
                    Text("Start Practice")
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Online Challenge",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Explore mathematics questions from " +
                            "the Open Trivia Database."
                )

                OutlinedButton(
                    onClick = { requestQuiz(true) },
                    enabled = !busy,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("start_online")
                ) {
                    Text("Start Online Challenge")
                }
            }
        }

        HorizontalDivider()

        Text(
            text = "Learn at your own pace",
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "No timers, no competitive rankings and " +
                    "no personal account required.",
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = "Learning records are saved in the app's local database.",
            style = MaterialTheme.typography.bodySmall
        )
    }

    if (pendingMode != null) {
        AlertDialog(
            onDismissRequest = { pendingMode = null },
            title = { Text("Start a new session?") },
            text = {
                Text(
                    "Your unfinished session will be replaced."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val online = pendingMode == "Online"
                        pendingMode = null
                        onStartQuiz(online)
                    }
                ) {
                    Text("Start New")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { pendingMode = null }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
