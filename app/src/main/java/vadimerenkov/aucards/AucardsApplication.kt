package vadimerenkov.aucards

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import vadimerenkov.aucards.data.AucardDao
import vadimerenkov.aucards.data.PresetCards
import vadimerenkov.aucards.di.appModule

private const val TAG = "AucardsApplication"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("aucards_settings")

class AucardsApplication: Application() {

	override fun onCreate() {
		super.onCreate()

		startKoin {
			androidContext(this@AucardsApplication)
			modules(appModule)
		}

		seedPresetCards()
	}

	private fun seedPresetCards() {
		val koin = GlobalContext.get()
		val scope: CoroutineScope = koin.get()
		val dataStore: DataStore<Preferences> = koin.get()
		val dao: AucardDao = koin.get()
		val key = booleanPreferencesKey(PresetCards.SEED_KEY)

		scope.launch {
			try {
				if (dataStore.data.first()[key] == true) return@launch
				dao.saveAllCategories(PresetCards.categories)
				dao.saveAllCards(PresetCards.buildCards())
				dataStore.edit { it[key] = true }
				Log.i(TAG, "Preset cards seeded")
			} catch (e: Exception) {
				Log.e(TAG, "Failed to seed preset cards: $e")
			}
		}
	}
}
