
package com.example.mathquest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mathquest.data.preferences.UserSettings
import com.example.mathquest.model.Difficulty

// Provides learning preferences, accessibility and privacy controls.
@Composable
fun SettingsScreen(
    settings: UserSettings,
    isBusy: Boolean,
    onDifficultyChanged: (Difficulty) -> Unit,
    onHighContrastChanged: (Boolean) -> Unit,
    onLargeTextChanged: (Boolean) -> Unit,
    onResetSettings: () -> Unit,
    onClearHistory: () -> Unit
) {

    var showClearDialog by rememberSaveable {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text("Personalise your learning experience.")

        // Allows learners to choose their own difficulty.
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .selectableGroup(),
                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = "Learning Difficulty",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Difficulty.entries.forEach { difficulty ->

                    val description = when (difficulty) {

                        Difficulty.Easy ->
                            "Addition and subtraction"

                        Difficulty.Medium ->
                            "Multiplication and division"

                        Difficulty.Hard ->
                            "Algebra and mixed operations"
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 64.dp)
                            .selectable(
                                selected =
                                    settings.difficulty == difficulty,
                                onClick = {
                                    onDifficultyChanged(difficulty)
                                },
                                role = Role.RadioButton
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        RadioButton(
                            selected =
                                settings.difficulty == difficulty,
                            onClick = null
                        )

                        Spacer(
                            modifier = Modifier.width(12.dp)
                        )

                        Column {

                            Text(
                                difficulty.name,
                                fontWeight = FontWeight.Medium
                            )

                            Text(
                                description,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                Text(
                    "Difficulty changes apply to your next session.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Provides inclusive and accessible design options.
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "Accessibility",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                SettingsToggle(
                    title = "High Contrast",
                    description =
                        "Improve the visibility of text and controls.",
                    checked = settings.highContrast,
                    onCheckedChange = onHighContrastChanged,
                    tag = "high_contrast"
                )

                HorizontalDivider()

                SettingsToggle(
                    title = "Large Quiz Text",
                    description =
                        "Increase the size of questions and answers.",
                    checked = settings.largeText,
                    onCheckedChange = onLargeTextChanged,
                    tag = "large_text"
                )
            }
        }

        // Gives learners control over their stored information.
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "Privacy and Data",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "MathQuest does not require an account, " +
                            "name, email address or location."
                )

                Text(
                    "Completed quiz records are stored in the " +
                            "app's local database."
                )

                Text(
                    "Online challenges contact Open Trivia Database " +
                            "to download mathematics questions. " +
                            "Quiz scores are not sent to the service."
                )

                OutlinedButton(
                    onClick = {
                        showClearDialog = true
                    },
                    enabled = !isBusy,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("clear_history")
                ) {
                    Text(
                        text = "Clear Learning History",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        OutlinedButton(
            onClick = onResetSettings,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset Settings")
        }

        Text(
            text = "About MathQuest",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Version 1.0\n" +
                    "CP3406 Educational App\n" +
                    "Online question source: Open Trivia Database\n" +
                    "Question data licence: CC BY-SA 4.0",
            style = MaterialTheme.typography.bodySmall
        )
    }

    // Requests confirmation before permanently deleting records.
    if (showClearDialog) {

        AlertDialog(
            onDismissRequest = {
                showClearDialog = false
            },
            title = {
                Text("Clear learning history?")
            },
            text = {
                Text(
                    "All saved quiz results and learning statistics " +
                            "will be deleted from the app. " +
                            "This action cannot be undone."
                )
            },
            confirmButton = {
                TextButton(
                    enabled = !isBusy,
                    onClick = {
                        showClearDialog = false
                        onClearHistory()
                    }
                ) {
                    Text("Delete History")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showClearDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

// Creates an accessible preference switch with a descriptive label.
@Composable
private fun SettingsToggle(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    tag: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .testTag(tag)
            .toggleable(
                value = checked,
                role = Role.Switch,
                onValueChange = onCheckedChange
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                title,
                fontWeight = FontWeight.Medium
            )

            Text(
                description,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Switch(
            checked = checked,
            onCheckedChange = null
        )
    }
}
