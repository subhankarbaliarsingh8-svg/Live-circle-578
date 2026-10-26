package com.example.data.model

data class UserProfile(
    val id: String,
    val username: String,
    val displayName: String,
    val bio: String = "",
    val avatarUrl: String = "",
    val coverUrl: String = "",
    val preferredLanguage: String = "hi",
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val postsCount: Int = 0,
    val isFollowing: Boolean = false,
    val isVerified: Boolean = false
)

data class Post(
    val id: String,
    val userId: String,
    val authorName: String,
    val authorHandle: String,
    val authorAvatar: String,
    val isAuthorVerified: Boolean = false,
    val caption: String,
    val mediaUrls: List<String> = emptyList(),
    val mediaType: String = "image", // "image", "video", "quote"
    val tags: List<String> = emptyList(),
    val language: String = "hi",
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
    val createdAt: String = "Just now"
)

data class ShortVideo(
    val id: String,
    val creatorId: String,
    val creatorName: String,
    val creatorHandle: String,
    val creatorAvatar: String,
    val caption: String,
    val videoUrl: String,
    val thumbnailUrl: String = "",
    val soundTitle: String = "Desi Beats Original Audio 🎵",
    val likesCount: Int = 1200,
    val commentsCount: Int = 45,
    val sharesCount: Int = 89,
    val isLiked: Boolean = false,
    val isFollowing: Boolean = false
)

data class Comment(
    val id: String,
    val postId: String,
    val userId: String,
    val userName: String,
    val userAvatar: String,
    val content: String,
    val createdAt: String = "Just now",
    val parentId: String? = null
)

enum class NotificationType {
    LIKE, COMMENT, FOLLOW, MENTION
}

data class NotificationItem(
    val id: String,
    val recipientId: String,
    val actorId: String,
    val actorName: String,
    val actorAvatar: String,
    val type: NotificationType,
    val content: String,
    val postId: String? = null,
    val isRead: Boolean = false,
    val createdAt: String = "Just now"
)

data class ChatMessage(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val senderAvatar: String,
    val content: String,
    val timestamp: String = "Now",
    val isMine: Boolean = false
)

data class Conversation(
    val id: String,
    val peer: UserProfile,
    val lastMessage: String,
    val lastTimestamp: String,
    val unreadCount: Int = 0
)

data class TrendingTag(
    val tag: String,
    val postCount: String,
    val category: String,
    val rank: Int
)

data class RoomSpeaker(
    val seatIndex: Int,
    val user: UserProfile,
    val isHost: Boolean = false,
    val isMuted: Boolean = false,
    val isSpeaking: Boolean = false
)

data class RoomChatMessage(
    val id: String,
    val senderName: String,
    val senderAvatar: String,
    val text: String,
    val isSystem: Boolean = false,
    val giftEmoji: String? = null
)

data class VirtualGift(
    val id: String,
    val name: String,
    val emoji: String,
    val desiCoins: Int
)

data class VoiceChatroom(
    val id: String,
    val title: String,
    val category: String,
    val language: String = "hi",
    val host: UserProfile,
    val speakers: List<RoomSpeaker> = emptyList(),
    val listenersCount: Int = 120,
    val listeners: List<UserProfile> = emptyList(),
    val isLive: Boolean = true,
    val tags: List<String> = emptyList()
)
