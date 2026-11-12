package vadimerenkov.aucards.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import vadimerenkov.aucards.data.Aucard
import vadimerenkov.aucards.data.AucardDao
import vadimerenkov.aucards.data.CardCategory

class FakeDao : AucardDao {
	private val list = mutableListOf<Aucard>()
	private val categories = mutableListOf<CardCategory>()

	override suspend fun saveAucard(aucard: Aucard) {
		list.add(aucard)
	}

	override suspend fun saveAllCards(cards: List<Aucard>) {
		list.addAll(cards)
	}

	override fun getAllCards(): Flow<List<Aucard>> {
		return flowOf(list)
	}

	override fun getAucardByID(id: Int): Flow<Aucard> {
		val card = list.find { it ->
			it.id == id
		}
		if (card != null) {
			return flowOf(card)
		}
		else {
			throw NoSuchElementException()
		}
	}

	override suspend fun deleteById(id: Int) {
		val card = list.find { it ->
			it.id == id
		}
		list.remove(card)
	}

	override fun getFavouriteCards(): Flow<List<Aucard>> {
		return flowOf(list.filter { it.isFavourite })
	}

	override suspend fun saveCategory(category: CardCategory) {
		categories.add(category)
	}

	override suspend fun saveAllCategories(categories: List<CardCategory>) {
		this.categories.addAll(categories)
	}

	override suspend fun deleteCategory(category: CardCategory) {
		categories.remove(category)
	}

	override fun getAllCategories(): Flow<List<CardCategory>> {
		return flowOf(categories)
	}
}
