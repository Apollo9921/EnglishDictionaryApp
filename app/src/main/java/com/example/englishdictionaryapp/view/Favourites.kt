package com.example.englishdictionaryapp.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.CountDownTimer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.example.englishdictionaryapp.R
import com.example.englishdictionaryapp.model.room.Word
import com.example.englishdictionaryapp.model.room.WordDatabase
import com.example.englishdictionaryapp.view.custom.*
import com.example.englishdictionaryapp.view.navigation.Destination
import com.example.englishdictionaryapp.view.theme.Black
import com.example.englishdictionaryapp.view.theme.White
import com.example.englishdictionaryapp.viewModel.WordViewModel
import kotlin.random.Random

@SuppressLint("StaticFieldLeak")
private lateinit var context: Context
private var loading = mutableStateOf(false)
private var items: List<ListItem> = ArrayList()
private lateinit var wordViewModel: WordViewModel
private var id = mutableStateOf(0)
private lateinit var owner: LifecycleOwner

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Favourites(navHostController: NavHostController) {
    context = LocalContext.current
    wordViewModel = WordViewModel(context)
    owner = LocalLifecycleOwner.current
    loading.value = true
    WordDatabase.getDatabase(context).wordDao().listAllWords().observe(owner) {
        if (!isLoaded.value) {
            if (favouriteWords.isNotEmpty()) {
                favouriteWords.clear()
            }
            for (i in it.indices) {
                favouriteWords.add(it[i])
            }
            items = (it.indices).map {
                ListItem(
                    height = Random.nextInt(150, 300).dp,
                    color = Color(
                        Random.nextLong(0xFFFFFFFF)
                    ).copy(alpha = 1f)
                )
            }
            isLoaded.value = true
        }
    }
    if (isLoaded.value && favouriteWords.isNotEmpty()) {
        loading.value = false
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
        ) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                state = listState,
            ) {
                items(favouriteWords.size) {
                    AnimatedVisibility(
                        visible = !deletedWord.contains(favouriteWords[it]),
                        exit = fadeOut(animationSpec = tween(1000)),
                        modifier = Modifier.padding(10.dp)
                    ) {
                        FavouriteWordsBox(
                            item = items[it],
                            word = favouriteWords[it],
                            navHostController
                        )
                    }
                }
            }
            GetResult()
        }
    } else {
        loading.value = false
        Empty()
    }
}

@Composable
private fun Empty() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.empty),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Black),
            modifier = Modifier
                .size(
                    if (mediaQueryWidth() <= small) {
                        100.dp
                    } else if (mediaQueryWidth() <= normal) {
                        150.dp
                    } else {
                        200.dp
                    }
                )
        )
        Text(
            text = stringResource(id = R.string.emptyFavourites),
            color = Black,
            fontSize =
            if (mediaQueryWidth() <= small) {
                20.sp
            } else if (mediaQueryWidth() <= normal) {
                24.sp
            } else {
                28.sp
            },
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

data class ListItem(
    val height: Dp,
    val color: Color
)

@Composable
private fun FavouriteWordsBox(
    item: ListItem,
    word: Word,
    navHostController: NavHostController
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(item.height)
            .clip(RoundedCornerShape(10.dp))
            .background(item.color)
            .clickable {
                id.value = word.id
                navHostController.navigate(Destination.FavouriteWord.passArgument(word.word))
            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Image(
                painter = painterResource(id = R.drawable.delete),
                contentDescription = null,
                colorFilter = ColorFilter.tint(White),
                modifier = Modifier
                    .size(
                        if (mediaQueryWidth() <= small) {
                            35.dp
                        } else if (mediaQueryWidth() <= normal) {
                            45.dp
                        } else {
                            55.dp
                        }
                    )
                    .clickable {
                        deletedWord.add(word)
                    }
            )
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = word.word.uppercase(),
                color = White,
                fontSize =
                if (mediaQueryWidth() <= small) {
                    16.sp
                } else if (mediaQueryWidth() <= normal) {
                    20.sp
                } else {
                    24.sp
                },
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
    if (deletedWord.contains(word)) {
        val timer = object : CountDownTimer(1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                favouriteWords.remove(word)
                wordViewModel.deleteWord(word)
            }
        }
        timer.start()
    }
}

@Composable
private fun GetResult() {
    when {
        loading.value -> {
            Loading()
        }
    }
}