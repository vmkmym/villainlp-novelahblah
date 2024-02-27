package com.example.villainlp.chat.geminiChat

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

        // query를 넣어야 하는데 무슨 이유에서인지 대화내용을 로드를 못함.
        // 경로가 정확하지 않았나? 싶어서 코파일럿한테 계속 물어봤지만 아닌 듯 (ChatModel이랑 코드 거의 비슷한데?)
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

    fun loadChatbotMessages(listener: (List<GeminiChatMessage>) -> Unit, title: String, uuid: String) {
        val database = Firebase.database
        val chatRef = database.getReference("final/$title")

        val query = chatRef.orderByChild("uuid").equalTo(uuid)

        // 마찬가지임
        // 근데 chatRef를 하면 당연히 구분 못함.
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
                Log.w("GeminiChatModel, loadChatBotMessages", "fail load message", error.toException())
            }
        })
    }
}