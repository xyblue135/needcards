package vadimerenkov.aucards.screens.quickspeak

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vadimerenkov.aucards.DispatchersProvider
import vadimerenkov.aucards.data.AucardDao
import vadimerenkov.aucards.data.QuickSpeak

class QuickSpeakViewModel(
	private val aucardDao: AucardDao,
	private val dispatchers: DispatchersProvider,
	rawText: String
) : ViewModel() {

	private val card = QuickSpeak.toCard(rawText)

	val text: String = card.text
	val color = card.color
	val titleFontSize: Int = card.titleFontSize

	private val _isSaving = MutableStateFlow(false)
	val isSaving = _isSaving.asStateFlow()

	// 存过就不许再存，防止连点或返回栈复用把这个 ID 写成两张卡。
	private var isAlreadySaved = false

	/** 存成一张普通卡片，排在列表最后。 */
	fun save(onSaved: () -> Unit) {
		if (isAlreadySaved || _isSaving.value) return
		isAlreadySaved = true
		_isSaving.value = true
		viewModelScope.launch(dispatchers.io) {
			val nextIndex = aucardDao.getAllCards().first()
				.maxOfOrNull { it.index }
				?.plus(1)
				?: 0
			aucardDao.saveAucard(card.copy(index = nextIndex))
			withContext(dispatchers.main) {
				_isSaving.value = false
				onSaved()
			}
		}
	}
}
