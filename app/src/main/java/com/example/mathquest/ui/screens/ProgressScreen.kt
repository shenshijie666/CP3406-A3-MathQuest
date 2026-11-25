
package com.example.mathquest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mathquest.data.local.QuizResult
import com.example.mathquest.model.Difficulty
import com.example.mathquest.model.accuracy
import java.text.DateFormat
import java.util.Date

// Displays learning statistics and completed quiz sessions.
@Composable
fun ProgressScreen(
    history: List<QuizResult>,
    historyError: String?
) {

    val totalQuestions = history.sumOf {
        it.totalQuestions
    }

    val correctAnswers = history.sumOf {
        it.correctAnswers
    }

    val percentage = accuracy(
        correctAnswers,
        totalQuestions
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            Text(
                text = "Learning Progress",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Text(
                "Track your improvement at your own pace."
            )
        }

        if (historyError != null) {

            item {
                Text(
                    text = historyError,
                    color = MaterialTheme.colorScheme.error
                )
            }

        } else {

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {

                        Text(
                            text = "Overall Accuracy",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = if (totalQuestions == 0) {
                                "—"
                            } else {
                                "$percentage%"
                            },
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold
                        )

                        LinearProgressIndicator(
                            progress = { percentage / 100f },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            "$correctAnswers of $totalQuestions correct"
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    StatisticCard(
                        title = "Sessions",
                        value = history.size.toString(),
                        modifier = Modifier.weight(1f)
                    )

                    StatisticCard(
                        title = "Questions",
                        value = totalQuestions.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Text(
                    text = "Performance by Difficulty",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            // Calculates performance for each difficulty level.
            items(Difficulty.entries.toList()) { difficulty ->

                val records = history.filter {
                    it.difficulty == difficulty.name
                }

                val total = records.sumOf {
                    it.totalQuestions
                }

                val correct = records.sumOf {
                    it.correctAnswers
                }

                val levelAccuracy = accuracy(correct, total)

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.SpaceBetween
                        ) {
                            Text(
                                difficulty.name,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                if (total == 0) {
                                    "No attempts"
                                } else {
                                    "$levelAccuracy%"
                                }
                            )
                        }

                        LinearProgressIndicator(
                            progress = { levelAccuracy / 100f },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            "$correct / $total correct",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Learning History",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (history.isEmpty()) {

                item {
                    Text(
                        "No completed sessions yet. " +
                                "Start practising to see your progress."
                    )
                }

            } else {

                items(
                    items = history,
                    key = { it.id }
                ) { result ->

                    HistoryCard(result)
                }
            }
        }
    }
}

// Displays a single learning statistic.
@Composable
private fun StatisticCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                title,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                value,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Displays information about a completed learning session.
@Composable
private fun HistoryCard(result: QuizResult) {

    val date = DateFormat
        .getDateTimeInstance(
            DateFormat.MEDIUM,
            DateFormat.SHORT
        )
        .format(Date(result.completedAt))

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Text(
                text = "${result.source} • ${result.difficulty}",
                fontWeight = FontWeight.Bold
            )

            Text(
                "Score: ${result.correctAnswers}/${result.totalQuestions}"
            )

            Text(
                "Accuracy: ${
                    accuracy(
                        result.correctAnswers,
                        result.totalQuestions
                    )
                }%"
            )

            Text(
                date,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
