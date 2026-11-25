
package com.example.mathquest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mathquest.data.LearningRepository
import com.example.mathquest.data.local.QuizResult
import com.example.mathquest.data.preferences.SettingsDataStore
import com.example.mathquest.data.preferences.UserSettings
import com.example.mathquest.model.Difficulty
import com.example.mathquest.model.QuizState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Represents the current state of the learning interface.
data class LearningUiState(
    val quiz: QuizState = QuizState(),
    val difficulty: Difficulty = Difficulty.Easy,
    val source: String = "Offline",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val isClearingHistory: Boolean = false,
    val message: String? = null,
    val saveError: String? = null
)

// Connects the user interface with application data and logic.
class LearningViewModel(
    private val repository: LearningRepository,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(LearningUiState())
    val uiState = _uiState.asStateFlow()

    private val _operationMessage = MutableStateFlow<String?>(null)
    val operationMessage = _operationMessage.asStateFlow()

    private val _historyError = MutableStateFlow<String?>(null)
    val historyError = _historyError.asStateFlow()

    // Observes the learner's persistent preferences.
    val settings: StateFlow<UserSettings> =
        settingsDataStore.settings
            .catch {
                _operationMessage.value = "Unable to load settings."
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = UserSettings()
            )

    // Observes learning records from the Room database.
    val history: StateFlow<List<QuizResult>> =
        repository.results
            .catch {
                _historyError.value =
                    "Unable to load learning history."
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // Starts an online or offline learning session.
    fun startQuiz(online: Boolean) {

        val current = _uiState.value

        if (
            current.isLoading ||
            current.isSaving ||
            current.isClearingHistory
        ) return

        val difficulty = settings.value.difficulty

        _uiState.value = LearningUiState(
            difficulty = difficulty,
            isLoading = true
        )

        viewModelScope.launch {

            try {

                val session = if (online) {
                    repository.createOnlineSession(difficulty)
                } else {
                    repository.createOfflineSession(difficulty)
                }

                _uiState.value = LearningUiState(
                    quiz = QuizState(
                        questions = session.questions
                    ),
                    difficulty = difficulty,
                    source = session.source,
                    message = session.message
                )

            } catch (exception: CancellationException) {

                throw exception

            } catch (exception: Exception) {

                _uiState.value = LearningUiState(
                    difficulty = difficulty,
                    message = "Unable to start the learning session."
                )
            }
        }
    }

    // Records the learner's selected answer.
    fun answerQuestion(choice: String) {

        val current = _uiState.value

        _uiState.value = current.copy(
            quiz = current.quiz.answer(choice)
        )
    }

    // Advances the quiz and saves the completed result.
    fun nextQuestion() {

        val current = _uiState.value

        if (
            current.isSaving ||
            current.isClearingHistory ||
            current.quiz.finished
        ) return

        val updatedQuiz = current.quiz.next()

        _uiState.value = current.copy(
            quiz = updatedQuiz
        )

        if (updatedQuiz.finished) {
            saveCompletedQuiz()
        }
    }

    // Prevents duplicate records for a completed session.
    private fun saveCompletedQuiz() {

        val current = _uiState.value

        if (
            !current.quiz.finished ||
            current.isSaving ||
            current.isSaved ||
            current.isClearingHistory
        ) return

        _uiState.value = current.copy(
            isSaving = true,
            saveError = null
        )

        viewModelScope.launch {

            try {

                repository.saveResult(
                    difficulty = current.difficulty,
                    correctAnswers = current.quiz.score,
                    totalQuestions = current.quiz.questions.size,
                    source = current.source
                )

                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    isSaved = true
                )

            } catch (exception: CancellationException) {

                throw exception

            } catch (exception: Exception) {

                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveError = "Unable to save the quiz result."
                )
            }
        }
    }

    // Allows the user to retry a failed database operation.
    fun retrySave() {
        saveCompletedQuiz()
    }

    // Updates the selected learning difficulty.
    fun setDifficulty(difficulty: Difficulty) {
        updateSettings {
            settingsDataStore.setDifficulty(difficulty)
        }
    }

    // Updates the high contrast accessibility preference.
    fun setHighContrast(enabled: Boolean) {
        updateSettings {
            settingsDataStore.setHighContrast(enabled)
        }
    }

    // Updates the large text accessibility preference.
    fun setLargeText(enabled: Boolean) {
        updateSettings {
            settingsDataStore.setLargeText(enabled)
        }
    }

    // Restores default application settings.
    fun resetSettings() {
        updateSettings {
            settingsDataStore.resetSettings()
        }
    }

    // Handles preference updates and storage errors.
    private fun updateSettings(
        action: suspend () -> Unit
    ) {
        viewModelScope.launch {
            try {
                action()
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _operationMessage.value =
                    "Unable to update settings."
            }
        }
    }

    // Removes learning records at the user's request.
    fun clearHistory() {

        val current = _uiState.value

        if (
            current.isLoading ||
            current.isSaving ||
            current.isClearingHistory
        ) return

        _uiState.value = current.copy(
            isClearingHistory = true
        )

        viewModelScope.launch {

            try {

                repository.clearHistory()

                _operationMessage.value =
                    "Learning history cleared."

            } catch (exception: CancellationException) {

                throw exception

            } catch (exception: Exception) {

                _operationMessage.value =
                    "Unable to clear learning history."

            } finally {

                _uiState.value = _uiState.value.copy(
                    isClearingHistory = false
                )
            }
        }
    }

    // Clears a message after it has been displayed.
    fun dismissOperationMessage() {
        _operationMessage.value = null
    }
}
