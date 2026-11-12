package vadimerenkov.aucards.data

import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * 一个术后阶段的护理建议。
 * 天数按「术后第几天」算，拔牙当天是第 1 天。
 */
data class RecoveryStage(
	val fromDay: Int,
	val toDay: Int,
	val title: String,
	val summary: String,
	val tips: List<String>
) {
	fun covers(day: Int): Boolean = day in fromDay..toDay

	/** 「第 1 天」「第 4-6 天」「第 15 天以后」这种给人看的写法。 */
	val dayLabel: String
		get() = when {
			toDay == Int.MAX_VALUE -> "第 $fromDay 天以后"
			fromDay == toDay -> "第 $fromDay 天"
			else -> "第 $fromDay-$toDay 天"
		}
}

/**
 * 拔牙后第几天。拔牙当天算第 1 天。
 * 没设日期、或者日期在未来，都返回 null。
 */
fun daysSinceExtraction(extraction: LocalDate?, today: LocalDate = LocalDate.now()): Int? {
	if (extraction == null) return null
	val elapsed = ChronoUnit.DAYS.between(extraction, today)
	if (elapsed < 0) return null
	return elapsed.toInt() + 1
}

val recoveryStages: List<RecoveryStage> = listOf(
	RecoveryStage(
		fromDay = 1,
		toDay = 1,
		title = "手术当天",
		summary = "止血和护住血块，这一天最关键",
		tips = listOf(
			"棉球咬 30-40 分钟就吐掉，咬太久反而容易出血",
			"24 小时内别刷牙、别漱口、别吐口水、别用吸管，负压会把血块带出来",
			"冰敷：敷 15 分钟歇 15 分钟，间断做，别一直贴着",
			"麻药没退之前别吃东西，容易咬伤舌头和嘴唇",
			"两小时后可以喝温凉流质，别用患侧嚼",
			"口水里带血丝是正常的，咽下去，别反复吐"
		)
	),
	RecoveryStage(
		fromDay = 2,
		toDay = 2,
		title = "第 2 天",
		summary = "肿开始起来，按时吃药",
		tips = listOf(
			"肿胀通常在这天明显，48-72 小时到高峰，别慌",
			"继续冰敷；过了 24 小时也可以改成温热敷",
			"止痛药按医生说的吃，疼得厉害别硬扛",
			"别剧烈运动、别弯腰搬重物、别洗太热的澡",
			"睡觉把枕头垫高一点，能减轻肿胀"
		)
	),
	RecoveryStage(
		fromDay = 3,
		toDay = 3,
		title = "第 3 天",
		summary = "肿到顶了，之后会往回收",
		tips = listOf(
			"这天往往最肿，从明天开始会慢慢消",
			"饭后用温盐水轻轻含漱，别用力漱口",
			"张不开嘴是正常的，别硬撑",
			"可以吃蒸蛋、土豆泥、软面条这类不用嚼的东西"
		)
	),
	RecoveryStage(
		fromDay = 4,
		toDay = 6,
		title = "第 4-6 天",
		summary = "消肿期，别去刺激创口",
		tips = listOf(
			"肿明显往下走，可以正常刷牙了，但避开创口那一块",
			"还是别吃辣的、烫的、硬的、脆的",
			"别抽烟喝酒，会拖慢愈合",
			"创口塞了食物，用温盐水轻轻冲，别用牙签抠"
		)
	),
	RecoveryStage(
		fromDay = 7,
		toDay = 7,
		title = "第 7 天",
		summary = "通常这天复诊拆线",
		tips = listOf(
			"一般术后 7 天复诊，可吸收线不用拆，让医生看一眼创口",
			"拆线后还要继续保持口腔清洁",
			"如果这天还明显疼、有异味，跟医生说清楚"
		)
	),
	RecoveryStage(
		fromDay = 8,
		toDay = 14,
		title = "第 8-14 天",
		summary = "基本恢复，饮食慢慢放开",
		tips = listOf(
			"吃喝基本正常了，硬的脆的还是缓一缓",
			"每次吃完漱口，别让食物渣留在洞里",
			"刷牙正常刷，创口附近轻一点"
		)
	),
	RecoveryStage(
		fromDay = 15,
		toDay = Int.MAX_VALUE,
		title = "半个月以后",
		summary = "牙槽窝在慢慢长平",
		tips = listOf(
			"那个洞要 1-3 个月才完全填平，是正常现象",
			"吃完东西漱口，别用舌头去顶、别用手抠",
			"如果一直有异味或疼痛，去复查"
		)
	)
)

/** 出现这些情况别等，直接联系医生或去医院。 */
val recoveryWarningSigns: List<String> = listOf(
	"咬棉球 1 小时后还在大量出血",
	"第 4 天以后疼痛或肿胀反而加重",
	"发烧超过 38.5℃",
	"吞咽、张嘴感觉困难",
	"嘴唇或舌头麻木超过一天没恢复",
	"创口有臭味、流脓，可能是干槽症，尽快复诊"
)

fun stageForDay(day: Int): RecoveryStage =
	recoveryStages.firstOrNull { it.covers(day) } ?: recoveryStages.last()
