package vadimerenkov.aucards.data

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

/**
 * 真跑一次「首次启动写入预设」的完整链路：
 * Room 真实 SQLite（内存库）+ 真实 DataStore，只是不需要真机。
 *
 * application 指定成系统 Application，避免触发 AucardsApplication.onCreate
 * 里的 startKoin 和它自己的 seed。
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = Application::class)
class PresetSeederTest {

	private lateinit var context: Context
	private lateinit var db: AucardsDatabase
	private lateinit var dataStore: DataStore<Preferences>
	private lateinit var scope: CoroutineScope

	@Before
	fun setUp() {
		context = ApplicationProvider.getApplicationContext()
		db = Room.inMemoryDatabaseBuilder(context, AucardsDatabase::class.java)
			.allowMainThreadQueries()
			.build()
		scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
		val storeFile = File(context.cacheDir, "seed_test_${System.nanoTime()}.preferences_pb")
		dataStore = PreferenceDataStoreFactory.create(scope = scope) { storeFile }
	}

	@After
	fun tearDown() {
		db.close()
		scope.cancel()
	}

	private fun cards() = runBlocking { db.aucardDao().getAllCards().first() }
	private fun categories() = runBlocking { db.aucardDao().getAllCategories().first() }

	@Test
	fun `首次启动写入 34 张卡片和 6 个分类`() = runBlocking {
		val wrote = PresetSeeder.seedOnce(db.aucardDao(), dataStore)

		assertThat(wrote).isTrue()
		assertThat(cards()).hasSize(34)
		assertThat(categories()).hasSize(6)
	}

	@Test
	fun `再次启动不会重复写入`() = runBlocking {
		PresetSeeder.seedOnce(db.aucardDao(), dataStore)
		val wroteAgain = PresetSeeder.seedOnce(db.aucardDao(), dataStore)

		assertThat(wroteAgain).isFalse()
		assertThat(cards()).hasSize(34)
	}

	@Test
	fun `用户删光卡片后不会被重新灌入`() = runBlocking {
		PresetSeeder.seedOnce(db.aucardDao(), dataStore)
		cards().forEach { db.aucardDao().deleteById(it.id) }
		assertThat(cards()).isEmpty()

		val wrote = PresetSeeder.seedOnce(db.aucardDao(), dataStore)

		assertThat(wrote).isFalse()
		assertThat(cards()).isEmpty()
	}

	@Test
	fun `落库后分类引用仍然有效且配色多样`() = runBlocking {
		PresetSeeder.seedOnce(db.aucardDao(), dataStore)
		val savedCards = cards()
		val savedCategoryIds = categories().map { it.id }.toSet()

		savedCards.forEach { card ->
			assertThat(savedCategoryIds).containsAtLeastElementsIn(card.categories)
		}
		assertThat(savedCards.map { it.color }.distinct().size).isGreaterThan(1)
	}

	@Test
	fun `落库顺序与预设索引一致`() = runBlocking {
		PresetSeeder.seedOnce(db.aucardDao(), dataStore)

		val texts = cards().map { it.text }

		assertThat(texts).isEqualTo(PresetCards.buildCards().map { it.text })
	}

	@Test
	fun `收藏标记能正确落库`() = runBlocking {
		PresetSeeder.seedOnce(db.aucardDao(), dataStore)

		val favourites = db.aucardDao().getFavouriteCards().first()

		assertThat(favourites).hasSize(8)
		assertThat(favourites.map { it.text }).contains("我说不了话")
	}
}
