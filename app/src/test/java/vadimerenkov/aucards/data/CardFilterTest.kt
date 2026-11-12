package vadimerenkov.aucards.data

import org.junit.Assert.assertEquals
import org.junit.Test

class CardFilterTest {

	private fun card(id: Int, favourite: Boolean = false, vararg categories: Int) = Aucard(
		id = id,
		text = "card $id",
		isFavourite = favourite,
		categories = categories.toList()
	)

	private val cards = listOf(
		card(1, favourite = true, 10),
		card(2, favourite = false, 10, 11),
		card(3, favourite = false, 11),
		card(4, favourite = true)
	)

	@Test
	fun `不加筛选时按原顺序返回全部`() {
		val result = filterPhrases(cards, favouritesOnly = false, categoryId = null)
		assertEquals(listOf(1, 2, 3, 4), result.map { it.id })
	}

	@Test
	fun `只看收藏`() {
		val result = filterPhrases(cards, favouritesOnly = true, categoryId = null)
		assertEquals(listOf(1, 4), result.map { it.id })
	}

	@Test
	fun `只看某个分组`() {
		val result = filterPhrases(cards, favouritesOnly = false, categoryId = 11)
		assertEquals(listOf(2, 3), result.map { it.id })
	}

	@Test
	fun `空分组返回空列表`() {
		val result = filterPhrases(cards, favouritesOnly = false, categoryId = 99)
		assertEquals(emptyList<Aucard>(), result)
	}

	@Test
	fun `收藏筛选优先于分组`() {
		// ViewModel 保证二者互斥，这里只锁死「同时为真时」的行为，避免出现两种结果
		val result = filterPhrases(cards, favouritesOnly = true, categoryId = 11)
		assertEquals(listOf(1, 4), result.map { it.id })
	}
}
