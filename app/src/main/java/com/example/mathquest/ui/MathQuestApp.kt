
package com.example.mathquest.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mathquest.ui.screens.HomeScreen
import com.example.mathquest.ui.screens.QuizScreen
import com.example.mathquest.viewmodel.LearningViewModel

// Defines the four main navigation destinations.
enum class Screen(
    val route: String,
    val label: String,
    val symbol: String
) {
    Home("home", "Home", "⌂"),
    Learn("learn", "Learn", "∑"),
    Progress("progress", "Progress", "▥"),
    Settings("settings", "Settings", "⚙")
}

// Connects application screens using Navigation Compose.
@Composable
fun MathQuestApp(
    viewModel: LearningViewModel
) {
    val navController = rememberNavController()

    val backStackEntry by
    navController.currentBackStackEntryAsState()

    val currentRoute =
        backStackEntry?.destination?.route

    val state by
    viewModel.uiState.collectAsStateWithLifecycle()

    val settings by
    viewModel.settings.collectAsStateWithLifecycle()

    val history by
    viewModel.history.collectAsStateWithLifecycle()

    val historyError by
    viewModel.historyError.collectAsStateWithLifecycle()

    // Prevents duplicate destinations in the navigation back stack.
    fun navigateTo(screen: Screen) {

        if (
            navController.currentDestination?.route ==
            screen.route
        ) return

        navController.navigate(screen.route) {

            popUpTo(
                navController.graph.findStartDestination().id
            ) {
                saveState = true
            }

            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {

            NavigationBar {

                Screen.entries.forEach { screen ->

                    NavigationBarItem(
                        selected = currentRoute == screen.route,
                        onClick = {
                            navigateTo(screen)
                        },
                        icon = {
                            Text(
                                text = screen.symbol,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.clearAndSetSemantics { }
                            )
                        },
                        label = {
                            Text(screen.label)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            composable(Screen.Home.route) {

                HomeScreen(
                    settings = settings,
                    history = history,
                    state = state,
                    historyError = historyError,
                    onStartQuiz = { online ->
                        viewModel.startQuiz(online)
                        navigateTo(Screen.Learn)
                    },
                    onResumeQuiz = {
                        navigateTo(Screen.Learn)
                    }
                )
            }

            composable(Screen.Learn.route) {

                QuizScreen(
                    state = state,
                    largeText = settings.largeText,
                    onAnswer = viewModel::answerQuestion,
                    onNext = viewModel::nextQuestion,
                    onRetrySave = viewModel::retrySave,
                    onHome = {
                        navigateTo(Screen.Home)
                    },
                    onProgress = {
                        navigateTo(Screen.Progress)
                    }
                )
            }

            composable(Screen.Progress.route) {

                PlaceholderScreen(
                    title = "Learning Progress"
                )
            }

            composable(Screen.Settings.route) {

                PlaceholderScreen(
                    title = "Settings"
                )
            }
        }
    }
}

// Temporarily reserves destinations for the remaining screens.
@Composable
private fun PlaceholderScreen(title: String) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Coming soon")
    }
}
