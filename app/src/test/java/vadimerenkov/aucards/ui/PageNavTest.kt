package vadimerenkov.aucards.ui

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PageNavTest {

	@Test
	fun `同一个页面不需要跳转`() {
		assertFalse(shouldJumpPage(0, 0))
		assertFalse(shouldJumpPage(2, 2))
	}

	@Test
	fun `相邻页面用滑动动画`() {
		assertFalse(shouldJumpPage(0, 1))
		assertFalse(shouldJumpPage(1, 2))
		assertFalse(shouldJumpPage(2, 1))
		assertFalse(shouldJumpPage(1, 0))
	}

	@Test
	fun `跨过中间页面时直接跳过去`() {
		assertTrue(shouldJumpPage(0, 2))
		assertTrue(shouldJumpPage(2, 0))
	}

	@Test
	fun `距离更远的页面同样直接跳`() {
		assertTrue(shouldJumpPage(0, 3))
		assertTrue(shouldJumpPage(4, 1))
	}
}
