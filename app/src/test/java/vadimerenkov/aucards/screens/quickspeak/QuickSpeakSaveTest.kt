package vadimerenkov.aucards.screens.quickspeak

import android.app.Application
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import vadimerenkov.aucards.data.Aucard
import vadimerenkov.aucards.fakes.FakeDao
import vadimerenkov.aucards.fakes.TestDispatchers

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = Application::class)
class QuickSpeakSaveTest {

	@Test
	fun `存为卡片会追加到列表末尾`() = runBlocking {
		val dao = FakeDao()
		dao.saveAucard(Aucard(text = "已有卡片", index = 5))
		val viewModel = QuickSpeakViewModel(dao, TestDispatchers(), "  帮我拿个冰袋  ")

		var saved = false
		viewModel.save { saved = true }
		withTimeout(2_000) {
			while (!saved) delay(10)
		}

		val cards = dao.getAllCards().first()
		assertThat(cards).hasSize(2)
		assertThat(cards.last().text).isEqualTo("帮我拿个冰袋")
		assertThat(cards.last().index).isEqualTo(6)
	}

	@Test
	fun `重复点击保存只写一张`() = runBlocking {
		val dao = FakeDao()
		val viewModel = QuickSpeakViewModel(dao, TestDispatchers(), "谢谢")

		viewModel.save {}
		viewModel.save {}
		delay(100)

		assertThat(dao.getAllCards().first()).hasSize(1)
	}
}
