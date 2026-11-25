
package com.example.mathquest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mathquest.data.AppContainer
import com.example.mathquest.model.accuracy
import com.example.mathquest.ui.theme.MathQuestTheme
import com.example.mathquest.viewmodel.LearningViewModel

class MainActivity : ComponentActivity() {

    // Creates the application's dependency container.
    private val appContainer by lazy {
        AppContainer(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            // Creates the ViewModel with its required dependencies.
            val learningViewModel: LearningViewModel =
                viewModel(
                    factory =
                        appContainer.learningViewModelFactory
                )

            val settings by
            learningViewModel.settings.collectAsStateWithLifecycle()

            val history by
            learningViewModel.history.collectAsStateWithLifecycle()

            val historyError by
            learningViewModel.historyError.collectAsStateWithLifecycle()

            val totalQuestions =
                history.sumOf { it.totalQuestions }

            val correctAnswers =
                history.sumOf { it.correctAnswers }

            MathQuestTheme {

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(20.dp)
                    ) {

                        Text(
                            text = "MathQuest",
                            style =
                                MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Learn mathematics at your own pace.",
                            style =
                                MaterialTheme.typography.bodyLarge
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
                                verticalArrangement =
                                    Arrangement.spacedBy(12.dp)
                            ) {

                                Text(
                                    text = "Learning Overview",
                                    style =
                                        MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "Difficulty: ${settings.difficulty.name}"
                                )

                                if (historyError == null) {

                                    Text(
                                        text = "Completed sessions: ${history.size}"
                                    )

                                    Text(
                                        text = "Questions answered: $totalQuestions"
                                    )

                                    Text(
                                        text = "Accuracy: ${
                                            accuracy(
                                                correctAnswers,
                                                totalQuestions
                                            )
                                        }%"
                                    )

                                } else {

                                    Text(
                                        text = historyError.orEmpty(),
                                        color =
                                            MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement =
                                    Arrangement.spacedBy(10.dp)
                            ) {

                                Text(
                                    text = "Explore Mathematics",
                                    style =
                                        MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                Text("Addition and subtraction")
                                Text("Multiplication and division")
                                Text("Algebra and problem-solving")
                            }
                        }

                        Text(
                            text = "Your learning progress is stored locally.",
                            style =
                                MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
