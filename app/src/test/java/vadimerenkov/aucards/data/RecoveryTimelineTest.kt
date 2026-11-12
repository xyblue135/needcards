package vadimerenkov.aucards.data

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDate

/**
 * 纯 JVM 校验恢复时间线：天数换算、阶段边界、没有空洞和重叠。
 */
class RecoveryTimelineTest {

	private val extraction = LocalDate.of(2026, 11, 12)

	@Test
	fun `拔牙当天算第一天`() {
		assertThat(daysSinceExtraction(extraction, extraction)).isEqualTo(1)
	}

	@Test
	fun `第二天和第七天`() {
		assertThat(daysSinceExtraction(extraction, extraction.plusDays(1))).isEqualTo(2)
		assertThat(daysSinceExtraction(extraction, extraction.plusDays(6))).isEqualTo(7)
	}

	@Test
	fun `没设日期返回 null`() {
		assertThat(daysSinceExtraction(null, extraction)).isNull()
	}

	@Test
	fun `日期在未来返回 null`() {
		assertThat(daysSinceExtraction(extraction, extraction.minusDays(1))).isNull()
	}

	@Test
	fun `阶段连续覆盖且不重叠`() {
		val stages = recoveryStages
		assertThat(stages.first().fromDay).isEqualTo(1)
		stages.zipWithNext { current, next ->
			assertThat(next.fromDay).isEqualTo(current.toDay + 1)
		}
		assertThat(stages.last().toDay).isEqualTo(Int.MAX_VALUE)
	}

	@Test
	fun `任意一天都能落到某个阶段`() {
		(1..400).forEach { day ->
			val stage = stageForDay(day)
			assertThat(stage.covers(day)).isTrue()
		}
	}

	@Test
	fun `关键天数对应到预期的阶段`() {
		assertThat(stageForDay(1).title).isEqualTo("手术当天")
		assertThat(stageForDay(3).title).isEqualTo("第 3 天")
		assertThat(stageForDay(5).title).isEqualTo("第 4-6 天")
		assertThat(stageForDay(7).title).isEqualTo("第 7 天")
		assertThat(stageForDay(14).title).isEqualTo("第 8-14 天")
		assertThat(stageForDay(30).title).isEqualTo("半个月以后")
	}

	@Test
	fun `阶段标题唯一且提示非空`() {
		val titles = recoveryStages.map { it.title }
		assertThat(titles).containsNoDuplicates()
		recoveryStages.forEach { stage ->
			assertThat(stage.summary.trim()).isNotEmpty()
			assertThat(stage.tips).isNotEmpty()
			stage.tips.forEach { assertThat(it.trim()).isNotEmpty() }
		}
	}

	@Test
	fun `天标签写法`() {
		assertThat(stageForDay(1).dayLabel).isEqualTo("第 1 天")
		assertThat(stageForDay(5).dayLabel).isEqualTo("第 4-6 天")
		assertThat(stageForDay(20).dayLabel).isEqualTo("第 15 天以后")
	}

	@Test
	fun `警示信号不为空`() {
		assertThat(recoveryWarningSigns).isNotEmpty()
		recoveryWarningSigns.forEach { assertThat(it.trim()).isNotEmpty() }
	}
}
