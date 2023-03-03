package com.example.englishdictionaryapp.view

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import com.example.englishdictionaryapp.R
import com.example.englishdictionaryapp.model.wordDefinition.WordDefinition
import com.example.englishdictionaryapp.view.custom.*
import com.example.englishdictionaryapp.view.internet.checkInternetConnection
import com.example.englishdictionaryapp.view.navigation.Destination
import com.example.englishdictionaryapp.view.theme.Black
import com.example.englishdictionaryapp.view.theme.Error
import com.example.englishdictionaryapp.view.theme.PurpleGrey40
import com.example.englishdictionaryapp.view.theme.White
import com.example.englishdictionaryapp.viewModel.SearchViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@SuppressLint("StaticFieldLeak")
private lateinit var context: Context
private var audio: MediaPlayer? = null
private lateinit var wordDefinition: WordDefinition
private var viewModel: SearchViewModel = SearchViewModel()
private var loading = mutableStateOf(false)
private var success = mutableStateOf(false)
private var error = mutableStateOf(false)
private var cancel = mutableStateOf(false)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FavouriteWord(navHostController: NavHostController, word: String) {
    isLoaded.value = false
    if (audio != null) {
        audio!!.release()
        audio = null
    }
    context = LocalContext.current
    BackHandler {
        success.value = false
        error.value = false
        cancel.value = false
        navHostController.popBackStack(
            Destination.Main.route,
            inclusive = true
        )
        navHostController.navigate(Destination.Main.route)
    }
    getWord(word)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        GetResult(navHostController, word)
    }
}

@Composable
private fun GetResult(navHostController: NavHostController, word: String) {
    when {
        loading.value -> {
            Loading()
        }
        error.value -> {
            CustomError(
                text = stringResource(id = R.string.somethingWentWrong),
                background = Error,
                painter = painterResource(id = R.drawable.error),
                word = word
            )
        }
        cancel.value || !internet.value -> {
            CustomError(
                text = stringResource(id = R.string.noInternet),
                background = PurpleGrey40,
                painter = painterResource(id = R.drawable.no_wifi),
                word = word
            )
        }
        success.value -> {
            ShowWordDefinition(navHostController)
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun getWord(word: String) {
    internet.value = checkInternetConnection(context)
    if (internet.value) {
        loading.value = true
        GlobalScope.launch(Dispatchers.IO) {
            viewModel.searchForWord(context, word)
            viewModel.searchUIState.collect {
                when (it) {
                    SearchViewModel.SearchUIState.Cancel -> {
                        loading.value = false
                        cancel.value = true
                    }
                    SearchViewModel.SearchUIState.Error -> {
                        loading.value = false
                        error.value = true
                    }
                    is SearchViewModel.SearchUIState.Success -> {
                        loading.value = false
                        wordDefinition = it.word
                        success.value = true
                    }
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun ShowWordDefinition(navHostController: NavHostController) {
    for (i in wordDefinition[0].phonetics.indices) {
        if (wordDefinition[0].phonetics[i].audio.isNotBlank()) {
            audio = MediaPlayer.create(context, wordDefinition[0].phonetics[i].audio.toUri())
            break
        }
    }
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Black)
                    .padding(20.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Top
            ) {
                Image(
                    painter = painterResource(id = R.drawable.back),
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
                            success.value = false
                            error.value = false
                            cancel.value = false
                            navHostController.popBackStack(
                                Destination.Main.route,
                                inclusive = true
                            )
                            navHostController.navigate(Destination.Main.route)
                        }
                )
                Spacer(modifier = Modifier.padding(5.dp))
                Text(
                    text = wordDefinition[0].word,
                    color = White,
                    fontSize =
                    if (mediaQueryWidth() <= small) {
                        25.sp
                    } else if (mediaQueryWidth() <= normal) {
                        35.sp
                    } else {
                        45.sp
                    },
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, top = 100.dp, end = 20.dp)
        ) {
            item {
                if (audio != null) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.play),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(Black),
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
                                        audio!!.start()
                                    }
                            )
                            Spacer(modifier = Modifier.padding(5.dp))
                            Text(
                                text = stringResource(id = R.string.audio),
                                color = Black,
                                fontSize =
                                if (mediaQueryWidth() <= small) {
                                    25.sp
                                } else if (mediaQueryWidth() <= normal) {
                                    35.sp
                                } else {
                                    45.sp
                                },
                                fontFamily = FontFamily.SansSerif,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            items(wordDefinition[0].meanings.size) {
                Column {
                    Spacer(modifier = Modifier.padding(5.dp))
                    Text(
                        text = wordDefinition[0].meanings[it].partOfSpeech,
                        color = Black,
                        fontSize =
                        if (mediaQueryWidth() <= small) {
                            30.sp
                        } else if (mediaQueryWidth() <= normal) {
                            35.sp
                        } else {
                            45.sp
                        },
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.padding(5.dp))
                    for (i in wordDefinition[0].meanings[it].definitions.indices) {
                        Text(
                            text = wordDefinition[0].meanings[it].definitions[i].definition,
                            color = Black,
                            fontSize =
                            if (mediaQueryWidth() <= small) {
                                20.sp
                            } else if (mediaQueryWidth() <= normal) {
                                25.sp
                            } else {
                                30.sp
                            },
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.padding(5.dp))
                        if (wordDefinition[0].meanings[it].definitions[i].example != null) {
                            Row {
                                Text(
                                    text = stringResource(id = R.string.example),
                                    color = Black,
                                    fontSize =
                                    if (mediaQueryWidth() <= small) {
                                        20.sp
                                    } else if (mediaQueryWidth() <= normal) {
                                        25.sp
                                    } else {
                                        30.sp
                                    },
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Justify
                                )
                                Spacer(modifier = Modifier.padding(5.dp))
                                Text(
                                    text = wordDefinition[0].meanings[it].definitions[i].example!!,
                                    color = Black,
                                    fontSize =
                                    if (mediaQueryWidth() <= small) {
                                        20.sp
                                    } else if (mediaQueryWidth() <= normal) {
                                        25.sp
                                    } else {
                                        30.sp
                                    },
                                    fontFamily = FontFamily.SansSerif,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        Spacer(modifier = Modifier.padding(10.dp))
                    }
                }
            }
            items(wordDefinition[0].meanings.size) {
                Column {
                    if (wordDefinition[0].meanings[it].synonyms.isNotEmpty()) {
                        Text(
                            text = stringResource(id = R.string.synonyms),
                            color = Black,
                            fontSize =
                            if (mediaQueryWidth() <= small) {
                                30.sp
                            } else if (mediaQueryWidth() <= normal) {
                                35.sp
                            } else {
                                40.sp
                            },
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.padding(5.dp))
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            maxItemsInEachRow = 10
                        ) {
                            for (y in wordDefinition[0].meanings[it].synonyms.indices) {
                                Text(
                                    text = wordDefinition[0].meanings[it].synonyms[y],
                                    color = Black,
                                    fontSize =
                                    if (mediaQueryWidth() <= small) {
                                        20.sp
                                    } else if (mediaQueryWidth() <= normal) {
                                        25.sp
                                    } else {
                                        30.sp
                                    },
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.padding(5.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.padding(5.dp))
                    if (wordDefinition[0].meanings[it].antonyms.isNotEmpty()) {
                        Text(
                            text = stringResource(id = R.string.antonyms),
                            color = Black,
                            fontSize =
                            if (mediaQueryWidth() <= small) {
                                30.sp
                            } else if (mediaQueryWidth() <= normal) {
                                35.sp
                            } else {
                                40.sp
                            },
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.padding(5.dp))
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            maxItemsInEachRow = 10
                        ) {
                            for (z in wordDefinition[0].meanings[it].antonyms.indices) {
                                Text(
                                    text = wordDefinition[0].meanings[it].antonyms[z],
                                    color = Black,
                                    fontSize =
                                    if (mediaQueryWidth() <= small) {
                                        20.sp
                                    } else if (mediaQueryWidth() <= normal) {
                                        25.sp
                                    } else {
                                        30.sp
                                    },
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.padding(5.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}