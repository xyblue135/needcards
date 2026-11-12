package vadimerenkov.aucards

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import vadimerenkov.aucards.data.AucardDao
import vadimerenkov.aucards.data.PresetSeeder
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

		scope.launch {
			try {
				if (PresetSeeder.seedOnce(dao, dataStore)) {
					Log.i(TAG, "Preset cards seeded")
				}
			} catch (e: Exception) {
				Log.e(TAG, "Failed to seed preset cards: $e")
			}
		}
	}
}
