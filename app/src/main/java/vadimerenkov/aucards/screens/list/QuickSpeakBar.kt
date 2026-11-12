package vadimerenkov.aucards.screens.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import vadimerenkov.aucards.R
import vadimerenkov.aucards.data.QuickSpeak

/**
 * 列表页底部的「临时说一句」输入条。
 *
 * 打完回车直接全屏显示并朗读，不建卡片；想留再在那一屏点「存为卡片」。
 * 这样临时想说的话不会把列表越堆越长。
 */
@Composable
fun QuickSpeakBar(
	onSend: (String) -> Unit,
	modifier: Modifier = Modifier
) {
	var text by remember { mutableStateOf("") }
	val keyboardController = LocalSoftwareKeyboardController.current

	fun send() {
		if (!QuickSpeak.isSendable(text)) return
		onSend(QuickSpeak.clean(text))
		text = ""
		keyboardController?.hide()
	}

	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		modifier = modifier
			.fillMaxWidth()
			.background(MaterialTheme.colorScheme.primaryContainer)
			.padding(horizontal = 12.dp, vertical = 6.dp)
	) {
		BasicTextField(
			value = text,
			onValueChange = { text = it.take(QuickSpeak.MAX_LENGTH) },
			singleLine = true,
			textStyle = MaterialTheme.typography.bodyLarge.copy(
				color = MaterialTheme.colorScheme.onSurface
			),
			cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
			keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
			keyboardActions = KeyboardActions(onSend = { send() }),
			decorationBox = { innerTextField ->
				Box(
					contentAlignment = Alignment.CenterStart,
					modifier = Modifier
						.fillMaxWidth()
						.clip(RoundedCornerShape(24.dp))
						.background(MaterialTheme.colorScheme.surface)
						.padding(horizontal = 16.dp, vertical = 12.dp)
				) {
					if (text.isEmpty()) {
						Text(
							text = stringResource(R.string.quick_speak_hint),
							style = MaterialTheme.typography.bodyLarge,
							color = MaterialTheme.colorScheme.onSurfaceVariant
						)
					}
					innerTextField()
				}
			},
			modifier = Modifier.weight(1f)
		)
		IconButton(
			onClick = { send() },
			enabled = QuickSpeak.isSendable(text)
		) {
			Icon(
				imageVector = Icons.AutoMirrored.Filled.Send,
				contentDescription = stringResource(R.string.quick_speak_send),
				tint = MaterialTheme.colorScheme.primary
			)
		}
	}
}
