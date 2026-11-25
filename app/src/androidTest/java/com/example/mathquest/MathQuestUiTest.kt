
package com.example.mathquest

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mathquest.data.local.QuizResult
import com.example.mathquest.data.preferences.UserSettings
import com.example.mathquest.model.Question
import com.example.mathquest.model.QuizState
import com.example.mathquest.ui.screens.HomeScreen
import com.example.mathquest.ui.screens.QuizScreen
import com.example.mathquest.ui.screens.ProgressScreen
import com.example.mathquest.ui.screens.SettingsScreen
import com.example.mathquest.viewmodel.LearningUiState
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MathQuestUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleQuestion = Question(
        prompt = "5 + 3 = ?",
        options = listOf("6", "7", "8", "9"),
        answer = "8",
        explanation = "5 + 3 equals 8."
    )

    // Verifies that both learning modes respond to user interaction.
    @Test
    fun homeScreenLearningButtonsWork() {

        var selectedMode: Boolean? = null

        composeTestRule.setContent {

            MaterialTheme {

                HomeScreen(
                    settings = UserSettings(),
                    history = emptyList(),
                    state = LearningUiState(),
                    historyError = null,
                    onStartQuiz = {
                        selectedMode = it
                    },
                    onResumeQuiz = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("MathQuest")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("start_offline")
            .performScrollTo()
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(false, selectedMode)
        }

        composeTestRule
            .onNodeWithTag("start_online")
            .performScrollTo()
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(true, selectedMode)
        }
    }

    // Verifies answering questions, feedback and quiz progression.
    @Test
    fun quizAnswerAndNextQuestionWork() {

        composeTestRule.setContent {

            var state by remember {
                mutableStateOf(
                    LearningUiState(
                        quiz = QuizState(
                            questions = listOf(
                                sampleQuestion,
                                sampleQuestion
                            )
                        )
                    )
                )
            }

            MaterialTheme {

                QuizScreen(
                    state = state,
                    largeText = false,
                    onAnswer = { choice ->
                        state = state.copy(
                            quiz = state.quiz.answer(choice)
                        )
                    },
                    onNext = {
                        state = state.copy(
                            quiz = state.quiz.next()
                        )
                    },
                    onRetrySave = {},
                    onHome = {},
                    onProgress = {}
                )
            }
        }

        composeTestRule
            .onNodeWithTag("next_question")
            .assertIsNotEnabled()

        composeTestRule
            .onNodeWithTag("answer_2")
            .performScrollTo()
            .performClick()

        composeTestRule
            .onNodeWithText("Correct!")
            .assertExists()

        composeTestRule
            .onNodeWithText("5 + 3 equals 8.")
            .assertExists()

        composeTestRule
            .onNodeWithTag("next_question")
            .assertIsEnabled()
            .performScrollTo()
            .performClick()

        composeTestRule
            .onNodeWithText("Question 2 of 2")
            .assertExists()
    }

    // Verifies that an empty learning session is handled safely.
    @Test
    fun emptyQuizDisplaysHelpfulMessage() {

        composeTestRule.setContent {

            MaterialTheme {

                QuizScreen(
                    state = LearningUiState(),
                    largeText = false,
                    onAnswer = {},
                    onNext = {},
                    onRetrySave = {},
                    onHome = {},
                    onProgress = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText(
                "You have not started a learning session."
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Choose a Learning Mode")
            .assertIsDisplayed()
    }

    // Verifies that learning statistics are displayed correctly.
    @Test
    fun progressScreenDisplaysLearningStatistics() {

        val history = listOf(
            QuizResult(
                id = 1,
                difficulty = "Easy",
                correctAnswers = 4,
                totalQuestions = 5,
                source = "Offline"
            )
        )

        composeTestRule.setContent {

            MaterialTheme {

                ProgressScreen(
                    history = history,
                    historyError = null
                )
            }
        }

        composeTestRule
            .onNodeWithText("Learning Progress")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Overall Accuracy")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("4 of 5 correct")
            .assertIsDisplayed()
    }

    // Creates a settings screen with independently managed test state.
    private fun showSettings(
        onClearHistory: () -> Unit = {}
    ) {

        composeTestRule.setContent {

            var settings by remember {
                mutableStateOf(UserSettings())
            }

            MaterialTheme {

                SettingsScreen(
                    settings = settings,
                    isBusy = false,
                    onDifficultyChanged = {
                        settings = settings.copy(
                            difficulty = it
                        )
                    },
                    onHighContrastChanged = {
                        settings = settings.copy(
                            highContrast = it
                        )
                    },
                    onLargeTextChanged = {
                        settings = settings.copy(
                            largeText = it
                        )
                    },
                    onResetSettings = {
                        settings = UserSettings()
                    },
                    onClearHistory = onClearHistory
                )
            }
        }
    }

    // Verifies that accessibility switches can be enabled.
    @Test
    fun accessibilitySettingsCanBeChanged() {

        showSettings()

        composeTestRule
            .onNodeWithTag("high_contrast")
            .performScrollTo()
            .assertIsOff()
            .performClick()
            .assertIsOn()

        composeTestRule
            .onNodeWithTag("large_text")
            .performScrollTo()
            .assertIsOff()
            .performClick()
            .assertIsOn()
    }

    // Ensures cancelling the dialog does not delete learning records.
    @Test
    fun cancellingClearHistoryDoesNotDeleteRecords() {

        var clearCount = 0

        showSettings {
            clearCount++
        }

        composeTestRule
            .onNodeWithTag("clear_history")
            .performScrollTo()
            .performClick()

        composeTestRule
            .onNodeWithText("Clear learning history?")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Cancel")
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(0, clearCount)
        }

        composeTestRule
            .onNodeWithText("Clear learning history?")
            .assertDoesNotExist()
    }

    // Ensures deletion is requested only after user confirmation.
    @Test
    fun confirmingClearHistoryRequestsDeletion() {

        var clearCount = 0

        showSettings {
            clearCount++
        }

        composeTestRule
            .onNodeWithTag("clear_history")
            .performScrollTo()
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(0, clearCount)
        }

        composeTestRule
            .onNodeWithText("Delete History")
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(1, clearCount)
        }

        composeTestRule
            .onNodeWithText("Clear learning history?")
            .assertDoesNotExist()
    }
}
