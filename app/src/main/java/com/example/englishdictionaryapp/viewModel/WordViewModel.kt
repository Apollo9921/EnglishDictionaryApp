package com.example.englishdictionaryapp.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishdictionaryapp.model.room.Word
import com.example.englishdictionaryapp.model.room.WordDatabase
import com.example.englishdictionaryapp.model.room.WordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WordViewModel(application: Context): ViewModel() {

    private val repository: WordRepository

    init {
        val wordDAO = WordDatabase.getDatabase(application).wordDao()
        repository = WordRepository(wordDAO)
    }

    fun addWord(word: Word) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addWord(word)
        }
    }

    fun deleteWord(word: Word) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteWord(word)
        }
    }
}