package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.DesiViewModel
import com.example.ui.Screen
import com.example.ui.theme.PeacockTeal
import com.example.ui.theme.SaffronOrange

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreatePostScreen(
    viewModel: DesiViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val isHindi = currentLang == "hi"

    var caption by remember { mutableStateOf("") }
    var selectedMediaUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1514565131-fce0801e5785?w=800") }
    var mediaType by remember { mutableStateOf("image") }
    var selectedLanguage by remember { mutableStateOf(if (isHindi) "hi" else "en") }

    val selectedTags = remember { mutableStateListOf("DesiVibes") }
    val availableTags = listOf("DesiVibes", "Diwali", "Cricket", "Bollywood", "Foodie", "Jugaad", "Shayari")

    val sampleMediaPresets = listOf(
        "https://images.unsplash.com/photo-1514565131-fce0801e5785?w=800" to "Festive Diya 🪔",
        "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=800" to "Desi Sweets 🍯",
        "https://images.unsplash.com/photo-1540747913346-19e32dc3e97e?w=800" to "Cricket Stadium 🏏",
        "https://images.unsplash.com/photo-1526772662000-3f88f10405ff?w=800" to "Indian Spices 🌶️"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("create_post_screen")
    ) {
        // Author row
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = currentUser.avatarUrl,
                contentDescription = currentUser.displayName,
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(SaffronOrange.copy(alpha = 0.2f)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = currentUser.displayName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "@${currentUser.username}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Caption Input Field
        OutlinedTextField(
            value = caption,
            onValueChange = { if (it.length <= 500) caption = it },
            placeholder = {
                Text(if (isHindi) "आज आपके मन में क्या है? अपने विचार और किस्से साझा करें..." else "What's happening? Share thoughts, stories, or jokes...")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .testTag("post_caption_input"),
            shape = RoundedCornerShape(12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "${caption.length}/500",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Hashtags section
        Text(
            text = if (isHindi) "हैशटैग जोड़ें" else "Select Hashtags",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(6.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            availableTags.forEach { tag ->
                val isSelected = selectedTags.contains(tag)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (isSelected) selectedTags.remove(tag)
                        else selectedTags.add(tag)
                    },
                    label = { Text("#$tag") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PeacockTeal.copy(alpha = 0.15f),
                        selectedLabelColor = PeacockTeal
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Media Preset Selection
        Text(
            text = if (isHindi) "मीडिया चुनें" else "Choose Attached Media",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            sampleMediaPresets.forEach { (url, label) ->
                val isSelected = selectedMediaUrl == url
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedMediaUrl = url },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) SaffronOrange.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = url,
                            contentDescription = label,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Selected Media Preview
        if (selectedMediaUrl.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                AsyncImage(
                    model = selectedMediaUrl,
                    contentDescription = "Preview",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                IconButton(
                    onClick = { selectedMediaUrl = "" },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Remove")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Publish Button
        Button(
            onClick = {
                val effectiveCaption = caption.ifBlank { "शुभकामनाएं और देसी वाइब्स! 🇮🇳✨" }
                viewModel.createPost(
                    caption = effectiveCaption,
                    mediaUrl = selectedMediaUrl,
                    mediaType = mediaType,
                    tags = selectedTags.toList(),
                    language = selectedLanguage
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = SaffronOrange),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("publish_post_button")
        ) {
            Text(
                text = if (isHindi) "पोस्ट प्रकाशित करें 🚀" else "Publish Post 🚀",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}
