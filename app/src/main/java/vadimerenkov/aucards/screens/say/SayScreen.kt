package vadimerenkov.aucards.screens.say

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import vadimerenkov.aucards.R
import vadimerenkov.aucards.data.QuickSpeak

/**
 * 「说一句」页：临时要说的话不用先做成卡片。
 *
 * 进来就聚焦输入框，打完点下面或按键盘上的发送，立刻全屏显示并朗读；
 * 想留着当常用语，在那一屏点「存为卡片」。
 */
@Composable
fun SayScreen(
	onSpeak: (String) -> Unit,
	modifier: Modifier = Modifier
) {
	var text by rememberSaveable { mutableStateOf("") }
	val focusRequester = remember { FocusRequester() }
	val keyboard = LocalSoftwareKeyboardController.current
	val canSend = QuickSpeak.isSendable(text)

	fun send() {
		if (!canSend) return
		val sentence = QuickSpeak.clean(text)
		text = ""
		keyboard?.hide()
		onSpeak(sentence)
	}

	// 点这一页就是为了说话，直接给焦点。等一帧再抢，否则部分机型不弹键盘
	LaunchedEffect(Unit) {
		delay(120)
		focusRequester.requestFocus()
		keyboard?.show()
	}

	Column(
		verticalArrangement = Arrangement.spacedBy(12.dp),
		modifier = modifier
			.fillMaxSize()
			.imePadding()
			.padding(horizontal = 16.dp, vertical = 12.dp)
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier.fillMaxWidth()
		) {
			Text(
				text = stringResource(R.string.say_hint),
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.weight(1f)
			)
			if (text.isNotEmpty()) {
				TextButton(onClick = { text = "" }) {
					Text(text = stringResource(R.string.clear))
				}
			}
		}
		OutlinedTextField(
			value = text,
			onValueChange = { text = it.take(QuickSpeak.MAX_LENGTH) },
			placeholder = {
				Text(text = stringResource(R.string.quick_speak_hint))
			},
			textStyle = MaterialTheme.typography.headlineSmall,
			keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
			keyboardActions = KeyboardActions(onSend = { send() }),
			shape = MaterialTheme.shapes.medium,
			modifier = Modifier
				.weight(1f)
				.fillMaxWidth()
				.focusRequester(focusRequester)
		)
		Button(
			onClick = { send() },
			enabled = canSend,
			shape = MaterialTheme.shapes.medium,
			modifier = Modifier
				.fillMaxWidth()
				.heightIn(min = 56.dp)
		) {
			Icon(
				imageVector = Icons.AutoMirrored.Filled.Send,
				contentDescription = null
			)
			Text(
				text = stringResource(R.string.quick_speak_send),
				style = MaterialTheme.typography.titleMedium,
				modifier = Modifier.padding(start = 8.dp)
			)
		}
	}
}
