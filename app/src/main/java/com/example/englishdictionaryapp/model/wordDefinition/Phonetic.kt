package com.example.englishdictionaryapp.model.wordDefinition

data class Phonetic(
    val audio: String,
    val license: License,
    val sourceUrl: String,
    val text: String
)