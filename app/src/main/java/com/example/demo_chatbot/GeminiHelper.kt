package com.example.demo_chatbot

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiHelper(context: Context) {
    private val generativeModel = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel("gemini-2.5-flash")

    suspend fun generateText(prompt: String): String {
        return try {
            // gọi trong IO dispatcher vì là network
            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(prompt) // suspend
            }
            response.text ?: "Không có phản hồi từ AI."
        } catch (e: Exception) {
            e.printStackTrace()
            "Lỗi khi gọi AI: ${e.localizedMessage}"
        }
    }
}