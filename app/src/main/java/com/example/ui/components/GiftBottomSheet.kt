package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VirtualGift
import com.example.ui.theme.MarigoldGold
import com.example.ui.theme.SaffronOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GiftBottomSheet(
    isHindi: Boolean,
    onDismiss: () -> Unit,
    onSendGift: (VirtualGift) -> Unit,
    sheetState: SheetState
) {
    val gifts = listOf(
        VirtualGift("g1", "दीया (Diya)", "🪔", 10),
        VirtualGift("g2", "गुलाब (Rose)", "🌹", 20),
        VirtualGift("g3", "शाही ताज (Crown)", "👑", 100),
        VirtualGift("g4", "देसी चाय (Chai)", "☕", 15),
        VirtualGift("g5", "ताली (Applause)", "👏", 5),
        VirtualGift("g6", "क्रिकेट बैट (Bat)", "🏏", 50),
        VirtualGift("g7", "मिठाई (Sweets)", "🍯", 25),
        VirtualGift("g8", "दिल (Heart)", "❤️", 10)
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isHindi) "वर्चुअल उपहार भेजें 🎁" else "Send Desi Gift 🎁",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Text(
                text = if (isHindi) "होस्ट और मंच के वक्ताओं का उत्साह बढ़ाएं!" else "Cheer for the host and speakers on stage!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                items(gifts, key = { it.id }) { gift ->
                    Card(
                        modifier = Modifier
                            .clickable {
                                onSendGift(gift)
                                onDismiss()
                            }
                            .testTag("gift_${gift.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = gift.emoji,
                                fontSize = 32.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = gift.name,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                                maxLines = 1
                            )
                            Text(
                                text = "🪙 ${gift.desiCoins}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = MarigoldGold, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
    }
}
