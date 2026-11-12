package vadimerenkov.aucards.data

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class QuickSpeakTest {

	@Test
	fun `空白输入不算可发送`() {
		assertThat(QuickSpeak.isSendable("")).isFalse()
		assertThat(QuickSpeak.isSendable("   ")).isFalse()
		assertThat(QuickSpeak.isSendable("\n\t")).isFalse()
		assertThat(QuickSpeak.isSendable("想喝水")).isTrue()
	}

	@Test
	fun `首尾空白会被清掉`() {
		assertThat(QuickSpeak.clean("  帮我拿个冰袋  ")).isEqualTo("帮我拿个冰袋")
		assertThat(QuickSpeak.toCard("  帮我拿个冰袋  ").text).isEqualTo("帮我拿个冰袋")
	}

	@Test
	fun `字号按字数递减且不越界`() {
		// 一个和两个字都是最大字号，从三个字开始逐档变小
		assertThat(QuickSpeak.titleSizeFor("好")).isEqualTo(88)
		assertThat(QuickSpeak.titleSizeFor("你好")).isEqualTo(88)

		val sizes = listOf(3, 4, 5, 6, 8, 12, 40).map { n ->
			QuickSpeak.titleSizeFor("字".repeat(n))
		}

		// 字号随字数递减，反过来说就是升序
		assertThat(sizes.reversed()).isInOrder()
		assertThat(sizes.distinct()).hasSize(sizes.size)
		assertThat(sizes.last()).isEqualTo(24)
	}

	@Test
	fun `临时卡片不带说明文字并沿用固定色`() {
		val card = QuickSpeak.toCard("好", index = 7)

		assertThat(card.description).isNull()
		assertThat(card.color).isEqualTo(QuickSpeak.color)
		assertThat(card.layout).isEqualTo(CardLayout.TITLE_SUBTITLE)
		assertThat(card.index).isEqualTo(7)
		// id 保持 0，Room 才会自增
		assertThat(card.id).isEqualTo(0)
	}
}
