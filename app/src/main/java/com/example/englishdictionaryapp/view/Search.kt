package com.example.englishdictionaryapp.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.englishdictionaryapp.R.*
import com.example.englishdictionaryapp.model.room.Word
import com.example.englishdictionaryapp.model.room.WordDatabase
import com.example.englishdictionaryapp.model.wordDefinition.WordDefinition
import com.example.englishdictionaryapp.view.custom.*
import com.example.englishdictionaryapp.view.internet.checkInternetConnection
import com.example.englishdictionaryapp.view.theme.Black
import com.example.englishdictionaryapp.view.theme.Favourite
import com.example.englishdictionaryapp.view.theme.White
import com.example.englishdictionaryapp.viewModel.SearchViewModel
import com.example.englishdictionaryapp.viewModel.WordViewModel
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.util.*

@SuppressLint("StaticFieldLeak")
private lateinit var context: Context
private var search = mutableStateOf("")
private var clickMic = mutableStateOf(false)
private var loading = mutableStateOf(false)
private var success = mutableStateOf(false)
private var error = mutableStateOf(false)
private var cancel = mutableStateOf(false)
private var isFavourite = mutableStateOf(false)
private var clickedFavourite = mutableStateOf(false)
private var wordFavourite: Word? = null
private lateinit var wordDefinition: WordDefinition
private var audio: MediaPlayer? = null
private var viewModel: SearchViewModel = SearchViewModel()
private lateinit var wordViewModel: WordViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Search() {
    isLoaded.value = false
    context = LocalContext.current
    wordViewModel = WordViewModel(context)
    resultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val speech = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                search.value = speech?.get(0).toString()
                searchForWord(search.value)
            }
        }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White),
        contentAlignment = Alignment.Center
    ) {
        if (!success.value) {
            Form()
        }
    }
    GetResult()
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun ShowWordDefinition() {
    for (i in wordDefinition[0].phonetics.indices) {
        if (wordDefinition[0].phonetics[i].audio.isNotBlank()) {
            audio = MediaPlayer.create(context, wordDefinition[0].phonetics[i].audio.toUri())
            break
        }
    }
    when {
        isFavourite.value && clickedFavourite.value -> {
            val date = remember {
                LocalDateTime.now()
            }
            clickedFavourite.value = false
            addWordToFavourites(wordDefinition[0].word, date.toString())
        }
        !isFavourite.value && clickedFavourite.value -> {
            clickedFavourite.value = false
            deleteWordToFavourites()
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
                    painter = painterResource(id = drawable.back),
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
                        .clickable { success.value = false }
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Top
            ) {
                Image(
                    painter = painterResource(id = drawable.favourite),
                    contentDescription = null,
                    colorFilter = if (!isFavourite.value) ColorFilter.tint(White) else ColorFilter.tint(
                        Favourite
                    ),
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
                            isFavourite.value = !isFavourite.value
                            clickedFavourite.value = true
                        }
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
                                painter = painterResource(id = drawable.play),
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
                                text = stringResource(id = string.audio),
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
                                    text = stringResource(id = string.example),
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
                            text = stringResource(id = string.synonyms),
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
                            text = stringResource(id = string.antonyms),
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

@Composable
private fun Form() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(raw.mic))
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = search.value,
            onValueChange = {
                search.value = it
            },
            label = {
                Text(
                    text = stringResource(id = string.search),
                    color = Black,
                    fontSize =
                    if (mediaQueryWidth() <= small) {
                        14.sp
                    } else if (mediaQueryWidth() <= normal) {
                        18.sp
                    } else {
                        22.sp
                    },
                    fontFamily = FontFamily.SansSerif
                )
            },
            placeholder = {
                Text(
                    text = stringResource(id = string.search),
                    color = Black,
                    fontSize =
                    if (mediaQueryWidth() <= small) {
                        14.sp
                    } else if (mediaQueryWidth() <= normal) {
                        18.sp
                    } else {
                        22.sp
                    },
                    fontFamily = FontFamily.SansSerif
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = { searchForWord(search.value) },
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Black
                    )
                }
            },
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 40.dp, end = 40.dp)
        )
        Spacer(modifier = Modifier.padding(20.dp))
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
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
                .clickable {
                    clickMic.value = true
                }
        )
    }
}

@OptIn(DelicateCoroutinesApi::class)
private fun searchForWord(query: String) {
    internet.value = checkInternetConnection(context)
    if (internet.value) {
        loading.value = true
        GlobalScope.launch(Dispatchers.IO) {
            viewModel.searchForWord(context, query)
            viewModel.searchUIState.collect {
                when (it) {
                    SearchViewModel.SearchUIState.Cancel -> {
                        loading.value = false
                        cancel.value = true
                        Handler(Looper.getMainLooper()).postDelayed({
                            cancel.value = false
                        }, 3000)
                    }
                    SearchViewModel.SearchUIState.Error -> {
                        loading.value = false
                        error.value = true
                        Handler(Looper.getMainLooper()).postDelayed({
                            error.value = false
                        }, 3000)
                    }
                    is SearchViewModel.SearchUIState.Success -> {
                        loading.value = false
                        success.value = true
                        wordDefinition = it.word
                    }
                }
            }
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
private fun addWordToFavourites(word: String, date: String) {
    GlobalScope.launch(Dispatchers.IO) {
        val wordAdding = Word(0, word, date)
        wordViewModel.addWord(wordAdding)
        wordFavourite = wordAdding
        isLoaded.value = false
    }
}

@OptIn(DelicateCoroutinesApi::class)
private fun deleteWordToFavourites() {
    GlobalScope.launch(Dispatchers.IO) {
        wordViewModel.deleteWord(wordFavourite!!)
        isLoaded.value = false
    }
}

@Composable
private fun GetResult() {
    when {
        clickMic.value -> {
            clickMic.value = false
            AskSpeechInput(context)
        }
        loading.value -> {
            Loading()
        }
        !internet.value -> {
            loading.value = false
            CustomMessage(text = stringResource(id = string.noInternet))
            Handler(Looper.getMainLooper()).postDelayed({
                internet.value = true
            }, 3000)
        }
        error.value -> {
            CustomMessage(text = stringResource(id = string.doNotExist))
        }
        cancel.value -> {
            CustomMessage(text = stringResource(id = string.noInternet))
        }
        success.value -> {
            val owner = LocalLifecycleOwner.current
            WordDatabase.getDatabase(context).wordDao().listSpecificWord(wordDefinition[0].word).observe(owner) {
                isFavourite.value = it != null
                wordFavourite = if (isFavourite.value) {
                    it
                } else {
                    null
                }
            }
            ShowWordDefinition()
        }
        !success.value -> {
            if (audio != null) {
                audio!!.release()
                audio = null
            }
            isFavourite.value = false
        }
    }
}
