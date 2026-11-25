
package com.example.mathquest.data.remote

import com.google.gson.annotations.SerializedName

// Represents the complete response returned by the trivia API.
data class TriviaResponse(

    @SerializedName("response_code")
    val responseCode: Int,

    @SerializedName("results")
    val results: List<TriviaQuestion>?
)

// Represents an individual question received from the API.
data class TriviaQuestion(

    @SerializedName("category")
    val category: String,

    @SerializedName("difficulty")
    val difficulty: String,

    @SerializedName("question")
    val question: String,

    @SerializedName("correct_answer")
    val correctAnswer: String,

    @SerializedName("incorrect_answers")
    val incorrectAnswers: List<String>
)
