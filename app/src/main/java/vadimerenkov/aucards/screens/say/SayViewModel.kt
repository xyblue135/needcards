package vadimerenkov.aucards.screens.say

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import vadimerenkov.aucards.data.RecentPhrases

class SayViewModel(
	private val recentPhrases: RecentPhrases
) : ViewModel() {

	val recent = recentPhrases.items
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000L),
			initialValue = emptyList()
		)

	fun remember(text: String) {
		viewModelScope.launch {
			recentPhrases.add(text)
		}
	}
}
