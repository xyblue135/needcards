package vadimerenkov.aucards.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import vadimerenkov.aucards.screens.settings.RECENT_PHRASES

/** 最多记几条，一行放得下、又不至于把屏幕塞满 */
const val RECENT_PHRASES_LIMIT = 5

/**
 * 把一句话插到最前面：重复的提到最前、超出条数丢掉、空的不记。
 *
 * 一天的对话里「我要喝水」「有点疼」这种会反复说，
 * 记下来点一下就能重念，比每次重新打字省事。
 */
fun appendRecent(
	existing: List<String>,
	text: String,
	limit: Int = RECENT_PHRASES_LIMIT
): List<String> {
	val clean = text.replace('\n', ' ').trim()
	if (clean.isEmpty()) return existing
	return (listOf(clean) + existing.filterNot { it == clean }).take(limit)
}

/** 存成一行一句话：句子里不会再有换行，所以直接用 \n 分隔最省事 */
fun encodeRecent(list: List<String>): String =
	list.joinToString("\n") { it.replace('\n', ' ') }

fun decodeRecent(raw: String?): List<String> =
	raw?.split('\n')?.filter { it.isNotBlank() } ?: emptyList()

class RecentPhrases(
	private val dataStore: DataStore<Preferences>
) {
	private val key = stringPreferencesKey(RECENT_PHRASES)

	val items: Flow<List<String>> = dataStore.data
		.map { preferences -> decodeRecent(preferences[key]) }

	/** 读最新值再写，避免连续两句时后一句把前一句覆盖掉 */
	suspend fun add(text: String) {
		dataStore.edit { preferences ->
			val current = decodeRecent(preferences[key])
			preferences[key] = encodeRecent(appendRecent(current, text))
		}
	}
}
