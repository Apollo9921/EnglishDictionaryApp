package com.example.englishdictionaryapp.view.main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.englishdictionaryapp.view.custom.isLoaded
import com.example.englishdictionaryapp.view.custom.listState
import com.example.englishdictionaryapp.view.navigation.AnimationNav
import com.example.englishdictionaryapp.view.theme.EnglishDictionaryAppTheme
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

var keepSplashOpened = true
@SuppressLint("StaticFieldLeak")
private lateinit var context: Context

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition {
            keepSplashOpened
        }
        setContent {
            EnglishDictionaryAppTheme {
                listState = rememberLazyStaggeredGridState()
                context = LocalContext.current
                val navController = rememberAnimatedNavController()
                AnimationNav(navHostController = navController)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        isLoaded.value = false
    }
}