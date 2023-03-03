package com.example.englishdictionaryapp.view.navigation

sealed class Destination(val route: String) {
    object Main: Destination(route = "main")
    object Search: Destination(route = "search")
    object Favourites: Destination(route = "favourites")
    object FavouriteWord: Destination(route = "favourite_word/{word}") {
        fun passArgument(word: String): String {
            return "favourite_word/$word"
        }
    }
}
