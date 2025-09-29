package com.example.demo_chatbot

data class ChatMessageModel(
    val message: String,
    val isUser: Boolean
)

//
data class GeminiRequest(
    val contents: List<Content>
)

data class Content(
    val role: String,
    val parts: List<Part>
)

data class Part(
    val text: String
)

data class GeminiResponse(
    val candidates: List<Candidate>
)

data class Candidate(
    val content: Content
)


