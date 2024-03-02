package com.example.villainlp.chat.openAichat

import android.util.Log
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.thread.ThreadId
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatModel {
    @OptIn(BetaOpenAI::class)
    fun saveChatMessage(chatMessage: ChatMessage, title: String, threadId: ThreadId?) {
        val database = Firebase.database
        val chatRef = database.getReference("gpt35/$title") // title은 채팅방 이름
        val newMessageRef = chatRef.push()
        newMessageRef.setValue(chatMessage)

        // 해당 대화에 대한 threadId도 저장
        newMessageRef.child("threadId").setValue(threadId.toString())
    }

    @OptIn(BetaOpenAI::class)
    fun saveChatbotMessage(chatbotMessage: ChatbotMessage, title: String, threadId: ThreadId?) {
        val database = Firebase.database
        val chatRef = database.getReference("gpt35/$title") // title은 채팅방 이름
        val newMessageRef = chatRef.push()
        newMessageRef.setValue(chatbotMessage)

        // 해당 대화에 대한 threadId도 저장
        newMessageRef.child("threadId").setValue(threadId.toString())
    }


    @OptIn(BetaOpenAI::class)
    fun loadChatMessages(listener: (List<ChatMessage>) -> Unit, title: String, threadId: ThreadId?) {
        val database = Firebase.database
        val chatRef = database.getReference("gpt35/$title")

        // 대화 스레드에 대한 쿼리를 추가하여 해당 스레드의 메시지만 가져옵니다.
        val query = chatRef.orderByChild("threadId").equalTo(threadId.toString())

        // 쿼리를 이용해서 데이터베이스에서 데이터를 가져오는 로직
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<ChatMessage>()
                for (childSnapshot in snapshot.children) {
                    val chatMessage = childSnapshot.getValue(ChatMessage::class.java)
                    chatMessage?.let {
                        messages.add(it)
                    }
                }
                listener(messages)
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("Load Chat Message", "채팅 내용 로드하기 실패!!", error.toException())
            }
        })
    }

    @OptIn(BetaOpenAI::class)
    fun loadChatBotMessages(listener: (List<ChatbotMessage>) -> Unit, title: String, threadId: ThreadId?) {
        val database = Firebase.database
        val chatRef = database.getReference("gpt35/$title")

        // 대화 스레드에 대한 쿼리를 추가하여 해당 스레드의 메시지만 가져옵니다.
        val query = chatRef.orderByChild("threadId").equalTo(threadId.toString())

        // 쿼리를 이용해서 데이터베이스에서 데이터를 가져오는 로직
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<ChatbotMessage>()
                for (childSnapshot in snapshot.children) {
                    val chatMessage = childSnapshot.getValue(ChatbotMessage::class.java)
                    chatMessage?.let {
                        messages.add(it)
                    }
                }
                listener(messages)
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("Load Chat Message", "채팅 내용 로드하기 실패!!", error.toException())
            }
        })
    }
}