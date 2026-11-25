# MathQuest – Interactive Mathematics Learning App

## Project Overview

MathQuest is an educational Android application developed for CP3406 Mobile Computing Assessment 3 at James Cook University Singapore.

The application is designed for secondary school students aged 13–16. It helps learners improve their mathematical skills through interactive quizzes, immediate feedback, difficulty-based exercises, and personalised learning statistics.

The project also incorporates ethical design principles, including privacy protection, accessibility, transparency, and user autonomy.

## Core Features

### 1. Home Screen

- Displays a summary of learning progress.
- Provides offline practice and online challenge modes.
- Allows learners to resume unfinished sessions.
- Displays the selected difficulty and overall accuracy.

### 2. Learning Activity

- Generates random mathematics questions.
- Provides five questions per learning session.
- Supports three difficulty levels:
    - Easy: Addition and subtraction.
    - Medium: Multiplication and division.
    - Hard: Algebra and mixed operations.
- Provides immediate answer feedback.
- Displays explanations for locally generated questions.
- Prevents duplicate scoring.
- Automatically saves completed quiz results.

### 3. Learning Progress

- Displays overall learning accuracy.
- Tracks completed learning sessions.
- Records the total number of answered questions.
- Displays performance by difficulty level.
- Provides historical learning records with timestamps.
- Stores completed results using Room Database.

### 4. Settings

- Allows learners to select their preferred difficulty.
- Supports high contrast mode.
- Supports larger quiz text.
- Allows users to reset their preferences.
- Provides a confirmation dialog before deleting learning history.
- Stores user preferences using DataStore.

## Technology Stack

| Technology | Purpose |
|------------|---------|
| Kotlin | Primary programming language |
| Jetpack Compose | Declarative user interface |
| Material Design 3 | Modern and accessible UI design |
| Navigation Compose | Screen navigation and back stack management |
| ViewModel | UI state and business logic management |
| StateFlow | Reactive state updates |
| Room Database | Persistent learning records |
| Retrofit | Internet API integration |
| Gson | JSON deserialization |
| DataStore | Persistent user preferences |
| KSP | Room database code generation |
| Coroutines | Asynchronous operations |
| JUnit | Model and database testing |
| Compose UI Testing | User interface testing |

## Application Architecture

MathQuest follows a layered architecture with separation of concerns.

```text
Jetpack Compose UI
        |
LearningViewModel
        |
LearningRepository
     /        \
Room DB     Retrofit API

SettingsDataStore -> LearningViewModel

AppContainer -> Dependency Injection
```

The ViewModel manages UI state and learning activities, while the Repository coordinates local and remote data sources.

Manual dependency injection is implemented through AppContainer, reducing direct dependencies between application components.

## Project Structure

```text
com.example.mathquest
|
|-- data
|   |-- local
|   |   |-- MathQuestDatabase.kt
|   |   |-- QuizResult.kt
|   |   |-- QuizResultDao.kt
|   |
|   |-- remote
|   |   |-- TriviaApiService.kt
|   |   |-- TriviaResponse.kt
|   |   |-- TriviaRemoteDataSource.kt
|   |
|   |-- preferences
|   |   |-- SettingsDataStore.kt
|   |
|   |-- LearningRepository.kt
|   |-- AppContainer.kt
|
|-- model
|   |-- Question.kt
|
|-- ui
|   |-- screens
|   |   |-- HomeScreen.kt
|   |   |-- QuizScreen.kt
|   |   |-- ProgressScreen.kt
|   |   |-- SettingsScreen.kt
|   |
|   |-- theme
|   |-- MathQuestApp.kt
|
|-- viewmodel
|   |-- LearningViewModel.kt
|
|-- MainActivity.kt
```

## Internet Connectivity

MathQuest integrates the Open Trivia Database API to retrieve online mathematics questions.

API provider:

https://opentdb.com/

The application uses Retrofit to retrieve multiple-choice mathematics questions and Gson to convert JSON responses into Kotlin objects.

If an internet connection or the external API is unavailable, the application switches to offline mathematics practice and informs the learner.

Online questions include the correct answer but do not necessarily provide detailed mathematical explanations.

## Database and Persistent Storage

Room Database is used to store completed learning sessions.

Each learning record contains:

- Unique session ID.
- Learning difficulty.
- Number of correct answers.
- Total number of questions.
- Online or offline learning source.
- Completion timestamp.

DataStore is used separately to persist learning difficulty, high contrast mode, and large quiz text preferences.

## Ethical and Professional Design

The ethical design of MathQuest was informed by the mobile application ethics topics explored in Assessment 2.

### Privacy and Data Protection

- No user registration is required.
- The application does not request names, email addresses or location information.
- Learning records are stored locally.
- Quiz scores are not transmitted to the external question provider.
- Learning records and preferences are excluded from configured Android backup and device-transfer rules.
- Online requests may expose ordinary connection information, such as an IP address, to the external service.

### Accessibility and Inclusivity

- High contrast mode improves content visibility.
- Larger quiz text supports learners who require increased readability.
- Answer feedback uses text as well as colour.
- Interactive controls provide descriptive labels and accessible semantics.
- Scrollable layouts accommodate different screen sizes.

### User Autonomy

- Learners can choose their preferred difficulty.
- No mandatory time limits are imposed.
- The application does not use competitive leaderboards or compulsory daily streaks.
- Users can delete their learning history.
- Confirmation is required before deleting stored records.

These design decisions aim to support responsible mobile development and protect the interests of learners.

## Testing

The project includes three categories of automated tests.

| Test Category | Number of Tests |
|---------------|-----------------|
| Model Unit Tests | 14 |
| Room Database Tests | 7 |
| Compose UI Tests | 7 |
| Total | 28 |

### Model Unit Tests

QuestionTest.kt verifies:

- Question generation.
- Correct and incorrect answer validation.
- Duplicate scoring prevention.
- Quiz progression.
- Invalid input handling.
- Accuracy calculations.

### Room Database Tests

QuizResultDaoTest.kt verifies:

- Database insertion and retrieval.
- Learning session counts.
- Correct answer statistics.
- Total question statistics.
- History ordering.
- Empty database handling.
- History deletion.

### Compose UI Tests

MathQuestUiTest.kt verifies:

- Online and offline learning buttons.
- Quiz interaction and feedback.
- Empty learning sessions.
- Learning statistics.
- Accessibility settings.
- History deletion confirmation and cancellation.

The project was also manually tested on a Pixel 6 Android emulator.

## Installation and Running Instructions

1. Clone or download this GitHub repository.
2. Open the project in Android Studio.
3. Synchronise the Gradle dependencies.
4. Select a compatible Android emulator or physical device.
5. Run the app configuration.

Minimum supported Android version: Android 8.0 (API 26).

An internet connection is required for online challenges, while offline practice remains available without internet access.

## Running Automated Tests

To run the local unit tests on Windows:

```bash
gradlew.bat testDebugUnitTest
```

To run the database and Compose UI tests, start an Android emulator and execute:

```bash
gradlew.bat connectedDebugAndroidTest
```

Tests can also be executed directly through Android Studio.

## Future Improvements

- Add additional mathematics topics.
- Introduce adaptive difficulty based on previous performance.
- Provide more detailed explanations for online questions.
- Improve tablet and landscape layouts.
- Conduct accessibility testing with a wider range of learners.
- Add optional personalised learning recommendations.

## References and Acknowledgements

- Android Developers: https://developer.android.com/
- Jetpack Compose: https://developer.android.com/compose
- Room Database: https://developer.android.com/training/data-storage/room
- Open Trivia Database: https://opentdb.com/
- Retrofit: https://square.github.io/retrofit/
- Australian Computer Society: https://www.acs.org.au/

Online question content is provided by Open Trivia Database under the Creative Commons Attribution-ShareAlike 4.0 International licence.

Generative AI was used to assist with application planning, code generation, implementation guidance, test development and documentation. Its use is acknowledged in the accompanying Declaration of AI-Generated Material.

## Author

Shijie Shen

James Cook University Singapore

CP3406 – Mobile Computing

Assessment 3 – Educational App