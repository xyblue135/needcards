package vadimerenkov.aucards.screens.draw

import androidx.compose.ui.geometry.Offset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DrawPadTest {

	@Test
	fun `刚开始是空的`() {
		assertTrue(DrawPad().isEmpty)
		assertNull(DrawPad().current)
	}

	@Test
	fun `落笔就是一条只有起点的笔画`() {
		val pad = DrawPad().startAt(Offset(10f, 10f))
		assertEquals(listOf(Offset(10f, 10f)), pad.current?.points)
		assertFalse(pad.isEmpty)
	}

	@Test
	fun `距离太近的点不记录`() {
		val pad = DrawPad().startAt(Offset(0f, 0f)).extendTo(Offset(1f, 0f))
		assertEquals(1, pad.current?.points?.size)
	}

	@Test
	fun `距离够远才追加`() {
		val pad = DrawPad().startAt(Offset(0f, 0f)).extendTo(Offset(30f, 40f))
		assertEquals(listOf(Offset(0f, 0f), Offset(30f, 40f)), pad.current?.points)
	}

	@Test
	fun `抬笔后这一笔进列表`() {
		val pad = DrawPad()
			.startAt(Offset(0f, 0f))
			.extendTo(Offset(20f, 0f))
			.finishStroke()
		assertEquals(1, pad.strokes.size)
		assertEquals(listOf(Offset(0f, 0f), Offset(20f, 0f)), pad.strokes.first().points)
		assertNull(pad.current)
	}

	@Test
	fun `撤销先取消正在写的一笔`() {
		val pad = DrawPad().startAt(Offset(0f, 0f)).undo()
		assertTrue(pad.isEmpty)
	}

	@Test
	fun `撤销再删掉上一笔`() {
		val pad = DrawPad()
			.startAt(Offset(0f, 0f))
			.finishStroke()
			.startAt(Offset(5f, 5f))
			.finishStroke()
			.undo()
		assertEquals(1, pad.strokes.size)
	}

	@Test
	fun `空板撤销不出错`() {
		assertTrue(DrawPad().undo().isEmpty)
	}

	@Test
	fun `清空回到初始`() {
		val pad = DrawPad()
			.startAt(Offset(0f, 0f))
			.finishStroke()
			.clear()
		assertTrue(pad.isEmpty)
	}
}
