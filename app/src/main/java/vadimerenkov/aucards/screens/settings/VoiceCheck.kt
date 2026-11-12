package vadimerenkov.aucards.screens.settings

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.QUEUE_FLUSH
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import vadimerenkov.aucards.R
import java.util.Locale

private const val TAG = "VoiceCheck"

/**
 * 朗读自检。
 *
 * 卡片有了、开关也开了，但如果系统没装中文语音包、或者媒体音量是 0，
 * 往往是「现场点了一下，没声」才发现。这里让人提前五秒验证一次，并直接说清原因。
 */
@Composable
fun VoiceCheck(modifier: Modifier = Modifier) {
	val context = LocalContext.current
	val audioManager = remember {
		context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
	}
	var isBound by remember { mutableStateOf(false) }
	// 一进设置页就先读一次，不用点按钮才知道音量是 0
	var mediaVolume by remember {
		mutableIntStateOf(
			audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: -1
		)
	}

	val textToSpeech = remember {
		TextToSpeech(context) { status ->
			isBound = status == TextToSpeech.SUCCESS
			if (status != TextToSpeech.SUCCESS) {
				Log.w(TAG, "TTS init failed: $status")
			}
		}
	}

	DisposableEffect(Unit) {
		onDispose { textToSpeech.shutdown() }
	}

	Column(
		verticalArrangement = Arrangement.spacedBy(4.dp),
		modifier = modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 8.dp)
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			modifier = Modifier.fillMaxWidth()
		) {
			Text(
				text = stringResource(R.string.voice_check_title),
				style = MaterialTheme.typography.bodyLarge,
				modifier = Modifier.weight(1f)
			)
			OutlinedButton(
				onClick = {
					mediaVolume = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: -1
					if (!isBound) return@OutlinedButton
					val locale = Locale.getDefault()
					val target = if (locale.language == "zh") Locale.SIMPLIFIED_CHINESE else locale
					textToSpeech.setLanguage(target)
					textToSpeech.setSpeechRate(0.9f)
					textToSpeech.speak(
						context.getString(R.string.voice_check_sentence),
						QUEUE_FLUSH,
						null,
						"voice_check"
					)
				}
			) {
				Text(text = stringResource(R.string.voice_check_play))
			}
		}

		when {
			// 音量 0 是最常见的「点了没声」，优先提示，而且和引擎状态无关
			mediaVolume == 0 -> {
				Row(
					verticalAlignment = Alignment.CenterVertically,
					modifier = Modifier.fillMaxWidth()
				) {
					Text(
						text = stringResource(R.string.voice_check_volume_zero),
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.error,
						modifier = Modifier.weight(1f)
					)
					TextButton(
						onClick = {
							val intent = Intent(Settings.ACTION_SOUND_SETTINGS)
								.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
							runCatching { context.startActivity(intent) }
								.onFailure { Log.w(TAG, "Cannot open sound settings: $it") }
						}
					) {
						Text(text = stringResource(R.string.voice_check_open_sound))
					}
				}
			}
			!isBound -> {
				Text(
					text = stringResource(R.string.voice_check_no_engine),
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.error
				)
			}
			else -> {
				Text(
					text = stringResource(R.string.voice_check_hint),
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
			}
		}
	}
}
