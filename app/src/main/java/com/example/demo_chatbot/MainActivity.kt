package com.example.demo_chatbot// com.example.demo_chatbot.MainActivity.kt
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val uiScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private lateinit var geminiHelper: GeminiHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var inputText: EditText
    private lateinit var sendButton: ImageView

    // views / adapter (giả sử bạn đã có ChatAdapter & ChatMessageModel)
    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<ChatMessageModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        inputText = findViewById(R.id.inputText)
        sendButton = findViewById(R.id.sendButton)

        geminiHelper = GeminiHelper(this.applicationContext)

        // setup recycler view & adapter (giữ như bạn đã làm)
        chatAdapter = ChatAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = chatAdapter

        sendButton.setOnClickListener {
            val userText = inputText.text.toString().trim()
            if (userText.isNotEmpty()) {
                addMessage(userText, isUser = true)
                inputText.text.clear()
                // show typing placeholder
                addMessage("Typing...", isUser = false, isPlaceholder = true)

                // gọi Gemini
                uiScope.launch {
                    val reply = geminiHelper.generateText(userText)
                    // remove typing placeholder (cuối danh sách)
                    removePlaceholderIfAny()
                    addMessage(reply, isUser = false)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel()
    }

    private fun addMessage(text: String, isUser: Boolean, isPlaceholder: Boolean = false) {
        // ChatMessageModel có thể thêm thuộc tính isPlaceholder nếu cần; đơn giản add & notify
        messages.add(ChatMessageModel(text, isUser))
        chatAdapter.notifyItemInserted(messages.size - 1)
        recyclerView.scrollToPosition(messages.size - 1)
    }

    private fun removePlaceholderIfAny() {
        if (messages.isNotEmpty()) {
            val last = messages.last()
            if (!last.isUser && last.message == "Typing...") {
                val idx = messages.size - 1
                messages.removeAt(idx)
                chatAdapter.notifyItemRemoved(idx)
            }
        }
    }
}
