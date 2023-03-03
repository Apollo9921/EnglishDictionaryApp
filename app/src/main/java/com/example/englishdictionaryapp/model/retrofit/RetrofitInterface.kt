package com.example.englishdictionaryapp.model.retrofit

import com.example.englishdictionaryapp.BuildConfig
import com.example.englishdictionaryapp.model.wordDefinition.WordDefinition
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitInterface {
    @GET("${BuildConfig.BASE_URL}{query}")
    fun getWordDefinition(@Path("query") query: String): Call<WordDefinition>
}