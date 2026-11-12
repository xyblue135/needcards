package vadimerenkov.aucards.data

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * 纯 JVM 校验，不需要设备：确认预设卡片结构与分类引用自洽。
 * 真机上「有没有写进数据库」另有风险，但这里能挡住数据本身的错误。
 */
class PresetCardsTest {

	@Test
	fun `分类 id 唯一且连续`() {
		val ids = PresetCards.categories.map { it.id }
		assertThat(ids).containsNoDuplicates()
		assertThat(ids).isEqualTo((1..PresetCards.categories.size).toList())
	}

	@Test
	fun `分类名非空且不重复`() {
		val names = PresetCards.categories.map { it.name }
		assertThat(names).containsNoDuplicates()
		names.forEach { assertThat(it.trim()).isNotEmpty() }
	}

	@Test
	fun `卡片数量符合预期`() {
		assertThat(PresetCards.buildCards()).hasSize(34)
	}

	@Test
	fun `每张卡片都归入了已存在的分类`() {
		val validIds = PresetCards.categories.map { it.id }.toSet()
		PresetCards.buildCards().forEach { card ->
			assertThat(card.categories).isNotEmpty()
			card.categories.forEach { id ->
				assertThat(validIds).contains(id)
			}
		}
	}

	@Test
	fun `卡片正文非空`() {
		PresetCards.buildCards().forEach { card ->
			assertThat(card.text.trim()).isNotEmpty()
		}
	}

	@Test
	fun `排序索引连续`() {
		val indexes = PresetCards.buildCards().map { it.index }
		assertThat(indexes).isEqualTo(indexes.sorted())
		assertThat(indexes).containsNoDuplicates()
	}

	@Test
	fun `字号在可读范围内`() {
		PresetCards.buildCards().forEach { card ->
			assertThat(card.titleFontSize).isIn(40..100)
			assertThat(card.descriptionFontSize).isIn(16..40)
		}
	}

	@Test
	fun `六个分类都有卡片`() {
		val cards = PresetCards.buildCards()
		PresetCards.categories.forEach { category ->
			val count = cards.count { it.categories.contains(category.id) }
			assertThat(count).isGreaterThan(0)
		}
	}
}
