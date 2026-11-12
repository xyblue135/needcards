package vadimerenkov.aucards.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecentPhrasesTest {

	@Test
	fun `新句子排在最前面`() {
		assertEquals(listOf("B", "A"), appendRecent(listOf("A"), "B"))
	}

	@Test
	fun `重复的句子提到最前面而不是出现两次`() {
		assertEquals(listOf("B", "A", "C"), appendRecent(listOf("A", "B", "C"), "B"))
	}

	@Test
	fun `超过上限丢掉最旧的`() {
		val existing = listOf("1", "2", "3", "4", "5")
		assertEquals(listOf("6", "1", "2", "3", "4"), appendRecent(existing, "6"))
	}

	@Test
	fun `空白句子不记录`() {
		val existing = listOf("A")
		assertEquals(existing, appendRecent(existing, ""))
		assertEquals(existing, appendRecent(existing, "   "))
		assertEquals(existing, appendRecent(existing, "\n"))
	}

	@Test
	fun `换行折成空格再记录`() {
		assertEquals(listOf("我要 喝水"), appendRecent(emptyList(), " 我要\n喝水 "))
	}

	@Test
	fun `存读一轮不丢内容`() {
		val list = listOf("我要喝水", "有点疼", "帮我拿冰袋")
		val encoded = encodeRecent(list)
		assertTrue(encoded.contains("\n"))
		assertEquals(list, decodeRecent(encoded))
	}

	@Test
	fun `没存过时是空列表`() {
		assertEquals(emptyList<String>(), decodeRecent(null))
		assertEquals(emptyList<String>(), decodeRecent(""))
	}
}
