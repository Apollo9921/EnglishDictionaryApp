package com.example.englishdictionaryapp.model.wordDefinition

data class WordDefinitionItem(
    val license: License,
    val meanings: List<Meaning>,
    val phonetics: List<Phonetic>,
    val sourceUrls: List<String>,
    val word: String
)