package com.example.englishdictionaryapp.model.room


class WordRepository(private val wordDAO: WordDAO) {

    suspend fun addWord(word: Word) {
        wordDAO.addWord(word)
    }

    suspend fun deleteWord(word: Word) {
        wordDAO.deleteWord(word)
    }
}