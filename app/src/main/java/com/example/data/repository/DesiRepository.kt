package com.example.data.repository

import android.content.Context
import com.example.data.model.ChatMessage
import com.example.data.model.Comment
import com.example.data.model.Conversation
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType
import com.example.data.model.Post
import com.example.data.model.RoomChatMessage
import com.example.data.model.RoomSpeaker
import com.example.data.model.ShortVideo
import com.example.data.model.TrendingTag
import com.example.data.model.UserProfile
import com.example.data.model.VirtualGift
import com.example.data.model.VoiceChatroom
import com.example.data.remote.SupabaseClient
import com.example.data.remote.SupabaseConnectionState
import com.example.data.remote.SupabaseStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class DesiRepository(private val context: Context) {

    val supabaseClient = SupabaseClient.getInstance(context)

    // Current User Profile
    private val _currentUser = MutableStateFlow(
        UserProfile(
            id = "desi-user-8819",
            username = "arjun_sharma",
            displayName = "Arjun Sharma",
            bio = "🇮🇳 Proud Desi Creator | Tech, Bollywood & Street Food 🍛 | Dil se Desi ✨",
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300",
            coverUrl = "https://images.unsplash.com/photo-1519741497674-611481863552?w=1000",
            preferredLanguage = "hi",
            followersCount = 1420,
            followingCount = 285,
            postsCount = 18,
            isFollowing = false,
            isVerified = true
        )
    )
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    // Language Preference ("hi" or "en")
    private val _currentLanguage = MutableStateFlow("hi")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    // Supabase Connection State
    private val _supabaseState = MutableStateFlow(
        if (supabaseClient.isConfigured) {
            SupabaseConnectionState(
                status = SupabaseStatus.CONNECTED,
                url = supabaseClient.supabaseUrl,
                anonKey = supabaseClient.supabaseAnonKey,
                message = "Connected to Supabase"
            )
        } else {
            SupabaseConnectionState(
                status = SupabaseStatus.NOT_CONFIGURED,
                url = "",
                anonKey = "",
                message = "Setup Supabase URL and Anon Key to enable live cloud sync."
            )
        }
    )
    val supabaseState: StateFlow<SupabaseConnectionState> = _supabaseState.asStateFlow()

    // Posts Feed
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    // Short Videos
    private val _shortVideos = MutableStateFlow<List<ShortVideo>>(emptyList())
    val shortVideos: StateFlow<List<ShortVideo>> = _shortVideos.asStateFlow()

    // Trending Tags
    private val _trendingTags = MutableStateFlow<List<TrendingTag>>(emptyList())
    val trendingTags: StateFlow<List<TrendingTag>> = _trendingTags.asStateFlow()

    // Comments map: PostId -> List<Comment>
    private val _comments = MutableStateFlow<Map<String, List<Comment>>>(emptyMap())
    val comments: StateFlow<Map<String, List<Comment>>> = _comments.asStateFlow()

    // Notifications
    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // Conversations & Messages
    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    private val _messages = MutableStateFlow<Map<String, List<ChatMessage>>>(emptyMap())
    val messages: StateFlow<Map<String, List<ChatMessage>>> = _messages.asStateFlow()

    // Suggested Creators
    private val _suggestedCreators = MutableStateFlow<List<UserProfile>>(emptyList())
    val suggestedCreators: StateFlow<List<UserProfile>> = _suggestedCreators.asStateFlow()

    // Voice Chatrooms (ShareChat-style Audio Adda)
    private val _voiceChatrooms = MutableStateFlow<List<VoiceChatroom>>(emptyList())
    val voiceChatrooms: StateFlow<List<VoiceChatroom>> = _voiceChatrooms.asStateFlow()

    private val _activeChatroom = MutableStateFlow<VoiceChatroom?>(null)
    val activeChatroom: StateFlow<VoiceChatroom?> = _activeChatroom.asStateFlow()

    private val _roomMessages = MutableStateFlow<Map<String, List<RoomChatMessage>>>(emptyMap())
    val roomMessages: StateFlow<Map<String, List<RoomChatMessage>>> = _roomMessages.asStateFlow()

    private val _isUserMutedInRoom = MutableStateFlow(true)
    val isUserMutedInRoom: StateFlow<Boolean> = _isUserMutedInRoom.asStateFlow()

    private val _userMicSeat = MutableStateFlow<Int?>(null)
    val userMicSeat: StateFlow<Int?> = _userMicSeat.asStateFlow()

    init {
        seedInitialData()
    }

    private fun seedInitialData() {
        val priyaProfile = UserProfile(
            id = "user_priya_01",
            username = "priya_desivibes",
            displayName = "Priya Patel",
            bio = "Rangoli Artist & Traditional Recipes 🌸 | Celebrating Indian Culture",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300",
            followersCount = 58900,
            followingCount = 120,
            postsCount = 42,
            isFollowing = true,
            isVerified = true
        )

        val kabirProfile = UserProfile(
            id = "user_kabir_02",
            username = "kabir_comedy",
            displayName = "Kabir Khan 😂",
            bio = "Desi Jugaad & Stand-up Clips | Daily Dose of Smiles 🎭",
            avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300",
            followersCount = 124000,
            followingCount = 45,
            postsCount = 88,
            isFollowing = false,
            isVerified = true
        )

        val ananyaProfile = UserProfile(
            id = "user_ananya_03",
            username = "ananya_melodies",
            displayName = "Ananya Roy 🎶",
            bio = "Classical Fusion & Sufi Vocals | Kolkata 🪕",
            avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300",
            followersCount = 34500,
            followingCount = 95,
            postsCount = 31,
            isFollowing = true,
            isVerified = false
        )

        val rohitProfile = UserProfile(
            id = "user_rohit_04",
            username = "cricket_desi",
            displayName = "Rohit & Cricket Masti 🏏",
            bio = "Match highlights, pitch reports & cricket memes! Bleed Blue 🇮🇳",
            avatarUrl = "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=300",
            followersCount = 89200,
            followingCount = 12,
            postsCount = 115,
            isFollowing = false,
            isVerified = true
        )

        _suggestedCreators.value = listOf(priyaProfile, kabirProfile, ananyaProfile, rohitProfile)

        // Seed Posts
        val initialPosts = listOf(
            Post(
                id = "post_01",
                userId = priyaProfile.id,
                authorName = priyaProfile.displayName,
                authorHandle = "@" + priyaProfile.username,
                authorAvatar = priyaProfile.avatarUrl,
                isAuthorVerified = true,
                caption = "दिवाली की तैयारियों के साथ घर में खुशियों की रौनक! शुभ दीपावली आप सभी को 🙏✨ Spread positivity and love. #DiwaliVibes #DesiFestivals #FestiveSeason #RangoliArt",
                mediaUrls = listOf("https://images.unsplash.com/photo-1514565131-fce0801e5785?w=800"),
                mediaType = "image",
                tags = listOf("DiwaliVibes", "DesiFestivals", "RangoliArt"),
                language = "hi",
                likesCount = 3840,
                commentsCount = 245,
                sharesCount = 512,
                isLiked = true,
                isBookmarked = false,
                createdAt = "2 घंटे पहले"
            ),
            Post(
                id = "post_02",
                userId = kabirProfile.id,
                authorName = kabirProfile.displayName,
                authorHandle = "@" + kabirProfile.username,
                authorAvatar = kabirProfile.avatarUrl,
                isAuthorVerified = true,
                caption = "जब माँ कहती है कि 'सब्जी में नमक कम है खुद डाल लो' 😭 The ultimate desi household mystery solved! Tag that sibling who always complains! #DesiJugaad #MomsOfIndia #DesiMemes #Comedy",
                mediaUrls = listOf("https://images.unsplash.com/photo-1526772662000-3f88f10405ff?w=800"),
                mediaType = "image",
                tags = listOf("DesiJugaad", "MomsOfIndia", "DesiMemes"),
                language = "hi",
                likesCount = 9410,
                commentsCount = 612,
                sharesCount = 1340,
                isLiked = false,
                isBookmarked = true,
                createdAt = "4 घंटे पहले"
            ),
            Post(
                id = "post_03",
                userId = ananyaProfile.id,
                authorName = ananyaProfile.displayName,
                authorHandle = "@" + ananyaProfile.username,
                authorAvatar = ananyaProfile.avatarUrl,
                isAuthorVerified = false,
                caption = "Late evening riyaaz session with old Bollywood acoustic unplugged 🎼 'तेरे बिना जिंदगी से कोई शिकवा तो नहीं...'. Listening on loop! #SufiVibes #AcousticDesi #RetroBollywood #DesiMusic",
                mediaUrls = listOf("https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=800"),
                mediaType = "image",
                tags = listOf("SufiVibes", "RetroBollywood", "DesiMusic"),
                language = "hi",
                likesCount = 1890,
                commentsCount = 118,
                sharesCount = 205,
                isLiked = false,
                isBookmarked = false,
                createdAt = "6 घंटे पहले"
            ),
            Post(
                id = "post_04",
                userId = rohitProfile.id,
                authorName = rohitProfile.displayName,
                authorHandle = "@" + rohitProfile.username,
                authorAvatar = rohitProfile.avatarUrl,
                isAuthorVerified = true,
                caption = "What an unbelievable match finish in Kolkata! That final over yorker was pure magic 🇮🇳🏏 Champions spirit! What was your favorite moment from tonight? #CricketFever #TeamIndia #DesiPride",
                mediaUrls = listOf("https://images.unsplash.com/photo-1540747913346-19e32dc3e97e?w=800"),
                mediaType = "image",
                tags = listOf("CricketFever", "TeamIndia", "DesiPride"),
                language = "en",
                likesCount = 14320,
                commentsCount = 1089,
                sharesCount = 4210,
                isLiked = true,
                isBookmarked = true,
                createdAt = "8 घंटे पहले"
            )
        )
        _posts.value = initialPosts

        // Seed Comments
        _comments.value = mapOf(
            "post_01" to listOf(
                Comment(
                    id = "c1",
                    postId = "post_01",
                    userId = "user_kavita",
                    userName = "Kavita Verma",
                    userAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100",
                    content = "बहुत सुंदर रंगोली दीदी! दिवाली की हार्दिक शुभकामनाएं 🪔",
                    createdAt = "1 घंटा पहले"
                ),
                Comment(
                    id = "c2",
                    postId = "post_01",
                    userId = "user_rahul",
                    userName = "Rahul Gupta",
                    userAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100",
                    content = "The color combination is breathtaking! What powders did you use?",
                    createdAt = "30 मिनट पहले"
                )
            ),
            "post_02" to listOf(
                Comment(
                    id = "c3",
                    postId = "post_02",
                    userId = "user_rohit",
                    userName = "Rohit Verma",
                    userAvatar = "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=100",
                    content = "Hahaha so relatable bhai! Every Indian mom ever 😂",
                    createdAt = "2 घंटे पहले"
                )
            )
        )

        // Seed Short Videos (Moj style)
        _shortVideos.value = listOf(
            ShortVideo(
                id = "clip_01",
                creatorId = priyaProfile.id,
                creatorName = priyaProfile.displayName,
                creatorHandle = "@" + priyaProfile.username,
                creatorAvatar = priyaProfile.avatarUrl,
                caption = "30-second easy Sandesh sweet recipe from Bengal! Try this at home 🍯✨ #SweetTooth #DesiRecipes #KolkataSweets",
                videoUrl = "https://assets.mixkit.co/videos/preview/mixkit-traditional-indian-sweets-close-up-42861-large.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=600",
                soundTitle = "Bengal Traditional Dhak & Shehnai 🎶",
                likesCount = 14500,
                commentsCount = 280,
                sharesCount = 920,
                isLiked = true,
                isFollowing = true
            ),
            ShortVideo(
                id = "clip_02",
                creatorId = kabirProfile.id,
                creatorName = kabirProfile.displayName,
                creatorHandle = "@" + kabirProfile.username,
                creatorAvatar = kabirProfile.avatarUrl,
                caption = "Desi wedding dance moves starter pack 😂 When the DJ plays Dhol! #WeddingSeason #BhangraBeats #DesiHumor",
                videoUrl = "https://assets.mixkit.co/videos/preview/mixkit-people-dancing-at-an-indian-wedding-party-42865-large.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1519741497674-611481863552?w=600",
                soundTitle = "Punjab Dhol Mashup 2026 🔥",
                likesCount = 38900,
                commentsCount = 1240,
                sharesCount = 4500,
                isLiked = false,
                isFollowing = false
            ),
            ShortVideo(
                id = "clip_03",
                creatorId = ananyaProfile.id,
                creatorName = ananyaProfile.displayName,
                creatorHandle = "@" + ananyaProfile.username,
                creatorAvatar = ananyaProfile.avatarUrl,
                caption = "Harmonium solo + Classical Thumri in Raag Bhairavi 🎹 Morning peace! #Riyaaz #IndianClassical #SoulMusic",
                videoUrl = "https://assets.mixkit.co/videos/preview/mixkit-playing-an-indian-instrument-42863-large.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600",
                soundTitle = "Raag Bhairavi Alaap - Live Riyaaz 🎵",
                likesCount = 8900,
                commentsCount = 195,
                sharesCount = 310,
                isLiked = false,
                isFollowing = true
            )
        )

        // Seed Trending Tags
        _trendingTags.value = listOf(
            TrendingTag("#DiwaliVibes", "184K posts", "Festivals", 1),
            TrendingTag("#CricketFever", "142K posts", "Sports", 2),
            TrendingTag("#DesiJugaad", "98K posts", "Comedy", 3),
            TrendingTag("#RetroBollywood", "65K posts", "Entertainment", 4),
            TrendingTag("#StreetFoodIndia", "52K posts", "Food", 5),
            TrendingTag("#DesiMusic", "44K posts", "Music", 6)
        )

        // Seed Notifications
        _notifications.value = listOf(
            NotificationItem(
                id = "notif_01",
                recipientId = _currentUser.value.id,
                actorId = priyaProfile.id,
                actorName = priyaProfile.displayName,
                actorAvatar = priyaProfile.avatarUrl,
                type = NotificationType.LIKE,
                content = "liked your post 'Street Food in Chandni Chowk'",
                postId = "post_01",
                isRead = false,
                createdAt = "5m ago"
            ),
            NotificationItem(
                id = "notif_02",
                recipientId = _currentUser.value.id,
                actorId = kabirProfile.id,
                actorName = kabirProfile.displayName,
                actorAvatar = kabirProfile.avatarUrl,
                type = NotificationType.COMMENT,
                content = "commented: 'Bhai bilkul sahi baat kahi!'",
                postId = "post_02",
                isRead = false,
                createdAt = "25m ago"
            ),
            NotificationItem(
                id = "notif_03",
                recipientId = _currentUser.value.id,
                actorId = ananyaProfile.id,
                actorName = ananyaProfile.displayName,
                actorAvatar = ananyaProfile.avatarUrl,
                type = NotificationType.FOLLOW,
                content = "started following you.",
                isRead = true,
                createdAt = "1h ago"
            )
        )

        // Seed Conversations
        _conversations.value = listOf(
            Conversation(
                id = "conv_priya",
                peer = priyaProfile,
                lastMessage = "Namaste Arjun! Diwali ke sweet boxes kab aa rahe hain? 🪔",
                lastTimestamp = "10:45 AM",
                unreadCount = 1
            ),
            Conversation(
                id = "conv_kabir",
                peer = kabirProfile,
                lastMessage = "Bhai next meme video script ready hai! Check WhatsApp.",
                lastTimestamp = "Yesterday",
                unreadCount = 0
            ),
            Conversation(
                id = "conv_ananya",
                peer = ananyaProfile,
                lastMessage = "Thank you for the melody suggestion! Worked like magic 🎶",
                lastTimestamp = "2d ago",
                unreadCount = 0
            )
        )

        // Seed Messages for conv_priya
        _messages.value = mapOf(
            "conv_priya" to listOf(
                ChatMessage(
                    id = "m1",
                    conversationId = "conv_priya",
                    senderId = priyaProfile.id,
                    senderName = priyaProfile.displayName,
                    senderAvatar = priyaProfile.avatarUrl,
                    content = "Namaste Arjun! Hope you are having a wonderful festive week 🙏",
                    timestamp = "10:30 AM",
                    isMine = false
                ),
                ChatMessage(
                    id = "m2",
                    conversationId = "conv_priya",
                    senderId = _currentUser.value.id,
                    senderName = _currentUser.value.displayName,
                    senderAvatar = _currentUser.value.avatarUrl,
                    content = "Namaste Priya ji! Yes, very busy with decorations and family celebrations ✨",
                    timestamp = "10:35 AM",
                    isMine = true
                ),
                ChatMessage(
                    id = "m3",
                    conversationId = "conv_priya",
                    senderId = priyaProfile.id,
                    senderName = priyaProfile.displayName,
                    senderAvatar = priyaProfile.avatarUrl,
                    content = "Diwali ke sweet boxes kab aa rahe hain? 🪔",
                    timestamp = "10:45 AM",
                    isMine = false
                )
            )
        )

        // Seed Voice Chatrooms (ShareChat-style Audio Adda)
        _voiceChatrooms.value = listOf(
            VoiceChatroom(
                id = "room_01",
                title = "Bollywood Antakshari Live 🎤 Gaane & Masti",
                category = "Music",
                language = "hi",
                host = ananyaProfile,
                speakers = listOf(
                    RoomSpeaker(0, ananyaProfile, isHost = true, isMuted = false, isSpeaking = true),
                    RoomSpeaker(1, kabirProfile, isHost = false, isMuted = false, isSpeaking = false),
                    RoomSpeaker(2, priyaProfile, isHost = false, isMuted = true, isSpeaking = false)
                ),
                listenersCount = 840,
                listeners = listOf(rohitProfile, _currentUser.value),
                tags = listOf("Antakshari", "BollywoodHits", "LiveSinging")
            ),
            VoiceChatroom(
                id = "room_02",
                title = "Cricket Live Match Charcha 🏏 IND vs AUS Debate",
                category = "Cricket",
                language = "hi",
                host = rohitProfile,
                speakers = listOf(
                    RoomSpeaker(0, rohitProfile, isHost = true, isMuted = false, isSpeaking = true),
                    RoomSpeaker(1, kabirProfile, isHost = false, isMuted = false, isSpeaking = false)
                ),
                listenersCount = 1420,
                listeners = listOf(priyaProfile),
                tags = listOf("CricketAdda", "TeamIndia", "MatchCharcha")
            ),
            VoiceChatroom(
                id = "room_03",
                title = "देसी हास्य कवि सम्मेलन 😂 नॉन-स्टॉप हंसी",
                category = "Comedy",
                language = "hi",
                host = kabirProfile,
                speakers = listOf(
                    RoomSpeaker(0, kabirProfile, isHost = true, isMuted = false, isSpeaking = true),
                    RoomSpeaker(1, ananyaProfile, isHost = false, isMuted = true, isSpeaking = false)
                ),
                listenersCount = 960,
                tags = listOf("DesiJokes", "StandUp", "HasyaKavi")
            ),
            VoiceChatroom(
                id = "room_04",
                title = "शायरी और ग़ज़ल की महफ़िल 📜 दर्द और इश्क़",
                category = "Shayari",
                language = "hi",
                host = priyaProfile,
                speakers = listOf(
                    RoomSpeaker(0, priyaProfile, isHost = true, isMuted = false, isSpeaking = true)
                ),
                listenersCount = 520,
                tags = listOf("ShayariAdda", "UrduShayari", "DilKiBaat")
            ),
            VoiceChatroom(
                id = "room_05",
                title = "Late Night Chai Pe Gupshup ☕ दिल खोल के बातें",
                category = "Gupshup",
                language = "hi",
                host = _currentUser.value,
                speakers = listOf(
                    RoomSpeaker(0, _currentUser.value, isHost = true, isMuted = false, isSpeaking = false)
                ),
                listenersCount = 310,
                tags = listOf("ChaiAdda", "Friendship", "MidnightTalks")
            )
        )

        // Seed Chatroom Messages
        _roomMessages.value = mapOf(
            "room_01" to listOf(
                RoomChatMessage("rm1", "Priya Patel", priyaProfile.avatarUrl, "Kya sur lagaya hai Ananya ji! 👏"),
                RoomChatMessage("rm2", "Kabir Khan", kabirProfile.avatarUrl, "Mera agla gaana 'K' se shuru hoga! 😂"),
                RoomChatMessage("rm3", "System", "", "Rohan Sharma joined the stage 🎙️", isSystem = true),
                RoomChatMessage("rm4", "Kavita", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100", "sent a 🌹 Rose gift to the Host!", giftEmoji = "🌹")
            ),
            "room_02" to listOf(
                RoomChatMessage("rm5", "Rohit", rohitProfile.avatarUrl, "Rohit Sharma ki pull shot ka koi jawaab nahi! 🏏"),
                RoomChatMessage("rm6", "Amit", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100", "Bhai bowling lineup me spinners ka jaadu chalega kal.")
            )
        )
    }

    // Toggle Language
    fun setLanguage(lang: String) {
        _currentLanguage.value = lang
    }

    // Update Supabase Settings
    suspend fun configureSupabase(url: String, key: String): Result<String> {
        supabaseClient.updateConfig(url, key)
        _supabaseState.value = _supabaseState.value.copy(
            status = SupabaseStatus.CONNECTING,
            url = url,
            anonKey = key,
            message = "Validating Supabase connection..."
        )
        val result = supabaseClient.checkHealth()
        if (result.isSuccess) {
            _supabaseState.value = _supabaseState.value.copy(
                status = SupabaseStatus.CONNECTED,
                message = "Connected to Supabase successfully!"
            )
        } else {
            _supabaseState.value = _supabaseState.value.copy(
                status = SupabaseStatus.ERROR,
                message = result.exceptionOrNull()?.message ?: "Failed to connect to Supabase."
            )
        }
        return result
    }

    // Toggle Post Like
    fun toggleLikePost(postId: String) {
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) {
                val newLiked = !post.isLiked
                val newCount = if (newLiked) post.likesCount + 1 else maxOf(0, post.likesCount - 1)
                post.copy(isLiked = newLiked, likesCount = newCount)
            } else post
        }
    }

    // Toggle Post Bookmark
    fun toggleBookmarkPost(postId: String) {
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) {
                post.copy(isBookmarked = !post.isBookmarked)
            } else post
        }
    }

    // Add Comment
    fun addComment(postId: String, content: String) {
        if (content.isBlank()) return
        val newComment = Comment(
            id = "c_" + UUID.randomUUID().toString().take(8),
            postId = postId,
            userId = _currentUser.value.id,
            userName = _currentUser.value.displayName,
            userAvatar = _currentUser.value.avatarUrl,
            content = content.trim(),
            createdAt = "Just now"
        )
        val currentList = _comments.value[postId] ?: emptyList()
        _comments.value = _comments.value + (postId to (currentList + newComment))

        // Update post comment count
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) {
                post.copy(commentsCount = post.commentsCount + 1)
            } else post
        }
    }

    // Delete Comment
    fun deleteComment(postId: String, commentId: String) {
        val currentList = _comments.value[postId] ?: return
        _comments.value = _comments.value + (postId to currentList.filter { it.id != commentId })
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) {
                post.copy(commentsCount = maxOf(0, post.commentsCount - 1))
            } else post
        }
    }

    // Create & Publish Post
    fun createPost(caption: String, mediaUrl: String, mediaType: String, tags: List<String>, language: String): Post {
        val newPost = Post(
            id = "post_" + UUID.randomUUID().toString().take(8),
            userId = _currentUser.value.id,
            authorName = _currentUser.value.displayName,
            authorHandle = "@" + _currentUser.value.username,
            authorAvatar = _currentUser.value.avatarUrl,
            isAuthorVerified = _currentUser.value.isVerified,
            caption = caption.trim(),
            mediaUrls = if (mediaUrl.isNotBlank()) listOf(mediaUrl) else emptyList(),
            mediaType = mediaType,
            tags = tags,
            language = language,
            likesCount = 0,
            commentsCount = 0,
            sharesCount = 0,
            isLiked = false,
            isBookmarked = false,
            createdAt = "अभी-अभी / Just now"
        )
        _posts.value = listOf(newPost) + _posts.value
        _currentUser.value = _currentUser.value.copy(postsCount = _currentUser.value.postsCount + 1)
        return newPost
    }

    // Delete Post
    fun deletePost(postId: String) {
        _posts.value = _posts.value.filter { it.id != postId }
    }

    // Toggle Follow on a Creator
    fun toggleFollowCreator(userId: String) {
        _suggestedCreators.value = _suggestedCreators.value.map { creator ->
            if (creator.id == userId) {
                val newStatus = !creator.isFollowing
                val newFollowers = if (newStatus) creator.followersCount + 1 else maxOf(0, creator.followersCount - 1)
                creator.copy(isFollowing = newStatus, followersCount = newFollowers)
            } else creator
        }
        // Also update in short videos
        _shortVideos.value = _shortVideos.value.map { video ->
            if (video.creatorId == userId) {
                video.copy(isFollowing = !video.isFollowing)
            } else video
        }
    }

    // Toggle Short Video Like
    fun toggleLikeShortVideo(videoId: String) {
        _shortVideos.value = _shortVideos.value.map { video ->
            if (video.id == videoId) {
                val newLiked = !video.isLiked
                val newCount = if (newLiked) video.likesCount + 1 else maxOf(0, video.likesCount - 1)
                video.copy(isLiked = newLiked, likesCount = newCount)
            } else video
        }
    }

    // Send Message
    fun sendMessage(conversationId: String, content: String) {
        if (content.isBlank()) return
        val newMsg = ChatMessage(
            id = "m_" + UUID.randomUUID().toString().take(8),
            conversationId = conversationId,
            senderId = _currentUser.value.id,
            senderName = _currentUser.value.displayName,
            senderAvatar = _currentUser.value.avatarUrl,
            content = content.trim(),
            timestamp = "Just now",
            isMine = true
        )
        val currentMsgs = _messages.value[conversationId] ?: emptyList()
        _messages.value = _messages.value + (conversationId to (currentMsgs + newMsg))

        // Update conversation preview
        _conversations.value = _conversations.value.map { conv ->
            if (conv.id == conversationId) {
                conv.copy(lastMessage = content.trim(), lastTimestamp = "Just now")
            } else conv
        }
    }

    // Mark Notification as Read
    fun markNotificationRead(id: String) {
        _notifications.value = _notifications.value.map { notif ->
            if (notif.id == id) notif.copy(isRead = true) else notif
        }
    }

    // Mark All Notifications as Read
    fun markAllNotificationsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    // Update Profile
    fun updateProfile(displayName: String, bio: String, avatarUrl: String) {
        _currentUser.value = _currentUser.value.copy(
            displayName = displayName.ifBlank { _currentUser.value.displayName },
            bio = bio,
            avatarUrl = avatarUrl.ifBlank { _currentUser.value.avatarUrl }
        )
    }

    // Report Post or User
    fun reportItem(targetType: String, targetId: String, reason: String) {
        // Log report locally & optionally to Supabase
    }

    // Block User
    fun blockUser(userId: String) {
        _posts.value = _posts.value.filter { it.userId != userId }
    }

    // Voice Chatroom Actions
    fun joinChatroom(room: VoiceChatroom) {
        _activeChatroom.value = room
        _userMicSeat.value = null
        _isUserMutedInRoom.value = true
        _voiceChatrooms.value = _voiceChatrooms.value.map {
            if (it.id == room.id) it.copy(listenersCount = it.listenersCount + 1) else it
        }
    }

    fun leaveChatroom() {
        _activeChatroom.value?.let { room ->
            _voiceChatrooms.value = _voiceChatrooms.value.map {
                if (it.id == room.id) it.copy(listenersCount = maxOf(0, it.listenersCount - 1)) else it
            }
        }
        _activeChatroom.value = null
        _userMicSeat.value = null
        _isUserMutedInRoom.value = true
    }

    fun takeMicSeat(seatIndex: Int) {
        val room = _activeChatroom.value ?: return
        val currentSpeakers = room.speakers.toMutableList()
        if (currentSpeakers.any { it.seatIndex == seatIndex }) return

        val newSpeaker = RoomSpeaker(
            seatIndex = seatIndex,
            user = _currentUser.value,
            isHost = room.host.id == _currentUser.value.id,
            isMuted = _isUserMutedInRoom.value,
            isSpeaking = false
        )
        currentSpeakers.add(newSpeaker)
        val updatedRoom = room.copy(speakers = currentSpeakers)
        _activeChatroom.value = updatedRoom
        _userMicSeat.value = seatIndex

        sendRoomMessage(room.id, "${_currentUser.value.displayName} took mic seat #${seatIndex + 1} 🎙️", isSystem = true)
    }

    fun leaveMicSeat() {
        val room = _activeChatroom.value ?: return
        val currentSpeakers = room.speakers.filter { it.user.id != _currentUser.value.id }
        _activeChatroom.value = room.copy(speakers = currentSpeakers)
        _userMicSeat.value = null
        _isUserMutedInRoom.value = true
    }

    fun toggleMute() {
        _isUserMutedInRoom.value = !_isUserMutedInRoom.value
        val room = _activeChatroom.value ?: return
        val updatedSpeakers = room.speakers.map { speaker ->
            if (speaker.user.id == _currentUser.value.id) {
                speaker.copy(isMuted = _isUserMutedInRoom.value)
            } else speaker
        }
        _activeChatroom.value = room.copy(speakers = updatedSpeakers)
    }

    fun sendRoomMessage(roomId: String, text: String, giftEmoji: String? = null, isSystem: Boolean = false) {
        val newMsg = RoomChatMessage(
            id = "rcm_" + UUID.randomUUID().toString().take(8),
            senderName = _currentUser.value.displayName,
            senderAvatar = _currentUser.value.avatarUrl,
            text = text,
            isSystem = isSystem,
            giftEmoji = giftEmoji
        )
        val list = _roomMessages.value[roomId] ?: emptyList()
        _roomMessages.value = _roomMessages.value + (roomId to (list + newMsg))
    }

    fun createVoiceChatroom(title: String, category: String, language: String): VoiceChatroom {
        val newRoom = VoiceChatroom(
            id = "room_" + UUID.randomUUID().toString().take(8),
            title = title.ifBlank { "देसी महफ़िल और गपशप ☕" },
            category = category,
            language = language,
            host = _currentUser.value,
            speakers = listOf(
                RoomSpeaker(
                    seatIndex = 0,
                    user = _currentUser.value,
                    isHost = true,
                    isMuted = false,
                    isSpeaking = true
                )
            ),
            listenersCount = 1,
            isLive = true,
            tags = listOf("DesiAdda", category)
        )
        _voiceChatrooms.value = listOf(newRoom) + _voiceChatrooms.value
        joinChatroom(newRoom)
        _userMicSeat.value = 0
        _isUserMutedInRoom.value = false
        return newRoom
    }
}
