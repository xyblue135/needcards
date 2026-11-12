package vadimerenkov.aucards.data

/**
 * 常用语页的可见卡片。
 *
 * 页面上只有「全部 / 收藏 / 某个分组」三种状态，且互斥（切换时由 ViewModel 保证）。
 * 抽成纯函数是为了能直接单测，不用起 Compose。
 */
fun filterPhrases(
	cards: List<Aucard>,
	favouritesOnly: Boolean,
	categoryId: Int?
): List<Aucard> = when {
	favouritesOnly -> cards.filter { it.isFavourite }
	categoryId != null -> cards.filter { it.categories.contains(categoryId) }
	else -> cards
}
