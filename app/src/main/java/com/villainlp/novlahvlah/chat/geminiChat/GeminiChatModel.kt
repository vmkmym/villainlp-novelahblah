package com.villainlp.novlahvlah.chat.geminiChat

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class GeminiChatModel {
    fun saveChatMessage(geminiChatMessage: GeminiChatMessage, title: String, uuid: String) {
        val database = Firebase.database
        val chatRef = database.getReference("final/$title")
        val newMessageRef = chatRef.push()
        newMessageRef.setValue(geminiChatMessage)

        newMessageRef.child("uuid").setValue(uuid)
    }

    fun saveChatbotMessage(geminiChatbotMessage: GeminiChatMessage, title: String, uuid: String) {
        val database = Firebase.database
        val chatRef = database.getReference("final/$title")
        val newMessageRef = chatRef.push()
        newMessageRef.setValue(geminiChatbotMessage)

        newMessageRef.child("uuid").setValue(uuid)
    }

    fun loadChatMessages(listener: (List<GeminiChatMessage>) -> Unit, title: String, uuid: String) {
        val database = Firebase.database
        val chatRef = database.getReference("final/$title")

        val query = chatRef.orderByChild("uuid").equalTo(uuid)

        query.addValueEventListener(object : ValueEventListener {
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
                Log.w("GeminiChatModel, loadChatMessages", "fail load message", error.toException())
            }
        })
    }

    fun loadChatbotMessages(listener: (List<GeminiChatMessage>) -> Unit, title: String, uuid: String) {
        val database = Firebase.database
        val chatRef = database.getReference("final/$title")

        val query = chatRef.orderByChild("uuid").equalTo(uuid)

        query.addValueEventListener(object : ValueEventListener {
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
                Log.w("GeminiChatModel, loadChatBotMessages", "fail load message", error.toException())
            }
        })
    }
}