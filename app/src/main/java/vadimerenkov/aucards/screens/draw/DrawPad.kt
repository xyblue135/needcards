package vadimerenkov.aucards.screens.draw

import androidx.compose.ui.geometry.Offset

/** 一次落笔到抬笔之间的笔迹 */
data class InkStroke(val points: List<Offset>)

/** 采样点太密就不存，否则一条线会存下几千个点 */
const val MIN_POINT_DISTANCE = 2.5f

fun shouldAddPoint(
	last: Offset,
	next: Offset,
	minDistance: Float = MIN_POINT_DISTANCE
): Boolean {
	val dx = next.x - last.x
	val dy = next.y - last.y
	return dx * dx + dy * dy >= minDistance * minDistance
}

/**
 * 手写板的全部状态：已完成的笔迹 + 正在写的那一笔。
 *
 * 故意做成不可变的纯数据，拖动时整块替换，方便单测；界面上只负责画。
 */
data class DrawPad(
	val strokes: List<InkStroke> = emptyList(),
	val current: InkStroke? = null
) {
	val isEmpty: Boolean
		get() = strokes.isEmpty() && current == null

	fun startAt(point: Offset): DrawPad = copy(current = InkStroke(listOf(point)))

	fun extendTo(point: Offset): DrawPad {
		val stroke = current ?: return startAt(point)
		if (!shouldAddPoint(stroke.points.last(), point)) return this
		return copy(current = InkStroke(stroke.points + point))
	}

	fun finishStroke(): DrawPad =
		if (current == null) this else copy(strokes = strokes + current, current = null)

	/** 正在写就取消这一笔，否则撤掉上一笔 */
	fun undo(): DrawPad = when {
		current != null -> copy(current = null)
		strokes.isNotEmpty() -> copy(strokes = strokes.dropLast(1))
		else -> this
	}

	fun clear(): DrawPad = DrawPad()
}
