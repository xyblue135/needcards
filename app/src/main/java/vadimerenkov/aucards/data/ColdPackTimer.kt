package vadimerenkov.aucards.data

/** 一段敷/停的时长：20 分钟 */
const val COLD_PACK_PHASE_SECONDS = 20 * 60

/**
 * 敷冰袋的节奏是「敷 20 分钟、停 20 分钟」，反复循环。
 * 下面几个都是纯函数，方便单测；界面只负责把秒数喂进来。
 */

/** 从开始到现在处在第几段（0 起）：偶数段敷、奇数段停 */
fun coldPackPhaseIndex(elapsedSeconds: Int): Int =
	maxOf(0, elapsedSeconds) / COLD_PACK_PHASE_SECONDS

/** 当前这一段是不是「敷」 */
fun coldPackIsCold(elapsedSeconds: Int): Boolean =
	coldPackPhaseIndex(elapsedSeconds) % 2 == 0

/** 当前这一段还剩多少秒；刚好整段开始时返回整段长度 */
fun coldPackRemaining(elapsedSeconds: Int): Int {
	val safe = maxOf(0, elapsedSeconds)
	return COLD_PACK_PHASE_SECONDS - (safe % COLD_PACK_PHASE_SECONDS)
}

/** 01:05 这种写法，超过一小时也只往上加分钟 */
fun formatCountdown(totalSeconds: Int): String {
	val safe = maxOf(0, totalSeconds)
	val minutes = safe / 60
	val seconds = safe % 60
	return "%02d:%02d".format(minutes, seconds)
}
