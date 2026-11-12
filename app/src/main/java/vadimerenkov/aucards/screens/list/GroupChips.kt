package vadimerenkov.aucards.screens.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import vadimerenkov.aucards.R
import vadimerenkov.aucards.data.CardCategory

/**
 * 常用语页顶部的分组选择条。
 *
 * 「全部 / 收藏 / 各个分组」是一行横向可滑的 chip：手机竖屏放得下三四个，
 * 分组再多也不用开抽屉。抽屉留给「新建、改名、删除、排序分组」这些管理动作。
 */
@Composable
internal fun GroupChips(
	categories: List<CardCategory>,
	favouritesOnly: Boolean,
	selectedCategory: CardCategory?,
	onSelectAll: () -> Unit,
	onSelectFavourites: () -> Unit,
	onSelectCategory: (CardCategory) -> Unit,
	onAddGroup: () -> Unit,
	modifier: Modifier = Modifier
) {
	LazyRow(
		contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		modifier = modifier.fillMaxWidth()
	) {
		item(key = "all") {
			GroupChip(
				text = stringResource(R.string.filter_all),
				selected = !favouritesOnly && selectedCategory == null,
				onClick = onSelectAll
			)
		}
		item(key = "favourites") {
			GroupChip(
				text = stringResource(R.string.favourites),
				selected = favouritesOnly,
				onClick = onSelectFavourites
			)
		}
		items(categories, key = { it.id }) { category ->
			GroupChip(
				text = category.name,
				selected = !favouritesOnly && selectedCategory?.id == category.id,
				onClick = { onSelectCategory(category) }
			)
		}
		item(key = "add") {
			// 末尾留一个「+」，新建分组不用再翻抽屉
			FilterChip(
				selected = false,
				onClick = onAddGroup,
				label = {
					Icon(
						imageVector = Icons.Default.Add,
						contentDescription = stringResource(R.string.new_category)
					)
				},
				shape = RoundedCornerShape(50)
			)
		}
	}
}

@Composable
private fun GroupChip(
	text: String,
	selected: Boolean,
	onClick: () -> Unit
) {
	FilterChip(
		selected = selected,
		onClick = onClick,
		label = {
			Text(text = text)
		},
		shape = RoundedCornerShape(50),
		colors = FilterChipDefaults.filterChipColors(
			selectedContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
			selectedLabelColor = MaterialTheme.colorScheme.primaryContainer
		)
	)
}
