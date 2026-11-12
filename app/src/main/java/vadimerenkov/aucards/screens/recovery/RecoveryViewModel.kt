package vadimerenkov.aucards.screens.recovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import vadimerenkov.aucards.data.RecoveryStage
import vadimerenkov.aucards.data.daysSinceExtraction
import vadimerenkov.aucards.data.stageForDay
import vadimerenkov.aucards.screens.settings.EXTRACTION_DATE
import vadimerenkov.aucards.screens.settings.Settings
import java.time.LocalDate

data class RecoveryUiState(
	val extractionDate: LocalDate? = null,
	val dayNumber: Int? = null,
	val stage: RecoveryStage? = null
)

class RecoveryViewModel(
	private val settings: Settings
) : ViewModel() {

	val uiState: StateFlow<RecoveryUiState> = settings.extractionDate
		.map { raw ->
			val date = raw?.let { parseDate(it) }
			val day = daysSinceExtraction(date)
			RecoveryUiState(
				extractionDate = date,
				dayNumber = day,
				stage = day?.let { stageForDay(it) }
			)
		}
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = RecoveryUiState()
		)

	fun saveDate(date: LocalDate) {
		viewModelScope.launch {
			settings.saveStringSettings(EXTRACTION_DATE, date.toString())
		}
	}

	private fun parseDate(raw: String): LocalDate? =
		runCatching { LocalDate.parse(raw) }.getOrNull()
}
