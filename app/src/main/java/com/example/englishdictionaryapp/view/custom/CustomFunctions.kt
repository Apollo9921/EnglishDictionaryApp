package com.example.englishdictionaryapp.view.custom

import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.englishdictionaryapp.R
import com.example.englishdictionaryapp.model.room.Word
import com.example.englishdictionaryapp.view.getWord
import com.example.englishdictionaryapp.view.theme.Black
import com.example.englishdictionaryapp.view.theme.Error
import com.example.englishdictionaryapp.view.theme.White
import java.util.*

var internet = mutableStateOf(true)
lateinit var resultLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>
lateinit var favouriteWords: SnapshotStateList<Word>
lateinit var deletedWord: SnapshotStateList<Word>
var isLoaded = mutableStateOf(false)
@OptIn(ExperimentalFoundationApi::class)
lateinit var listState: LazyStaggeredGridState

val small = 600.dp
val normal = 840.dp

@Composable
fun mediaQueryWidth(): Dp {
    return LocalContext.current.resources.displayMetrics.widthPixels.dp / LocalDensity.current.density
}

@Composable
fun AskSpeechInput(context: Context) {
    if (!SpeechRecognizer.isRecognitionAvailable(context)) {
        Toast.makeText(
            context,
            stringResource(id = R.string.speechNotAvailable),
            Toast.LENGTH_SHORT
        ).show()
    } else {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, stringResource(id = R.string.talk))

        resultLauncher.launch(intent)
    }
}

@Composable
fun CustomMessage(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Error)
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.error),
                contentDescription = null,
                colorFilter = ColorFilter.tint(White),
                modifier = Modifier
                    .size(
                        if (mediaQueryWidth() <= small) {
                            35.dp
                        } else if (mediaQueryWidth() <= normal) {
                            40.dp
                        } else {
                            45.dp
                        }
                    )
            )
            Spacer(modifier = Modifier.padding(5.dp))
            Text(
                text = text,
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
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun Loading() {
    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Black.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = White,
                strokeWidth = 3.dp,
                modifier = Modifier.size(
                    if (mediaQueryWidth() <= small) {
                        100.dp
                    } else if (mediaQueryWidth() <= normal) {
                        150.dp
                    } else {
                        200.dp
                    }
                )
            )
        }
    }
}

@Composable
fun CustomError(text: String, background: Color, painter: Painter, word: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                colorFilter = ColorFilter.tint(White),
                modifier = Modifier
                    .size(
                        if (mediaQueryWidth() <= small) {
                            100.dp
                        } else if (mediaQueryWidth() <= normal) {
                            125.dp
                        } else {
                            150.dp
                        }
                    )
            )
            Spacer(modifier = Modifier.padding(5.dp))
            Text(
                text = text,
                color = White,
                fontSize =
                if (mediaQueryWidth() <= small) {
                    20.sp
                } else if (mediaQueryWidth() <= normal) {
                    25.sp
                } else {
                    30.sp
                },
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clickable {
                        getWord(word)
                    }
            )
        }
    }
}