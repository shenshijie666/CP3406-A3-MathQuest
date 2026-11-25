
package com.example.mathquest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mathquest.data.AppContainer
import com.example.mathquest.ui.MathQuestApp
import com.example.mathquest.ui.theme.MathQuestTheme
import com.example.mathquest.viewmodel.LearningViewModel

// Provides the main entry point for the MathQuest application.
class MainActivity : ComponentActivity() {

    private val appContainer by lazy {
        AppContainer(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            val learningViewModel: LearningViewModel =
                viewModel(
                    factory =
                        appContainer.learningViewModelFactory
                )

            MathQuestTheme {

                MathQuestApp(
                    viewModel = learningViewModel
                )
            }
        }
    }
}
