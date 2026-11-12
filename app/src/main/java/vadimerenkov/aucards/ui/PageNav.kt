package vadimerenkov.aucards.ui

import kotlin.math.abs

/**
 * 底部导航切页时该不该「瞬移」。
 *
 * 跨过中间页（0 → 2）时如果还做滑动动画，页面会从中间那一页滑过去，既让人以为点错了，
 * 也会在动画中途产生中间页的 currentPage。相邻页则保留滑动，衔接更自然。
 */
fun shouldJumpPage(from: Int, to: Int): Boolean = abs(to - from) > 1
