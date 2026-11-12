package vadimerenkov.aucards.screens.settings

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
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
 * 朗读开关在这个分支里默认是开的：用户说不出话，App 一打开就该替他开口。
 * 但用户自己关掉之后不能再被默认值翻回来。
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = Application::class)
class SettingsDefaultsTest {

	private lateinit var context: Context
	private lateinit var dataStore: DataStore<Preferences>
	private lateinit var scope: CoroutineScope
	private lateinit var settings: Settings

	@Before
	fun setUp() {
		context = ApplicationProvider.getApplicationContext()
		scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
		val storeFile = File(context.cacheDir, "settings_test_${System.nanoTime()}.preferences_pb")
		dataStore = PreferenceDataStoreFactory.create(scope = scope) { storeFile }
		settings = Settings(dataStore, scope)
	}

	@After
	fun tearDown() {
		scope.cancel()
	}

	@Test
	fun `首次启动朗读默认开启`() = runBlocking {
		assertThat(settings.voice.first()).isEqualTo(true)
	}

	@Test
	fun `用户关掉朗读后不会被默认值覆盖`() = runBlocking {
		settings.saveBoolSettings(VOICE_CARD, false)

		assertThat(settings.voice.first()).isEqualTo(false)
	}

	@Test
	fun `关掉再打开仍然保持关闭`() = runBlocking {
		settings.saveBoolSettings(VOICE_CARD, false)
		// 模拟换一个实例重新读，等价于杀死进程后再进应用
		val reopened = Settings(dataStore, scope)

		assertThat(reopened.voice.first()).isEqualTo(false)
	}
}
