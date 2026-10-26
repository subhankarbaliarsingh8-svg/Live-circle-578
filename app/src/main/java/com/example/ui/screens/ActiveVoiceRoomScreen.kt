package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.RoomChatMessage
import com.example.data.model.RoomSpeaker
import com.example.data.model.VoiceChatroom
import com.example.ui.DesiViewModel
import com.example.ui.components.GiftBottomSheet
import com.example.ui.theme.MarigoldGold
import com.example.ui.theme.PeacockTeal
import com.example.ui.theme.SaffronOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveVoiceRoomScreen(
    room: VoiceChatroom,
    viewModel: DesiViewModel,
    modifier: Modifier = Modifier
) {
    val currentLang by viewModel.currentLanguage.collectAsState()
    val isHindi = currentLang == "hi"

    val isMuted by viewModel.isUserMutedInRoom.collectAsState()
    val userMicSeat by viewModel.userMicSeat.collectAsState()
    val messagesMap by viewModel.roomMessages.collectAsState()
    val messages = messagesMap[room.id] ?: emptyList()
    val floatingGift by viewModel.floatingGift.collectAsState()

    var showGiftSheet by remember { mutableStateOf(false) }
    var inRoomText by remember { mutableStateOf("") }
    val giftSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Audio Permission Launcher
    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.toggleRoomMute()
        } else {
            viewModel.showFeedback("Microphone permission required to speak on stage")
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1B1822),
                        Color(0xFF14131A),
                        Color(0xFF0C0A10)
                    )
                )
            )
            .testTag("active_voice_room_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Room Top Header Bar
            RoomTopHeader(
                room = room,
                isHindi = isHindi,
                onLeaveClick = { viewModel.leaveActiveRoom() }
            )

            // The Stage: 8 Speaker Mic Seats (2 rows of 4)
            TheStageGrid(
                speakers = room.speakers,
                userMicSeat = userMicSeat,
                onSeatClick = { seatIdx ->
                    if (userMicSeat == seatIdx) {
                        viewModel.leaveMicSeat()
                    } else if (userMicSeat == null) {
                        viewModel.takeMicSeat(seatIdx)
                    }
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Audience Count & Listeners Avatars Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Headphones,
                    contentDescription = null,
                    tint = PeacockTeal,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isHindi) "श्रोता (${room.listenersCount} लोग सुन रहे हैं)" else "Audience (${room.listenersCount} listening)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = PeacockTeal
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // In-Room Live Chat Messages Stream
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    InRoomMessagePill(msg = msg)
                }
            }

            // Bottom In-Room Action Control Bar
            RoomBottomControlBar(
                isHindi = isHindi,
                isMuted = isMuted,
                isOnStage = userMicSeat != null,
                inRoomText = inRoomText,
                onTextChanged = { inRoomText = it },
                onSendText = {
                    if (inRoomText.isNotBlank()) {
                        viewModel.sendRoomMessage(inRoomText)
                        inRoomText = ""
                    }
                },
                onToggleMic = {
                    if (userMicSeat != null) {
                        if (isMuted) {
                            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        } else {
                            viewModel.toggleRoomMute()
                        }
                    } else {
                        viewModel.showFeedback("Tap an empty mic seat above to join the stage first!")
                    }
                },
                onToggleStageSeat = {
                    if (userMicSeat != null) {
                        viewModel.leaveMicSeat()
                    } else {
                        val firstAvailable = (0..7).firstOrNull { idx -> room.speakers.none { it.seatIndex == idx } } ?: 0
                        viewModel.takeMicSeat(firstAvailable)
                    }
                },
                onOpenGifts = { showGiftSheet = true }
            )
        }

        // Floating Animated Gift Celebration Overlay
        AnimatedVisibility(
            visible = floatingGift != null,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            floatingGift?.let { emoji ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .padding(24.dp)
                ) {
                    Text(
                        text = emoji,
                        fontSize = 72.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "उपहार भेजा गया! 🎁",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MarigoldGold
                        )
                    )
                }
            }
        }
    }

    // Gift Tray Bottom Sheet
    if (showGiftSheet) {
        GiftBottomSheet(
            isHindi = isHindi,
            onDismiss = { showGiftSheet = false },
            onSendGift = { gift -> viewModel.sendVirtualGift(gift) },
            sheetState = giftSheetState
        )
    }
}

@Composable
fun RoomTopHeader(
    room: VoiceChatroom,
    isHindi: Boolean,
    onLeaveClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onLeaveClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = room.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 15.sp
                ),
                maxLines = 1
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "👑 ${room.host.displayName}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MarigoldGold,
                        fontSize = 11.5.sp
                    )
                )
                Text(
                    text = " • ${room.category}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Leave Room Button
        Button(
            onClick = onLeaveClick,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            modifier = Modifier.height(30.dp)
        ) {
            Text(
                text = if (isHindi) "छोड़ें ✕" else "Leave ✕",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }
    }
}

@Composable
fun TheStageGrid(
    speakers: List<RoomSpeaker>,
    userMicSeat: Int?,
    onSeatClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF221F2B))
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                (0..3).forEach { seatIndex ->
                    val speaker = speakers.firstOrNull { it.seatIndex == seatIndex }
                    MicSeatItem(
                        seatIndex = seatIndex,
                        speaker = speaker,
                        isUserHere = userMicSeat == seatIndex,
                        onClick = { onSeatClick(seatIndex) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                (4..7).forEach { seatIndex ->
                    val speaker = speakers.firstOrNull { it.seatIndex == seatIndex }
                    MicSeatItem(
                        seatIndex = seatIndex,
                        speaker = speaker,
                        isUserHere = userMicSeat == seatIndex,
                        onClick = { onSeatClick(seatIndex) }
                    )
                }
            }
        }
    }
}

@Composable
fun MicSeatItem(
    seatIndex: Int,
    speaker: RoomSpeaker?,
    isUserHere: Boolean,
    onClick: () -> Unit
) {
    val isSpeaking = speaker?.isSpeaking == true

    val infiniteTransition = rememberInfiniteTransition(label = "speakerRipple")
    val rippleScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ripple"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .testTag("mic_seat_$seatIndex")
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .scale(if (isSpeaking) rippleScale else 1f),
            contentAlignment = Alignment.Center
        ) {
            if (speaker != null) {
                // Occupied seat
                AsyncImage(
                    model = speaker.user.avatarUrl,
                    contentDescription = speaker.user.displayName,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .border(
                            width = if (isSpeaking) 2.dp else 1.dp,
                            color = if (isSpeaking) SaffronOrange else Color.White.copy(alpha = 0.3f),
                            shape = CircleShape
                        ),
                    contentScale = ContentScale.Crop
                )

                // Host crown badge
                if (speaker.isHost) {
                    Text(
                        text = "👑",
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.TopEnd)
                    )
                }

                // Mute mic icon badge
                Surface(
                    shape = CircleShape,
                    color = if (speaker.isMuted) Color(0xFFD32F2F) else SaffronOrange,
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (speaker.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            } else {
                // Empty seat placeholder
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.08f),
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Take seat",
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = speaker?.user?.displayName?.take(8) ?: "#${seatIndex + 1}",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.5.sp,
                color = if (speaker != null) Color.White else Color.White.copy(alpha = 0.5f),
                fontWeight = if (isSpeaking) FontWeight.Bold else FontWeight.Normal
            ),
            maxLines = 1
        )
    }
}

@Composable
fun InRoomMessagePill(msg: RoomChatMessage) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (msg.isSystem) PeacockTeal.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.08f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (msg.giftEmoji != null) {
                Text(text = msg.giftEmoji, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = msg.senderName,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (msg.isSystem) PeacockTeal else SaffronOrange
                )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = msg.text,
                style = MaterialTheme.typography.bodySmall.copy(color = Color.White),
                maxLines = 2
            )
        }
    }
}

@Composable
fun RoomBottomControlBar(
    isHindi: Boolean,
    isMuted: Boolean,
    isOnStage: Boolean,
    inRoomText: String,
    onTextChanged: (String) -> Unit,
    onSendText: () -> Unit,
    onToggleMic: () -> Unit,
    onToggleStageSeat: () -> Unit,
    onOpenGifts: () -> Unit
) {
    Surface(
        color = Color(0xFF191620),
        tonalElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Text Input
            OutlinedTextField(
                value = inRoomText,
                onValueChange = onTextChanged,
                placeholder = {
                    Text(
                        if (isHindi) "बातचीत करें..." else "Chat in room...",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("in_room_chat_input"),
                shape = RoundedCornerShape(22.dp),
                singleLine = true
            )

            if (inRoomText.isNotBlank()) {
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = onSendText,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SaffronOrange)
                        .testTag("in_room_send_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Stage Seat / Raise Hand Button
            IconButton(
                onClick = onToggleStageSeat,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isOnStage) SaffronOrange else Color.White.copy(alpha = 0.12f))
                    .testTag("stage_mic_seat_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.PanTool,
                    contentDescription = "Stage Seat",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Mic Mute / Unmute Button
            IconButton(
                onClick = onToggleMic,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (!isMuted && isOnStage) SaffronOrange else Color.White.copy(alpha = 0.12f))
                    .testTag("room_mic_toggle_btn")
            ) {
                Icon(
                    imageVector = if (!isMuted && isOnStage) Icons.Default.Mic else Icons.Default.MicOff,
                    contentDescription = "Mic Toggle",
                    tint = if (!isMuted && isOnStage) Color.White else Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Gift Tray Button
            IconButton(
                onClick = onOpenGifts,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MarigoldGold)
                    .testTag("gift_tray_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.CardGiftcard,
                    contentDescription = "Gifts",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
