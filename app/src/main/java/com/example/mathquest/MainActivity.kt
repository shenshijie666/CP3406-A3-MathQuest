
package com.example.mathquest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mathquest.data.AppContainer
import com.example.mathquest.ui.MathQuestApp
import com.example.mathquest.ui.theme.MathQuestTheme
import com.example.mathquest.viewmodel.LearningViewModel

// Provides a high contrast colour scheme for improved readability.
private val HighContrastColors = lightColorScheme(

    primary = Color(0xFF003366),
    onPrimary = Color.White,

    primaryContainer = Color(0xFFDCEBFF),
    onPrimaryContainer = Color.Black,

    secondary = Color(0xFF333333),
    onSecondary = Color.White,

    secondaryContainer = Color(0xFFE5E5E5),
    onSecondaryContainer = Color.Black,

    tertiary = Color(0xFF00552B),
    onTertiary = Color.White,

    tertiaryContainer = Color(0xFFD9F2DF),
    onTertiaryContainer = Color.Black,

    background = Color.White,
    onBackground = Color.Black,

    surface = Color.White,
    onSurface = Color.Black,
    onSurfaceVariant = Color.Black,

    surfaceVariant = Color(0xFFE5E5E5),
    surfaceContainer = Color(0xFFF0F0F0),
    surfaceContainerHigh = Color(0xFFE8E8E8),
    surfaceContainerHighest = Color(0xFFE0E0E0),

    outline = Color.Black,
    outlineVariant = Color(0xFF555555),

    error = Color(0xFF990000),
    onError = Color.White,

    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color.Black
)

// Provides the main entry point for MathQuest.
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

            val settings by
            learningViewModel.settings
                .collectAsStateWithLifecycle()

            val systemDarkTheme = isSystemInDarkTheme()

            // Keeps system bar icons visible in high contrast mode.
            val useDarkIcons =
                settings.highContrast || !systemDarkTheme

            SideEffect {

                val controller =
                    WindowCompat.getInsetsController(
                        window,
                        window.decorView
                    )

                controller.isAppearanceLightStatusBars =
                    useDarkIcons

                controller.isAppearanceLightNavigationBars =
                    useDarkIcons
            }

            MathQuestTheme {

                // Applies accessibility preferences without
                // recreating the navigation controller.
                MaterialTheme(
                    colorScheme =
                        if (settings.highContrast) {
                            HighContrastColors
                        } else {
                            MaterialTheme.colorScheme
                        },
                    typography = MaterialTheme.typography,
                    shapes = MaterialTheme.shapes
                ) {

                    MathQuestApp(
                        viewModel = learningViewModel
                    )
                }
            }
        }
    }
}
