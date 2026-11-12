package vadimerenkov.aucards.data

import androidx.compose.ui.graphics.Color

/**
 * 预置卡片与分类，首次启动时写入数据库。
 * 场景：拔智齿 / 口腔手术后短期无法开口说话。
 */
object PresetCards {

	const val SEED_KEY = "preset_seeded_v1"

	// 分类 id 显式指定，便于卡片直接引用
	val categories: List<CardCategory> = listOf(
		CardCategory(id = 1, name = "先说一句", index = 1),
		CardCategory(id = 2, name = "疼与求助", index = 2),
		CardCategory(id = 3, name = "医护", index = 3),
		CardCategory(id = 4, name = "日常需求", index = 4),
		CardCategory(id = 5, name = "客套", index = 5),
		CardCategory(id = 6, name = "应付外人", index = 6)
	)

	private val purple = Color(0xFF7F77DD)
	private val red = Color(0xFFE24B4A)
	private val amber = Color(0xFFEF9F27)
	private val blue = Color(0xFF378ADD)
	private val green = Color(0xFF639922)
	private val gray = Color(0xFF888780)

	private data class Preset(
		val text: String,
		val description: String?,
		val color: Color,
		val categories: List<Int>,
		val favourite: Boolean = false
	)

	private fun sizeFor(text: String): Int = when {
		text.length <= 2 -> 88
		text.length == 3 -> 76
		text.length == 4 -> 66
		text.length == 5 -> 56
		else -> 46
	}

	private val presets: List<Preset> = listOf(
		// 1 先说一句
		Preset("我说不了话", "刚拔了智齿，嘴张不开，看我打字", purple, listOf(1), favourite = true),
		Preset("我听得见", "只是说不出声，打字给你看", purple, listOf(1)),
		Preset("请等一下", "我打出来给你看", purple, listOf(1)),

		// 2 疼与求助
		Preset("很疼", "疼得厉害", red, listOf(2), favourite = true),
		Preset("疼得受不了", "能不能加止痛药", red, listOf(2, 3)),
		Preset("这边肿了", "你看看正常吗", red, listOf(2, 3)),
		Preset("还在出血", "止不住", red, listOf(2, 3)),
		Preset("我有点发烧", "身上发冷", red, listOf(2, 3)),
		Preset("想吐", "不太舒服", red, listOf(2)),
		Preset("好多了", "不用担心", red, listOf(2)),

		// 3 医护
		Preset("帮我叫医生", null, amber, listOf(3), favourite = true),
		Preset("帮我叫护士", null, amber, listOf(3)),
		Preset("我对药过敏", null, amber, listOf(3)),
		Preset("什么时候复诊", null, amber, listOf(3)),
		Preset("几号拆线", null, amber, listOf(3)),
		Preset("这个药怎么吃", null, amber, listOf(3)),
		Preset("冰敷多久一次", null, amber, listOf(3)),

		// 4 日常需求
		Preset("想喝水", "温的", blue, listOf(4), favourite = true),
		Preset("想吃东西", "软的就行", blue, listOf(4)),
		Preset("给我冰袋", null, blue, listOf(4), favourite = true),
		Preset("帮我拿药", null, blue, listOf(4), favourite = true),
		Preset("扶我一下", null, blue, listOf(4)),
		Preset("我想躺下", null, blue, listOf(4)),
		Preset("关一下灯", "太亮了", blue, listOf(4)),
		Preset("手机递我", null, blue, listOf(4)),
		Preset("帮我拿纸笔", null, blue, listOf(4)),

		// 5 客套
		Preset("谢谢", null, green, listOf(5), favourite = true),
		Preset("等一下", null, green, listOf(5), favourite = true),
		Preset("不用了，谢谢", null, green, listOf(5)),
		Preset("我没事", "过两天就好", green, listOf(5)),

		// 6 应付外人
		Preset("说不了话", "刚拔智齿，不好意思", gray, listOf(6)),
		Preset("多少钱", "我扫码", gray, listOf(6)),
		Preset("放门口就行", "谢谢", gray, listOf(6)),
		Preset("打字聊吧", "说不了话", gray, listOf(6))
	)

	fun buildCards(): List<Aucard> = presets.mapIndexed { index, preset ->
		Aucard(
			text = preset.text,
			description = preset.description,
			color = preset.color,
			categories = preset.categories,
			isFavourite = preset.favourite,
			titleFontSize = sizeFor(preset.text),
			descriptionFontSize = 28,
			index = index
		)
	}
}
