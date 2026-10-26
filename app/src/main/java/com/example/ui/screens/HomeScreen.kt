package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.example.data.model.Post
import com.example.ui.DesiViewModel
import com.example.ui.FeedFilterTab
import com.example.ui.Screen
import com.example.ui.components.PostCard
import com.example.ui.theme.PeacockTeal
import com.example.ui.theme.SaffronOrange

@Composable
fun HomeScreen(
    viewModel: DesiViewModel,
    modifier: Modifier = Modifier
) {
    val posts by viewModel.feedPosts.collectAsState()
    val activeTab by viewModel.feedTab.collectAsState()
    val activeCategory by viewModel.selectedCategory.collectAsState()
    val voiceRooms by viewModel.voiceChatrooms.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val isHindi = currentLang == "hi"

    val categories = listOf("All", "Festivals", "Comedy", "Bollywood", "Cricket", "Music", "Food")

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen")
    ) {
        // Tab Row: For You, Following, Trending
        TabRow(
            selectedTabIndex = activeTab.ordinal,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = SaffronOrange,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab.ordinal]),
                    color = SaffronOrange,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = activeTab == FeedFilterTab.FOR_YOU,
                onClick = { viewModel.setFeedTab(FeedFilterTab.FOR_YOU) },
                text = {
                    Text(
                        text = if (isHindi) "आपके लिए" else "For You",
                        fontWeight = if (activeTab == FeedFilterTab.FOR_YOU) FontWeight.Bold else FontWeight.Normal
                    )
                },
                modifier = Modifier.testTag("tab_for_you")
            )
            Tab(
                selected = activeTab == FeedFilterTab.FOLLOWING,
                onClick = { viewModel.setFeedTab(FeedFilterTab.FOLLOWING) },
                text = {
                    Text(
                        text = if (isHindi) "फॉलोइंग" else "Following",
                        fontWeight = if (activeTab == FeedFilterTab.FOLLOWING) FontWeight.Bold else FontWeight.Normal
                    )
                },
                modifier = Modifier.testTag("tab_following")
            )
            Tab(
                selected = activeTab == FeedFilterTab.TRENDING,
                onClick = { viewModel.setFeedTab(FeedFilterTab.TRENDING) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Trending",
                            tint = SaffronOrange,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = if (isHindi) "ट्रेंडिंग" else "Trending",
                            fontWeight = if (activeTab == FeedFilterTab.TRENDING) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                },
                modifier = Modifier.testTag("tab_trending")
            )
        }

        // Category Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { category ->
                val selected = activeCategory == category
                FilterChip(
                    selected = selected,
                    onClick = { viewModel.setCategory(category) },
                    label = {
                        Text(
                            text = when (category) {
                                "All" -> if (isHindi) "सभी" else "All"
                                "Festivals" -> if (isHindi) "त्योहार 🪔" else "Festivals 🪔"
                                "Comedy" -> if (isHindi) "कॉमेडी 😂" else "Comedy 😂"
                                "Bollywood" -> if (isHindi) "बॉलीवुड 🎬" else "Bollywood 🎬"
                                "Cricket" -> if (isHindi) "क्रिकेट 🏏" else "Cricket 🏏"
                                "Music" -> if (isHindi) "संगीत 🎶" else "Music 🎶"
                                "Food" -> if (isHindi) "देसी स्वाद 🍛" else "Food 🍛"
                                else -> category
                            },
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PeacockTeal.copy(alpha = 0.15f),
                        selectedLabelColor = PeacockTeal
                    ),
                    modifier = Modifier.testTag("category_chip_$category")
                )
            }
        }

        // Live Voice Chatrooms Shortcut Strip (ShareChat-style Audio Adda)
        if (voiceRooms.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFD32F2F),
                        modifier = Modifier.size(8.dp)
                    ) {}
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHindi) "लाइव देसी अड्डा 🎙️" else "Live Voice Clubs 🎙️",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { viewModel.navigateTo(Screen.VOICE_ROOMS) }
                        .padding(4.dp)
                ) {
                    Text(
                        text = if (isHindi) "सभी देखें" else "View all",
                        style = MaterialTheme.typography.labelSmall.copy(color = PeacockTeal, fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "All rooms",
                        tint = PeacockTeal,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(voiceRooms, key = { it.id }) { room ->
                    Card(
                        modifier = Modifier
                            .width(180.dp)
                            .clickable { viewModel.openChatroom(room) }
                            .testTag("home_voice_room_${room.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFFFEBEE)
                                ) {
                                    Text(
                                        text = "LIVE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFFD32F2F),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 9.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Headphones,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "${room.listenersCount}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = room.title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                ),
                                maxLines = 1
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = room.host.avatarUrl,
                                    contentDescription = room.host.displayName,
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = room.host.displayName,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // Feed Post List
        if (posts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🇮🇳",
                        fontSize = 48.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isHindi) "इस श्रेणी में अभी कोई पोस्ट नहीं है" else "No posts found in this feed yet.",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isHindi) "नया पोस्ट बनाने के लिए नीचे '+' बटन दबाएं!" else "Tap the '+' button below to publish the first post!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(posts, key = { it.id }) { post ->
                    PostCard(
                        post = post,
                        isCurrentLanguageHindi = isHindi,
                        onLikeClick = { viewModel.toggleLike(post.id) },
                        onCommentClick = { viewModel.openComments(post) },
                        onShareClick = { viewModel.sharePost(post) },
                        onBookmarkClick = { viewModel.toggleBookmark(post.id) },
                        onReportClick = { reason -> viewModel.reportPost(post, reason) }
                    )
                }
            }
        }
    }
}
