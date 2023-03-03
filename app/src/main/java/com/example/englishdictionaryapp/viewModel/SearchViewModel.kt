package com.example.englishdictionaryapp.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.englishdictionaryapp.model.retrofit.NetworkModule
import com.example.englishdictionaryapp.model.wordDefinition.WordDefinition
import com.example.englishdictionaryapp.view.custom.internet
import com.example.englishdictionaryapp.view.internet.checkInternetConnection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import retrofit2.awaitResponse

class SearchViewModel: ViewModel() {
    private var checking = 0

    private var _searchUIState = MutableStateFlow<SearchUIState>(SearchUIState.Error)
    var searchUIState: StateFlow<SearchUIState> = _searchUIState
    private var networkModule = NetworkModule()

    sealed class SearchUIState {
        data class Success(val word: WordDefinition): SearchUIState()
        object Error: SearchUIState()
        object Cancel: SearchUIState()
    }

    fun searchForWord(context: Context, query: String) {
        runBlocking(Dispatchers.IO) {
            val call = networkModule.retrofitInterface().getWordDefinition(query).awaitResponse()
            checking = 0
            while (checking == 0) {
                internet.value = checkInternetConnection(context)
                if (internet.value) {
                    if (call.isSuccessful) {
                        _searchUIState.value = SearchUIState.Success(call.body()!!)
                        checking++
                    } else {
                        _searchUIState.value = SearchUIState.Error
                        checking++
                    }
                } else {
                    networkModule.retrofitInterface().getWordDefinition(query).cancel()
                    _searchUIState.value = SearchUIState.Cancel
                    checking++
                }
            }
        }
    }
}