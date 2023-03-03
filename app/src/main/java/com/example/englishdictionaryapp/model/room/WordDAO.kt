package com.example.englishdictionaryapp.model.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WordDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addWord(word: Word)

    @Delete
    suspend fun deleteWord(word: Word)

    @Query("SELECT * FROM word_table WHERE word =:word")
    fun listSpecificWord(word: String): LiveData<Word>

    @Query("SELECT * FROM word_table ORDER BY date DESC")
    fun listAllWords(): LiveData<List<Word>>

}