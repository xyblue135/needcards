package vadimerenkov.aucards.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first

/**
 * 首次启动写入预置卡片，用 DataStore 标记保证只写一次。
 * 抽成独立对象是为了能在不启动 Application 的情况下测试。
 */
object PresetSeeder {

	val seedKey = booleanPreferencesKey(PresetCards.SEED_KEY)

	/**
	 * 幂等：已经写过就直接返回，用户删光卡片后也不会被重新灌入。
	 *
	 * @return 本次是否真的写入了卡片
	 */
	suspend fun seedOnce(
		dao: AucardDao,
		dataStore: DataStore<Preferences>
	): Boolean {
		if (dataStore.data.first()[seedKey] == true) return false

		dao.saveAllCategories(PresetCards.categories)
		dao.saveAllCards(PresetCards.buildCards())
		dataStore.edit { it[seedKey] = true }
		return true
	}
}
