package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Comment
import com.example.data.model.Conversation
import com.example.data.model.Post
import com.example.data.model.RoomChatMessage
import com.example.data.model.ShortVideo
import com.example.data.model.TrendingTag
import com.example.data.model.UserProfile
import com.example.data.model.VirtualGift
import com.example.data.model.VoiceChatroom
import com.example.data.remote.SupabaseConnectionState
import com.example.data.repository.DesiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Screen {
    HOME,
    DISCOVER,
    VOICE_ROOMS,
    CREATE,
    CLIPS,
    NOTIFICATIONS,
    PROFILE,
    CHAT_DETAIL,
    ACTIVE_VOICE_ROOM
}

enum class FeedFilterTab {
    FOR_YOU,
    FOLLOWING,
    TRENDING
}

class DesiViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DesiRepository(application)

    // Current screen navigation
    private val _currentScreen = MutableStateFlow(Screen.HOME)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Active conversation for CHAT_DETAIL screen
    private val _activeConversation = MutableStateFlow<Conversation?>(null)
    val activeConversation: StateFlow<Conversation?> = _activeConversation.asStateFlow()

    // Home feed tab
    private val _feedTab = MutableStateFlow(FeedFilterTab.FOR_YOU)
    val feedTab: StateFlow<FeedFilterTab> = _feedTab.asStateFlow()

    // Category filter for Home
    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Search query for Discover screen
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // UI Feedback Message
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // Active Comment Post
    private val _activeCommentPost = MutableStateFlow<Post?>(null)
    val activeCommentPost: StateFlow<Post?> = _activeCommentPost.asStateFlow()

    // Dialog toggles
    private val _showSupabaseSetupDialog = MutableStateFlow(false)
    val showSupabaseSetupDialog: StateFlow<Boolean> = _showSupabaseSetupDialog.asStateFlow()

    private val _showEditProfileDialog = MutableStateFlow(false)
    val showEditProfileDialog: StateFlow<Boolean> = _showEditProfileDialog.asStateFlow()

    // Data from repository
    val currentUser: StateFlow<UserProfile> = repository.currentUser
    val currentLanguage: StateFlow<String> = repository.currentLanguage
    val supabaseState: StateFlow<SupabaseConnectionState> = repository.supabaseState
    val shortVideos: StateFlow<List<ShortVideo>> = repository.shortVideos
    val trendingTags: StateFlow<List<TrendingTag>> = repository.trendingTags
    val notifications = repository.notifications
    val conversations = repository.conversations
    val messages = repository.messages
    val suggestedCreators = repository.suggestedCreators

    // Voice Chatrooms State
    val voiceChatrooms: StateFlow<List<VoiceChatroom>> = repository.voiceChatrooms
    val activeChatroom: StateFlow<VoiceChatroom?> = repository.activeChatroom
    val roomMessages: StateFlow<Map<String, List<RoomChatMessage>>> = repository.roomMessages
    val isUserMutedInRoom: StateFlow<Boolean> = repository.isUserMutedInRoom
    val userMicSeat: StateFlow<Int?> = repository.userMicSeat

    private val _showCreateRoomDialog = MutableStateFlow(false)
    val showCreateRoomDialog: StateFlow<Boolean> = _showCreateRoomDialog.asStateFlow()

    private val _floatingGift = MutableStateFlow<String?>(null)
    val floatingGift: StateFlow<String?> = _floatingGift.asStateFlow()

    // Filtered Feed based on tab and category
    val feedPosts: StateFlow<List<Post>> = combine(
        repository.posts,
        _feedTab,
        _selectedCategory
    ) { posts, tab, category ->
        var list = when (tab) {
            FeedFilterTab.FOR_YOU -> posts
            FeedFilterTab.FOLLOWING -> posts.filter { it.likesCount > 2000 }
            FeedFilterTab.TRENDING -> posts.sortedByDescending { it.likesCount + it.commentsCount }
        }
        if (category != "All") {
            list = list.filter { post ->
                post.tags.any { it.contains(category, ignoreCase = true) } ||
                post.caption.contains(category, ignoreCase = true)
            }
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Search Results
    val searchResults: StateFlow<List<Post>> = combine(
        repository.posts,
        _searchQuery
    ) { posts, query ->
        if (query.isBlank()) posts
        else posts.filter {
            it.caption.contains(query, ignoreCase = true) ||
            it.authorName.contains(query, ignoreCase = true) ||
            it.tags.any { tag -> tag.contains(query, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Comments for active post
    fun getCommentsForPost(postId: String): List<Comment> {
        return repository.comments.value[postId] ?: emptyList()
    }

    // Navigation actions
    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun openChat(conversation: Conversation) {
        _activeConversation.value = conversation
        _currentScreen.value = Screen.CHAT_DETAIL
    }

    fun setFeedTab(tab: FeedFilterTab) {
        _feedTab.value = tab
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleLanguage() {
        val next = if (currentLanguage.value == "hi") "en" else "hi"
        repository.setLanguage(next)
        showFeedback(if (next == "hi") "भाषा बदलकर हिंदी कर दी गई है" else "Language changed to English")
    }

    fun toggleLike(postId: String) {
        repository.toggleLikePost(postId)
    }

    fun toggleBookmark(postId: String) {
        repository.toggleBookmarkPost(postId)
        showFeedback("Saved to bookmarks!")
    }

    fun openComments(post: Post) {
        _activeCommentPost.value = post
    }

    fun closeComments() {
        _activeCommentPost.value = null
    }

    fun addComment(postId: String, text: String) {
        repository.addComment(postId, text)
        showFeedback("Comment posted! 💬")
    }

    fun deleteComment(postId: String, commentId: String) {
        repository.deleteComment(postId, commentId)
        showFeedback("Comment deleted")
    }

    fun sharePost(post: Post) {
        showFeedback("Link copied to clipboard! (desiconnect.in/p/${post.id})")
    }

    fun reportPost(post: Post, reason: String) {
        repository.reportItem("post", post.id, reason)
        showFeedback("Post reported to moderation team. Thank you.")
    }

    fun blockUser(userId: String) {
        repository.blockUser(userId)
        showFeedback("User blocked.")
    }

    fun createPost(caption: String, mediaUrl: String, mediaType: String, tags: List<String>, language: String) {
        repository.createPost(caption, mediaUrl, mediaType, tags, language)
        _currentScreen.value = Screen.HOME
        showFeedback("Post published successfully! 🎉")
    }

    fun toggleFollowCreator(userId: String) {
        repository.toggleFollowCreator(userId)
    }

    fun toggleLikeShortVideo(videoId: String) {
        repository.toggleLikeShortVideo(videoId)
    }

    fun sendMessage(conversationId: String, text: String) {
        repository.sendMessage(conversationId, text)
    }

    fun markAllNotificationsRead() {
        repository.markAllNotificationsRead()
        showFeedback("All notifications marked as read")
    }

    fun markNotificationRead(id: String) {
        repository.markNotificationRead(id)
    }

    fun setShowSupabaseSetup(show: Boolean) {
        _showSupabaseSetupDialog.value = show
    }

    fun setShowEditProfile(show: Boolean) {
        _showEditProfileDialog.value = show
    }

    fun updateProfile(displayName: String, bio: String, avatarUrl: String) {
        repository.updateProfile(displayName, bio, avatarUrl)
        setShowEditProfile(false)
        showFeedback("Profile updated successfully!")
    }

    fun configureSupabase(url: String, key: String) {
        viewModelScope.launch {
            val result = repository.configureSupabase(url, key)
            if (result.isSuccess) {
                showFeedback("Supabase connected successfully! 🚀")
                _showSupabaseSetupDialog.value = false
            } else {
                showFeedback("Connection failed: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun showFeedback(msg: String) {
        _snackbarMessage.value = msg
    }

    fun clearFeedback() {
        _snackbarMessage.value = null
    }

    // Voice Chatroom Functions
    fun openChatroom(room: VoiceChatroom) {
        repository.joinChatroom(room)
        _currentScreen.value = Screen.ACTIVE_VOICE_ROOM
        showFeedback("Joined '${room.title}' 🎙️")
    }

    fun leaveActiveRoom() {
        repository.leaveChatroom()
        _currentScreen.value = Screen.VOICE_ROOMS
        showFeedback("Left voice room")
    }

    fun takeMicSeat(seatIndex: Int) {
        repository.takeMicSeat(seatIndex)
        showFeedback("You took mic seat #${seatIndex + 1}! You are on stage 🎙️")
    }

    fun leaveMicSeat() {
        repository.leaveMicSeat()
        showFeedback("Stepped down to audience")
    }

    fun toggleRoomMute() {
        repository.toggleMute()
        val muted = repository.isUserMutedInRoom.value
        showFeedback(if (muted) "Microphone muted 🔇" else "Microphone unmuted 🎙️ You are speaking live!")
    }

    fun sendRoomMessage(text: String) {
        val room = repository.activeChatroom.value ?: return
        repository.sendRoomMessage(room.id, text)
    }

    fun sendVirtualGift(gift: VirtualGift) {
        val room = repository.activeChatroom.value ?: return
        repository.sendRoomMessage(
            roomId = room.id,
            text = "sent a ${gift.name} ${gift.emoji}!",
            giftEmoji = gift.emoji
        )
        _floatingGift.value = gift.emoji
        viewModelScope.launch {
            kotlinx.coroutines.delay(2200)
            _floatingGift.value = null
        }
        showFeedback("Sent ${gift.name} ${gift.emoji} to the stage!")
    }

    fun setShowCreateRoomDialog(show: Boolean) {
        _showCreateRoomDialog.value = show
    }

    fun createVoiceChatroom(title: String, category: String, language: String) {
        val newRoom = repository.createVoiceChatroom(title, category, language)
        _showCreateRoomDialog.value = false
        _currentScreen.value = Screen.ACTIVE_VOICE_ROOM
        showFeedback("Live Voice Room created! You are the Host 👑")
    }
}
