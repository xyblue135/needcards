package vadimerenkov.aucards.screens.recovery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vadimerenkov.aucards.data.RecoveryStage
import vadimerenkov.aucards.data.recoveryStages
import vadimerenkov.aucards.data.recoveryWarningSigns
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Composable
fun RecoveryScreen(
	viewModel: RecoveryViewModel,
	modifier: Modifier = Modifier
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	var showDatePicker by remember { mutableStateOf(false) }
	// null 表示跟随当前阶段，-1 表示用户手动全部收起
	var manualExpanded by remember { mutableStateOf<Int?>(null) }
	val currentIndex = recoveryStages.indexOf(uiState.stage)
	val expandedIndex = manualExpanded ?: currentIndex

	if (showDatePicker) {
		ExtractionDatePicker(
			initialDate = uiState.extractionDate,
			onDismiss = { showDatePicker = false },
			onConfirm = {
				viewModel.saveDate(it)
				showDatePicker = false
			}
		)
	}

	LazyColumn(
		modifier = modifier.fillMaxSize(),
		contentPadding = PaddingValues(
			start = 16.dp,
			end = 16.dp,
			top = 16.dp,
			bottom = 96.dp
		),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		item {
			val day = uiState.dayNumber
			if (day == null) {
				EmptyHeaderCard(onPickDate = { showDatePicker = true })
			} else {
				DayHeaderCard(
					dayNumber = day,
					date = uiState.extractionDate,
					onChangeDate = { showDatePicker = true }
				)
			}
		}

		uiState.stage?.let { stage ->
			item {
				StageCard(stage = stage)
			}
		}

		// 前 48 小时冰敷，之后改热敷；没有日期就不猜
		uiState.dayNumber?.let { day ->
			item {
				ColdPackCard(isCold = day <= 2)
			}
		}

		item {
			WarningCard()
		}

		item {
			Text(
				text = "整个恢复过程",
				style = MaterialTheme.typography.titleMedium,
				color = MaterialTheme.colorScheme.onBackground,
				modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
			)
		}

		items(recoveryStages, key = { it.title }) { stage ->
			val index = recoveryStages.indexOf(stage)
			TimelineRow(
				stage = stage,
				isCurrent = index == currentIndex,
				expanded = index == expandedIndex,
				onClick = {
					manualExpanded = if (index == expandedIndex) -1 else index
				}
			)
		}

		item {
			Text(
				text = "以上是常见的术后护理常识，具体以你的医生嘱咐为准。",
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.padding(top = 8.dp)
			)
		}
	}
}

@Composable
private fun DayHeaderCard(
	dayNumber: Int,
	date: LocalDate?,
	onChangeDate: () -> Unit
) {
	Card(
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.primaryContainer
		),
		modifier = Modifier.fillMaxWidth()
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier
				.fillMaxWidth()
				.padding(vertical = 20.dp, horizontal = 16.dp)
		) {
			Text(
				text = "拔牙后",
				style = MaterialTheme.typography.labelLarge,
				color = MaterialTheme.colorScheme.onPrimaryContainer
			)
			Text(
				text = "第 $dayNumber 天",
				fontSize = 44.sp,
				fontWeight = FontWeight.Medium,
				color = MaterialTheme.colorScheme.onPrimaryContainer,
				modifier = Modifier.padding(vertical = 4.dp)
			)
			if (date != null) {
				Text(
					text = "${date.monthValue} 月 ${date.dayOfMonth} 日拔的牙",
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onPrimaryContainer
				)
			}
			TextButton(onClick = onChangeDate) {
				Text(
					text = "改日期",
					color = MaterialTheme.colorScheme.onPrimaryContainer
				)
			}
		}
	}
}

@Composable
private fun EmptyHeaderCard(onPickDate: () -> Unit) {
	Card(
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.secondaryContainer
		),
		modifier = Modifier.fillMaxWidth()
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier
				.fillMaxWidth()
				.padding(vertical = 24.dp, horizontal = 16.dp)
		) {
			Icon(
				imageVector = Icons.Default.DateRange,
				contentDescription = null,
				tint = MaterialTheme.colorScheme.onSecondaryContainer
			)
			Text(
				text = "还没设拔牙日期",
				style = MaterialTheme.typography.titleMedium,
				color = MaterialTheme.colorScheme.onSecondaryContainer,
				modifier = Modifier.padding(top = 8.dp)
			)
			Text(
				text = "设一下日期，就能看到今天是第几天、该注意什么",
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSecondaryContainer,
				textAlign = TextAlign.Center,
				modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
			)
			Button(onClick = onPickDate) {
				Text(text = "选择拔牙日期")
			}
		}
	}
}

@Composable
private fun StageCard(stage: RecoveryStage) {
	Card(
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.surfaceVariant
		),
		modifier = Modifier.fillMaxWidth()
	) {
		Column(modifier = Modifier.padding(16.dp)) {
			Text(
				text = "现在 · ${stage.title}",
				style = MaterialTheme.typography.labelLarge,
				color = MaterialTheme.colorScheme.primary
			)
			Text(
				text = stage.summary,
				style = MaterialTheme.typography.titleMedium,
				color = MaterialTheme.colorScheme.onSurface,
				modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
			)
			stage.tips.forEachIndexed { index, tip ->
				if (index > 0) {
					HorizontalDivider(
						modifier = Modifier.padding(vertical = 8.dp),
						color = MaterialTheme.colorScheme.outlineVariant
					)
				}
				TipRow(tip)
			}
		}
	}
}

@Composable
private fun TipRow(text: String) {
	Row(modifier = Modifier.fillMaxWidth()) {
		Box(
			modifier = Modifier
				.padding(top = 7.dp, end = 8.dp)
				.size(5.dp)
				.background(MaterialTheme.colorScheme.primary, CircleShape)
		)
		Text(
			text = text,
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant
		)
	}
}

@Composable
private fun WarningCard() {
	Card(
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.errorContainer
		),
		modifier = Modifier.fillMaxWidth()
	) {
		Column(modifier = Modifier.padding(16.dp)) {
			Row(verticalAlignment = Alignment.CenterVertically) {
				Icon(
					imageVector = Icons.Default.Warning,
					contentDescription = null,
					tint = MaterialTheme.colorScheme.onErrorContainer
				)
				Spacer(modifier = Modifier.width(8.dp))
				Text(
					text = "这些情况别等，联系医生",
					style = MaterialTheme.typography.titleMedium,
					color = MaterialTheme.colorScheme.onErrorContainer
				)
			}
			Spacer(modifier = Modifier.height(8.dp))
			recoveryWarningSigns.forEach { sign ->
				Text(
					text = "· $sign",
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onErrorContainer,
					modifier = Modifier.padding(vertical = 2.dp)
				)
			}
		}
	}
}

@Composable
private fun TimelineRow(
	stage: RecoveryStage,
	isCurrent: Boolean,
	expanded: Boolean,
	onClick: () -> Unit
) {
	Card(
		colors = CardDefaults.cardColors(
			containerColor = if (isCurrent) {
				MaterialTheme.colorScheme.primaryContainer
			} else {
				MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
			}
		),
		modifier = Modifier
			.fillMaxWidth()
			.clickable(onClick = onClick)
	) {
		Column(modifier = Modifier.padding(16.dp)) {
			Row(verticalAlignment = Alignment.CenterVertically) {
				Text(
					text = stage.dayLabel,
					style = MaterialTheme.typography.labelLarge,
					color = if (isCurrent) {
						MaterialTheme.colorScheme.onPrimaryContainer
					} else {
						MaterialTheme.colorScheme.primary
					},
					modifier = Modifier.width(88.dp)
				)
				Column(modifier = Modifier.weight(1f)) {
					Text(
						text = stage.title,
						style = MaterialTheme.typography.titleSmall,
						color = MaterialTheme.colorScheme.onSurface
					)
					Text(
						text = stage.summary,
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
				}
			}
			if (expanded) {
				Spacer(modifier = Modifier.height(10.dp))
				stage.tips.forEach { tip ->
					TipRow(tip)
					Spacer(modifier = Modifier.height(6.dp))
				}
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExtractionDatePicker(
	initialDate: LocalDate?,
	onDismiss: () -> Unit,
	onConfirm: (LocalDate) -> Unit
) {
	val state = rememberDatePickerState(
		initialSelectedDateMillis = initialDate
			?.atStartOfDay(ZoneOffset.UTC)
			?.toInstant()
			?.toEpochMilli()
	)
	DatePickerDialog(
		onDismissRequest = onDismiss,
		confirmButton = {
			TextButton(
				onClick = {
					val millis = state.selectedDateMillis
					if (millis != null) {
						// DatePicker 用 UTC 零点表示日期，按 UTC 读回来才不会差一天
						onConfirm(
							Instant.ofEpochMilli(millis)
								.atZone(ZoneOffset.UTC)
								.toLocalDate()
						)
					}
				}
			) {
				Text(text = "确定")
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text(text = "取消")
			}
		}
	) {
		DatePicker(state = state)
	}
}
