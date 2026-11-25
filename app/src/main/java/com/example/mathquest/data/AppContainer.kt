
package com.example.mathquest.data

import android.content.Context
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.mathquest.data.local.MathQuestDatabase
import com.example.mathquest.data.preferences.SettingsDataStore
import com.example.mathquest.data.remote.TriviaApiService
import com.example.mathquest.data.remote.TriviaRemoteDataSource
import com.example.mathquest.viewmodel.LearningViewModel

// Provides the dependencies required by the application.
class AppContainer(context: Context) {

    private val applicationContext =
        context.applicationContext

    // Creates the shared Room database.
    private val database =
        MathQuestDatabase.getDatabase(applicationContext)

    // Creates the Retrofit network service.
    private val apiService =
        TriviaApiService.create()

    // Provides access to online mathematics questions.
    private val remoteDataSource =
        TriviaRemoteDataSource(apiService)

    // Connects the database and network data sources.
    private val repository =
        LearningRepository(
            quizResultDao = database.quizResultDao(),
            remoteDataSource = remoteDataSource
        )

    // Provides persistent application preferences.
    private val settingsDataStore =
        SettingsDataStore(applicationContext)

    // Injects the required dependencies into the ViewModel.
    val learningViewModelFactory = viewModelFactory {
        initializer {
            LearningViewModel(
                repository = repository,
                settingsDataStore = settingsDataStore
            )
        }
    }
}
