package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Mic
import com.example.data.remote.SupabaseStatus
import com.example.ui.DesiViewModel
import com.example.ui.Screen
import com.example.ui.components.CommentBottomSheet
import com.example.ui.components.CreateRoomDialog
import com.example.ui.components.EditProfileDialog
import com.example.ui.components.SupabaseSetupDialog
import com.example.ui.screens.ActiveVoiceRoomScreen
import com.example.ui.screens.ChatDetailScreen
import com.example.ui.screens.ClipsScreen
import com.example.ui.screens.CreatePostScreen
import com.example.ui.screens.DiscoverScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.VoiceChatroomsScreen
import com.example.ui.theme.DesiConnectTheme
import com.example.ui.theme.MarigoldGold
import com.example.ui.theme.PeacockTeal
import com.example.ui.theme.SaffronLight
import com.example.ui.theme.SaffronOrange

class MainActivity : ComponentActivity() {

    private val viewModel: DesiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DesiConnectTheme {
                DesiConnectApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesiConnectApp(viewModel: DesiViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val activeConversation by viewModel.activeConversation.collectAsState()
    val activeCommentPost by viewModel.activeCommentPost.collectAsState()
    val showSupabaseSetup by viewModel.showSupabaseSetupDialog.collectAsState()
    val showEditProfile by viewModel.showEditProfileDialog.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val supabaseState by viewModel.supabaseState.collectAsState()
    val snackbarMsg by viewModel.snackbarMessage.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val isHindi = currentLang == "hi"

    val activeChatroom by viewModel.activeChatroom.collectAsState()
    val showCreateRoom by viewModel.showCreateRoomDialog.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Handle system back navigation
    BackHandler(enabled = currentScreen != Screen.HOME) {
        when (currentScreen) {
            Screen.ACTIVE_VOICE_ROOM -> viewModel.leaveActiveRoom()
            Screen.VOICE_ROOMS -> viewModel.navigateTo(Screen.HOME)
            Screen.CHAT_DETAIL -> viewModel.navigateTo(Screen.NOTIFICATIONS)
            else -> viewModel.navigateTo(Screen.HOME)
        }
    }

    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedback()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            if (currentScreen != Screen.CHAT_DETAIL && currentScreen != Screen.ACTIVE_VOICE_ROOM) {
                DesiTopBar(
                    isHindi = isHindi,
                    supabaseStatus = supabaseState.status,
                    onLanguageToggle = { viewModel.toggleLanguage() },
                    onVoiceAddaClick = { viewModel.navigateTo(Screen.VOICE_ROOMS) },
                    onSupabaseClick = { viewModel.setShowSupabaseSetup(true) },
                    onMessagesClick = { viewModel.navigateTo(Screen.NOTIFICATIONS) }
                )
            }
        },
        bottomBar = {
            if (currentScreen != Screen.CHAT_DETAIL && currentScreen != Screen.ACTIVE_VOICE_ROOM) {
                DesiBottomNavigation(
                    currentScreen = currentScreen,
                    isHindi = isHindi,
                    onNavigate = { viewModel.navigateTo(it) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.HOME -> HomeScreen(viewModel = viewModel)
                Screen.DISCOVER -> DiscoverScreen(viewModel = viewModel)
                Screen.VOICE_ROOMS -> VoiceChatroomsScreen(viewModel = viewModel)
                Screen.CREATE -> CreatePostScreen(viewModel = viewModel)
                Screen.CLIPS -> ClipsScreen(viewModel = viewModel)
                Screen.NOTIFICATIONS -> NotificationsScreen(viewModel = viewModel)
                Screen.PROFILE -> ProfileScreen(viewModel = viewModel)
                Screen.CHAT_DETAIL -> {
                    activeConversation?.let { conv ->
                        ChatDetailScreen(conversation = conv, viewModel = viewModel)
                    } ?: run {
                        HomeScreen(viewModel = viewModel)
                    }
                }
                Screen.ACTIVE_VOICE_ROOM -> {
                    activeChatroom?.let { room ->
                        ActiveVoiceRoomScreen(room = room, viewModel = viewModel)
                    } ?: run {
                        VoiceChatroomsScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }

    // Comment Bottom Sheet
    activeCommentPost?.let { post ->
        val comments = viewModel.getCommentsForPost(post.id)
        CommentBottomSheet(
            post = post,
            comments = comments,
            isHindi = isHindi,
            onDismiss = { viewModel.closeComments() },
            onAddComment = { text -> viewModel.addComment(post.id, text) },
            onDeleteComment = { commentId -> viewModel.deleteComment(post.id, commentId) },
            sheetState = sheetState
        )
    }

    // Supabase Setup Dialog
    if (showSupabaseSetup) {
        SupabaseSetupDialog(
            connectionState = supabaseState,
            onDismiss = { viewModel.setShowSupabaseSetup(false) },
            onSaveAndConnect = { url, key -> viewModel.configureSupabase(url, key) }
        )
    }

    // Edit Profile Dialog
    if (showEditProfile) {
        EditProfileDialog(
            user = currentUser,
            isHindi = isHindi,
            onDismiss = { viewModel.setShowEditProfile(false) },
            onSave = { name, bio, avatar -> viewModel.updateProfile(name, bio, avatar) }
        )
    }

    // Create Voice Room Dialog
    if (showCreateRoom) {
        CreateRoomDialog(
            isHindi = isHindi,
            onDismiss = { viewModel.setShowCreateRoomDialog(false) },
            onCreate = { title, cat, lang ->
                viewModel.createVoiceChatroom(title, cat, lang)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesiTopBar(
    isHindi: Boolean,
    supabaseStatus: SupabaseStatus,
    onLanguageToggle: () -> Unit,
    onVoiceAddaClick: () -> Unit,
    onSupabaseClick: () -> Unit,
    onMessagesClick: () -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Stylized Diya / Connect Logo
                Surface(
                    shape = CircleShape,
                    color = SaffronOrange,
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "🇮🇳",
                            fontSize = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "DesiConnect",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = SaffronOrange,
                            fontSize = 18.sp
                        )
                    )
                    Text(
                        text = if (isHindi) "अपना देसी सोशल नेटवर्क" else "Bharat Ka Social Media",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        },
        actions = {
            // Voice Adda Direct Shortcut Button
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = PeacockTeal.copy(alpha = 0.15f),
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onVoiceAddaClick() }
                    .padding(end = 4.dp)
                    .testTag("topbar_voice_adda_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Adda",
                        tint = PeacockTeal,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isHindi) "अड्डा 🔴" else "Adda 🔴",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PeacockTeal
                        )
                    )
                }
            }

            // Language Switch Badge Button
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SaffronOrange.copy(alpha = 0.12f),
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onLanguageToggle() }
                    .padding(end = 4.dp)
                    .testTag("language_toggle_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHindi) "हिंदी 🇮🇳" else "EN 🌐",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SaffronOrange
                        )
                    )
                }
            }

            // Supabase Cloud Status Icon
            IconButton(
                onClick = onSupabaseClick,
                modifier = Modifier.testTag("topbar_supabase_button")
            ) {
                Box {
                    Icon(
                        imageVector = if (supabaseStatus == SupabaseStatus.CONNECTED) Icons.Default.CloudDone else Icons.Default.Cloud,
                        contentDescription = "Supabase Status",
                        tint = if (supabaseStatus == SupabaseStatus.CONNECTED) PeacockTeal else SaffronOrange
                    )
                    // Status dot
                    Surface(
                        shape = CircleShape,
                        color = if (supabaseStatus == SupabaseStatus.CONNECTED) PeacockTeal else MarigoldGold,
                        modifier = Modifier
                            .size(7.dp)
                            .align(Alignment.TopEnd)
                    ) {}
                }
            }

            // Messages / Notification Icon with Badge
            IconButton(
                onClick = onMessagesClick,
                modifier = Modifier.testTag("topbar_messages_button")
            ) {
                BadgedBox(
                    badge = {
                        Badge(containerColor = SaffronOrange) {
                            Text("1", color = Color.White)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Mail,
                        contentDescription = "Messages"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun DesiBottomNavigation(
    currentScreen: Screen,
    isHindi: Boolean,
    onNavigate: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        // 1. Home Tab
        NavigationBarItem(
            selected = currentScreen == Screen.HOME,
            onClick = { onNavigate(Screen.HOME) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == Screen.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text(if (isHindi) "होम" else "Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SaffronOrange,
                selectedTextColor = SaffronOrange,
                indicatorColor = SaffronOrange.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("nav_home")
        )

        // 2. Discover Tab
        NavigationBarItem(
            selected = currentScreen == Screen.DISCOVER,
            onClick = { onNavigate(Screen.DISCOVER) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == Screen.DISCOVER) Icons.Filled.Explore else Icons.Outlined.Explore,
                    contentDescription = "Discover"
                )
            },
            label = { Text(if (isHindi) "खोजें" else "Discover") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SaffronOrange,
                selectedTextColor = SaffronOrange,
                indicatorColor = SaffronOrange.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("nav_discover")
        )

        // 3. Center Create Post (+) Button
        NavigationBarItem(
            selected = currentScreen == Screen.CREATE,
            onClick = { onNavigate(Screen.CREATE) },
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(SaffronOrange, MarigoldGold)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create Post",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            label = { Text(if (isHindi) "नया" else "Create") },
            colors = NavigationBarItemDefaults.colors(
                selectedTextColor = SaffronOrange
            ),
            modifier = Modifier.testTag("nav_create")
        )

        // 4. Clips / Moj Short Videos Tab
        NavigationBarItem(
            selected = currentScreen == Screen.CLIPS,
            onClick = { onNavigate(Screen.CLIPS) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == Screen.CLIPS) Icons.Filled.VideoLibrary else Icons.Outlined.VideoLibrary,
                    contentDescription = "Clips"
                )
            },
            label = { Text(if (isHindi) "मौज" else "Clips") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SaffronOrange,
                selectedTextColor = SaffronOrange,
                indicatorColor = SaffronOrange.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("nav_clips")
        )

        // 5. Profile Tab
        NavigationBarItem(
            selected = currentScreen == Screen.PROFILE,
            onClick = { onNavigate(Screen.PROFILE) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == Screen.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Profile"
                )
            },
            label = { Text(if (isHindi) "प्रोफ़ाइल" else "Profile") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SaffronOrange,
                selectedTextColor = SaffronOrange,
                indicatorColor = SaffronOrange.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("nav_profile")
        )
    }
}
