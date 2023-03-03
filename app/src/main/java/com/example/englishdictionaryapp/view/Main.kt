package com.example.englishdictionaryapp.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.englishdictionaryapp.view.bottomBar.BottomNavigationItems
import com.example.englishdictionaryapp.view.bottomBar.bottomNavigationBar
import com.example.englishdictionaryapp.view.custom.deletedWord
import com.example.englishdictionaryapp.view.custom.favouriteWords
import com.example.englishdictionaryapp.view.main.keepSplashOpened

private var index = mutableStateOf("")

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main(navHostController: NavHostController) {
    keepSplashOpened = false
    favouriteWords = remember { mutableStateListOf() }
    deletedWord = remember { mutableStateListOf() }
    Scaffold(
        bottomBar = { index.value = bottomNavigationBar() }
    ) {
        Box(
          modifier = Modifier.padding(bottom = it.calculateBottomPadding())
        ) {
            when (index.value) {
                BottomNavigationItems.Search.route -> {
                    Search()
                }
                BottomNavigationItems.Favourites.route -> {
                    Favourites(navHostController = navHostController)
                }
            }
        }
    }
}