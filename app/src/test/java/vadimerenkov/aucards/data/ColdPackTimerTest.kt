package vadimerenkov.aucards.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ColdPackTimerTest {

	@Test
	fun `第一段是敷`() {
		assertEquals(0, coldPackPhaseIndex(0))
		assertTrue(coldPackIsCold(0))
		assertTrue(coldPackIsCold(1199))
	}

	@Test
	fun `第二段是停`() {
		assertEquals(1, coldPackPhaseIndex(1200))
		assertFalse(coldPackIsCold(1200))
		assertFalse(coldPackIsCold(2399))
	}

	@Test
	fun `第三段又回到敷`() {
		assertTrue(coldPackIsCold(2400))
		assertEquals(2, coldPackPhaseIndex(2400))
	}

	@Test
	fun `倒计时在整段开始时是满的`() {
		assertEquals(1200, coldPackRemaining(0))
		assertEquals(1200, coldPackRemaining(1200))
		assertEquals(1200, coldPackRemaining(2400))
	}

	@Test
	fun `倒计时逐秒递减且到点归零`() {
		assertEquals(1199, coldPackRemaining(1))
		assertEquals(1, coldPackRemaining(1199))
		assertEquals(1, coldPackRemaining(2399))
	}

	@Test
	fun `负数当作刚开始`() {
		assertEquals(0, coldPackPhaseIndex(-5))
		assertEquals(1200, coldPackRemaining(-5))
	}

	@Test
	fun `时间格式补零`() {
		assertEquals("20:00", formatCountdown(1200))
		assertEquals("00:07", formatCountdown(7))
		assertEquals("01:05", formatCountdown(65))
		assertEquals("00:00", formatCountdown(-3))
	}
}
