package vadimerenkov.aucards.screens.quickspeak

import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.QUEUE_FLUSH
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import vadimerenkov.aucards.R
import vadimerenkov.aucards.screens.fullscreencard.layouts.DisplayText
import vadimerenkov.aucards.screens.fullscreencard.toolbar.ActionButton
import vadimerenkov.aucards.ui.calculateContentColor
import java.util.Locale

private const val TAG = "QuickSpeak"
private const val UTTERANCE_ID = "quick_speak"

/**
 * 打完字立刻全屏 + 朗读，不落库。想留着当常用卡再点「存为卡片」。
 */
@Composable
fun QuickSpeakScreen(
	viewModel: QuickSpeakViewModel,
	onClose: () -> Unit
) {
	val context = LocalContext.current
	val isSaving by viewModel.isSaving.collectAsState()

	var isBound by remember { mutableStateOf(false) }
	val textToSpeech = remember {
		TextToSpeech(context) { status ->
			if (status == TextToSpeech.SUCCESS) {
				Log.d(TAG, "Initialized tts")
				isBound = true
			}
		}
	}

	LaunchedEffect(isBound) {
		if (!isBound) return@LaunchedEffect
		// 进来就是要说话，所以这里不看「自动朗读」开关：先矫正语言，再念。
		val locale = Locale.getDefault()
		val target = if (locale.language == "zh") Locale.SIMPLIFIED_CHINESE else locale
		val result = textToSpeech.setLanguage(target)
		if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
			Log.w(TAG, "TTS language $target unavailable, using engine default")
		}
		textToSpeech.setSpeechRate(0.9f)
		textToSpeech.speak(viewModel.text, QUEUE_FLUSH, null, UTTERANCE_ID)
	}

	DisposableEffect(Unit) {
		onDispose { textToSpeech.shutdown() }
	}

	val contentColor = calculateContentColor(viewModel.color)

	Box(
		contentAlignment = Alignment.Center,
		modifier = Modifier
			.fillMaxSize()
			.background(viewModel.color)
			.systemBarsPadding()
			.imePadding()
			.clickable { onClose() }
	) {
		DisplayText(
			text = viewModel.text,
			textSize = viewModel.titleFontSize,
			color = contentColor,
			backgroundColor = Color.Transparent,
			modifier = Modifier.padding(horizontal = 24.dp, vertical = 96.dp)
		)

		ActionButton(
			onClick = {
				if (isBound) {
					textToSpeech.speak(viewModel.text, QUEUE_FLUSH, null, UTTERANCE_ID)
				}
			},
			icon = painterResource(R.drawable.speaker),
			contentDescription = stringResource(R.string.read_aloud),
			iconSize = 72,
			tint = contentColor,
			modifier = Modifier
				.align(Alignment.BottomStart)
				.padding(horizontal = 20.dp, vertical = 24.dp)
		)

		Button(
			onClick = { viewModel.save(onClose) },
			enabled = !isSaving,
			modifier = Modifier
				.align(Alignment.BottomEnd)
				.padding(horizontal = 20.dp, vertical = 24.dp)
		) {
			Text(text = stringResource(R.string.save_as_card))
		}
	}
}
