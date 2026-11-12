package vadimerenkov.aucards.screens.recovery

import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.SystemClock
import android.provider.AlarmClock
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import vadimerenkov.aucards.data.COLD_PACK_PHASE_SECONDS
import vadimerenkov.aucards.data.coldPackIsCold
import vadimerenkov.aucards.data.coldPackPhaseIndex
import vadimerenkov.aucards.data.coldPackRemaining
import vadimerenkov.aucards.data.formatCountdown

private const val TAG = "ColdPack"

/**
 * 敷 20 分钟、停 20 分钟 的计时卡。
 *
 * 用 elapsedRealtime 记录起点而不是自己每秒累加，切后台、锁屏回来都不会走偏；
 * 每段结束响一声。应用被杀掉就管不到了，所以旁边留一个「系统计时器」出口。
 */
@Composable
internal fun ColdPackCard(
	isCold: Boolean,
	modifier: Modifier = Modifier
) {
	val context = LocalContext.current
	var accumulatedSeconds by rememberSaveable { mutableLongStateOf(0L) }
	var startedAt by rememberSaveable { mutableLongStateOf(0L) }
	var running by rememberSaveable { mutableStateOf(false) }
	var now by remember { mutableLongStateOf(SystemClock.elapsedRealtime()) }

	LaunchedEffect(running) {
		if (!running) return@LaunchedEffect
		while (isActive) {
			now = SystemClock.elapsedRealtime()
			delay(500)
		}
	}

	val elapsed = (
		accumulatedSeconds +
			if (running) (now - startedAt) / 1000 else 0L
		).toInt()
	val phaseIndex = coldPackPhaseIndex(elapsed)
	val inColdPhase = coldPackIsCold(elapsed)
	val remaining = coldPackRemaining(elapsed)

	var announcedPhase by remember { mutableIntStateOf(phaseIndex) }
	LaunchedEffect(phaseIndex) {
		// 只在往前推进时响，重置回第 0 段不该响
		if (running && phaseIndex > announcedPhase) {
			playChime(context)
		}
		announcedPhase = phaseIndex
	}

	Card(
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.secondaryContainer
		),
		modifier = modifier.fillMaxWidth()
	) {
		Column(
			verticalArrangement = Arrangement.spacedBy(6.dp),
			modifier = Modifier.padding(16.dp)
		) {
			Text(
				text = if (isCold) "冷敷计时" else "热敷计时",
				style = MaterialTheme.typography.titleMedium,
				color = MaterialTheme.colorScheme.onSecondaryContainer
			)
			Text(
				text = "敷 $COLD_PACK_MINUTES 分钟、停 $COLD_PACK_MINUTES 分钟，交替着来",
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSecondaryContainer
			)

			Text(
				text = formatCountdown(remaining),
				fontSize = 40.sp,
				fontWeight = FontWeight.Medium,
				color = MaterialTheme.colorScheme.onSecondaryContainer,
				modifier = Modifier.padding(top = 4.dp)
			)
			Text(
				text = if (inColdPhase) "现在：敷着" else "现在：拿下来歇一会儿",
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSecondaryContainer
			)

			Row(
				horizontalArrangement = Arrangement.spacedBy(8.dp),
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier.padding(top = 8.dp)
			) {
				Button(
					onClick = {
						if (running) {
							accumulatedSeconds += (SystemClock.elapsedRealtime() - startedAt) / 1000
							running = false
						} else {
							startedAt = SystemClock.elapsedRealtime()
							now = startedAt
							running = true
						}
					}
				) {
					Text(text = if (running) "暂停" else "开始")
				}
				OutlinedButton(
					enabled = running || accumulatedSeconds > 0,
					onClick = {
						running = false
						accumulatedSeconds = 0L
					}
				) {
					Text(text = "重置")
				}
			}

			TextButton(
				onClick = { openSystemTimer(context) }
			) {
				Text(text = "用系统计时器（应用关掉也会响）")
			}
		}
	}
}

/** 20 分钟，写在一处免得文案和逻辑对不上 */
private const val COLD_PACK_MINUTES = COLD_PACK_PHASE_SECONDS / 60

private fun playChime(context: Context) {
	runCatching {
		val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
		RingtoneManager.getRingtone(context, uri)?.play()
	}.onFailure { Log.w(TAG, "Cannot play chime: $it") }
}

private fun openSystemTimer(context: Context) {
	runCatching {
		val intent = Intent(AlarmClock.ACTION_SET_TIMER)
			.putExtra(AlarmClock.EXTRA_LENGTH, COLD_PACK_PHASE_SECONDS)
			.putExtra(AlarmClock.EXTRA_MESSAGE, "该换冷敷/热敷了")
			.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
		context.startActivity(intent)
	}.onFailure { Log.w(TAG, "No clock app for ACTION_SET_TIMER: $it") }
}
