package com.example.englishdictionaryapp.model.wordDefinition

data class Definition(
    val antonyms: List<Any>,
    val definition: String,
    val example: String?,
    val synonyms: List<Any>
)