package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.DesiRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("DesiConnect", appName)
    }

    @Test
    fun `repository initial seed and post like test`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = DesiRepository(context)

        val initialPosts = repo.posts.value
        assertTrue("Posts should be seeded", initialPosts.isNotEmpty())

        val firstPost = initialPosts.first()
        val initialLikes = firstPost.likesCount
        val initialLikedState = firstPost.isLiked

        repo.toggleLikePost(firstPost.id)
        val updatedPost = repo.posts.value.first { it.id == firstPost.id }
        assertEquals(!initialLikedState, updatedPost.isLiked)

        // Test post creation
        val newPost = repo.createPost(
            caption = "Test Desi Post 🎉",
            mediaUrl = "",
            mediaType = "quote",
            tags = listOf("TestTag"),
            language = "hi"
        )
        assertNotNull(newPost)
        assertEquals(initialPosts.size + 1, repo.posts.value.size)
    }

    @Test
    fun `voice chatroom join take mic and send gift test`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = DesiRepository(context)

        val rooms = repo.voiceChatrooms.value
        assertTrue("Voice chatrooms should be seeded", rooms.isNotEmpty())

        val firstRoom = rooms.first()
        val initialListeners = firstRoom.listenersCount

        // Join room
        repo.joinChatroom(firstRoom)
        assertNotNull(repo.activeChatroom.value)
        assertEquals(firstRoom.id, repo.activeChatroom.value?.id)

        // Take an available mic seat (e.g., seat 3)
        repo.takeMicSeat(3)
        assertEquals(3, repo.userMicSeat.value)
        val activeRoom = repo.activeChatroom.value
        assertTrue(activeRoom?.speakers?.any { it.seatIndex == 3 } == true)

        // Toggle mute
        val wasMuted = repo.isUserMutedInRoom.value
        repo.toggleMute()
        assertEquals(!wasMuted, repo.isUserMutedInRoom.value)

        // Send a room message
        repo.sendRoomMessage(firstRoom.id, "Bhai kya live mahfil hai! 🪔")
        val messages = repo.roomMessages.value[firstRoom.id]
        assertTrue(messages?.any { it.text.contains("mahfil") } == true)

        // Leave room
        repo.leaveChatroom()
        assertEquals(null, repo.activeChatroom.value)
    }
}
