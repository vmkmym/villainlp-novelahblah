package com.example.villainlp.chat.geminiChat

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class GeminiChatModel {
    fun createChatRoom(title: String): String {
        val database = Firebase.database
        val chatRoomsRef = database.getReference("ChatRooms")
        val newChatRoomRef = chatRoomsRef.push()

        newChatRoomRef.child("title").setValue(title)

        // newChatRoomRef의 키는 고유한 ID로 사용될 수 있습니다.
        return newChatRoomRef.key ?: ""
    }
    fun saveChatMessage(geminiChatMessage: GeminiChatMessage, title: String) {
        val database = Firebase.database
        val chatRef = database.getReference("Gemini/$title") // title은 채팅방 이름
        val newMessageRef = chatRef.push()
        newMessageRef.setValue(geminiChatMessage)
    }

    fun saveChatbotMessage(geminiChatbotMessage: GeminiChatMessage, title: String) {
        val database = Firebase.database
        val chatRef = database.getReference("Gemini/$title") // title은 채팅방 이름
        val newMessageRef = chatRef.push()
        newMessageRef.setValue(geminiChatbotMessage)
    }

    fun loadChatMessages(listener: (List<GeminiChatMessage>) -> Unit, title: String) {
        val database = Firebase.database
        val chatRef = database.getReference("Gemini/$title")

        chatRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<GeminiChatMessage>()
                for (childSnapshot in snapshot.children) {
                    val chatMessage = childSnapshot.getValue(GeminiChatMessage::class.java)
                    chatMessage?.let {
                        messages.add(it)
                    }
                }
                listener(messages)
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("GeminiChatModel, loadChatMessages", "fail load message", error.toException())
            }
        })
    }

    fun loadChatbotMessages(
        listener: (List<GeminiChatMessage>) -> Unit,
        title: String,
    ) {
        val database = Firebase.database
        val chatRef = database.getReference("Gemini/$title")

        chatRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<GeminiChatMessage>()
                for (childSnapshot in snapshot.children) {
                    val chatMessage = childSnapshot.getValue(GeminiChatMessage::class.java)
                    chatMessage?.let {
                        messages.add(it)
                    }
                }
                listener(messages)
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("GeminiChatModel, loadChatBotMessages", "fail load message", error.toException())
            }
        })
    }
}