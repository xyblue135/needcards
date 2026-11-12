package vadimerenkov.aucards.data

import androidx.compose.ui.graphics.Color

/**
 * 「临时说一句」：一段文字直接变成可以全屏显示和朗读的卡片，不落库。
 *
 * 说话的时候没精力调样式，所以字号按字数自动给，颜色固定，
 * 想留着当常用卡再去点「存为卡片」。
 */
object QuickSpeak {
	const val MAX_LENGTH = 120

	// 和预设里「开场说明」同一支紫色，临时卡片一眼能认出来。
	val color = Color(0xFF8E7CC3)

	fun isSendable(raw: String): Boolean = raw.trim().isNotEmpty()

	fun clean(raw: String): String = raw.trim()

	fun titleSizeFor(text: String): Int = when {
		text.length <= 2 -> 88
		text.length == 3 -> 76
		text.length == 4 -> 66
		text.length == 5 -> 56
		text.length == 6 -> 46
		text.length <= 10 -> 40
		text.length <= 16 -> 32
		else -> 24
	}

	fun toCard(raw: String, index: Int = 0): Aucard {
		val text = clean(raw)
		return Aucard(
			text = text,
			color = color,
			titleFontSize = titleSizeFor(text),
			index = index
		)
	}
}
