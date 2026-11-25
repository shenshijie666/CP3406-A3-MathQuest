
package com.example.mathquest.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Defines the network requests supported by the trivia API.
interface TriviaApiService {

    @GET("api.php")
    suspend fun getMathQuestions(

        // Requests five questions for each learning session.
        @Query("amount")
        amount: Int = 5,

        // Category 19 represents mathematics.
        @Query("category")
        category: Int = 19,

        @Query("difficulty")
        difficulty: String,

        @Query("type")
        type: String = "multiple",

        // Preserves special characters in mathematical questions.
        @Query("encode")
        encode: String = "base64"

    ): TriviaResponse

    companion object {

        private const val BASE_URL = "https://opentdb.com/"

        // Creates a Retrofit service for network communication.
        fun create(): TriviaApiService {

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TriviaApiService::class.java)
        }
    }
}
