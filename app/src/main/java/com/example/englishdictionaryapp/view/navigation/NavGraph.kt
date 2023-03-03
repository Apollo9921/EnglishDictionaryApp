package com.example.englishdictionaryapp.view.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.englishdictionaryapp.view.FavouriteWord
import com.example.englishdictionaryapp.view.Favourites
import com.example.englishdictionaryapp.view.Main
import com.example.englishdictionaryapp.view.Search
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimationNav(navHostController: NavHostController) {
    AnimatedNavHost(navHostController, startDestination = Destination.Main.route) {
        composable(
            route = Destination.Main.route
        ) {
            Main(navHostController = navHostController)
        }
        composable(
            route = Destination.Search.route
        ) {
            Search()
        }
        composable(
            route = Destination.Favourites.route
        ) {
            Favourites(navHostController = navHostController)
        }
        composable(
            route = Destination.FavouriteWord.route,
            arguments = listOf(
                navArgument("word") {
                    type = NavType.StringType
                }
            ),
            enterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(700))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(700))
            }
        ) {
            FavouriteWord(
                navHostController = navHostController,
                word = it.arguments?.getString("word")!!
            )
        }
    }
}