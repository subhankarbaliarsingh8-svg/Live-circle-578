package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.PeacockTeal
import com.example.ui.theme.SaffronOrange

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateRoomDialog(
    isHindi: Boolean,
    onDismiss: () -> Unit,
    onCreate: (title: String, category: String, language: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Gupshup") }
    var selectedLang by remember { mutableStateOf(if (isHindi) "hi" else "en") }

    val categories = listOf(
        "Gupshup" to if (isHindi) "गपशप ☕" else "Gupshup ☕",
        "Music" to if (isHindi) "संगीत 🎤" else "Music 🎤",
        "Cricket" to if (isHindi) "क्रिकेट 🏏" else "Cricket 🏏",
        "Comedy" to if (isHindi) "कॉमेडी 😂" else "Comedy 😂",
        "Shayari" to if (isHindi) "शायरी 📜" else "Shayari 📜",
        "Devotional" to if (isHindi) "भक्ति 🙏" else "Bhakti 🙏"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = null,
                    tint = SaffronOrange,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isHindi) "नया वॉइस चैट रूम बनाएं 🎙️" else "Start Live Voice Room 🎙️",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (isHindi) "रूम का नाम (शीर्षक)" else "Room Title",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = {
                        Text(if (isHindi) "उदा. बॉलीवुड अंताक्षरी और मस्ती..." else "e.g., Bollywood Antakshari Live...")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_room_title_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (isHindi) "श्रेणी चुनें" else "Select Category",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    categories.forEach { (catKey, catLabel) ->
                        val isSelected = selectedCategory == catKey
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = catKey },
                            label = { Text(catLabel) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PeacockTeal.copy(alpha = 0.15f),
                                selectedLabelColor = PeacockTeal
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val effectiveTitle = title.ifBlank {
                        if (isHindi) "देसी महफ़िल और लाइव गपशप ☕" else "Desi Live Voice Adda ☕"
                    }
                    onCreate(effectiveTitle, selectedCategory, selectedLang)
                },
                colors = ButtonDefaults.buttonColors(containerColor = SaffronOrange),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("submit_create_room_button")
            ) {
                Text(if (isHindi) "लाइव शुरू करें 🔴" else "Go Live Now 🔴")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(if (isHindi) "रद्द करें" else "Cancel")
            }
        }
    )
}
