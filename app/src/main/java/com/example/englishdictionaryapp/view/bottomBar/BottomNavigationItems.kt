package com.example.englishdictionaryapp.view.bottomBar

import com.example.englishdictionaryapp.R
import com.example.englishdictionaryapp.view.navigation.Destination

sealed class BottomNavigationItems(var title: String, var icon: Int, var route: String) {

    object Search : BottomNavigationItems("Search", R.drawable.search, Destination.Search.route)
    object Favourites :
        BottomNavigationItems("Favourites", R.drawable.favourites, Destination.Favourites.route)
}
