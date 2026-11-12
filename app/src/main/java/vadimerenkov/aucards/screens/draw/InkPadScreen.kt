package vadimerenkov.aucards.screens.draw

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import vadimerenkov.aucards.R

/**
 * 全屏手写板：说不出话、打字又慢的时候，写完把屏幕举给对方看。
 *
 * 纸和墨固定白底黑字，不跟随深色主题——这是要给别人看的东西，对比度越高越好。
 */
@Composable
fun InkPadScreen(
	onClose: () -> Unit,
	modifier: Modifier = Modifier
) {
	var pad by remember { mutableStateOf(DrawPad()) }

	// 举着给对方看的时候别自动锁屏
	val view = LocalView.current
	DisposableEffect(Unit) {
		view.keepScreenOn = true
		onDispose { view.keepScreenOn = false }
	}

	Column(
		modifier = modifier
			.fillMaxSize()
			.background(MaterialTheme.colorScheme.surface)
			.systemBarsPadding()
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 8.dp, vertical = 4.dp)
		) {
			Text(
				text = stringResource(R.string.handwriting),
				style = MaterialTheme.typography.titleMedium,
				modifier = Modifier.weight(1f)
			)
			TextButton(
				enabled = !pad.isEmpty,
				onClick = { pad = pad.undo() }
			) {
				Text(text = stringResource(R.string.undo))
			}
			TextButton(
				enabled = !pad.isEmpty,
				onClick = { pad = pad.clear() }
			) {
				Text(text = stringResource(R.string.clear))
			}
			Button(
				onClick = onClose,
				modifier = Modifier.padding(start = 4.dp)
			) {
				Text(text = stringResource(R.string.done))
			}
		}

		Box(
			modifier = Modifier
				.weight(1f)
				.padding(horizontal = 8.dp, vertical = 8.dp)
				.clip(RoundedCornerShape(12.dp))
				.background(PAPER)
		) {
			Canvas(
				modifier = Modifier
					.fillMaxSize()
					.pointerInput(Unit) {
						detectDragGestures(
							onDragStart = { offset -> pad = pad.startAt(offset) },
							onDrag = { change, _ ->
								pad = pad.extendTo(change.position)
								change.consume()
							},
							onDragEnd = { pad = pad.finishStroke() },
							onDragCancel = { pad = pad.finishStroke() }
						)
					}
			) {
				pad.strokes.forEach { stroke -> drawInk(stroke) }
				pad.current?.let { stroke -> drawInk(stroke) }
			}
			if (pad.isEmpty) {
				Text(
					text = stringResource(R.string.ink_pad_hint),
					color = Color(0xFF9E9E9E),
					textAlign = TextAlign.Center,
					modifier = Modifier
						.align(Alignment.Center)
						.padding(horizontal = 32.dp)
				)
			}
		}
	}
}

private val PAPER = Color.White
private val INK = Color(0xFF111111)

private fun DrawScope.drawInk(stroke: InkStroke) {
	val points = stroke.points
	if (points.isEmpty()) return
	val width = 5.dp.toPx()
	if (points.size == 1) {
		drawCircle(color = INK, radius = width / 2, center = points.first())
		return
	}
	val path = Path().apply {
		moveTo(points.first().x, points.first().y)
		for (index in 1 until points.size) {
			val point: Offset = points[index]
			lineTo(point.x, point.y)
		}
	}
	drawPath(
		path = path,
		color = INK,
		style = Stroke(width = width, cap = StrokeCap.Round, join = StrokeJoin.Round)
	)
}
