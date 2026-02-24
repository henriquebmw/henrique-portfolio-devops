package com.musicai.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musicai.app.ui.chat.ChatUiMessage
import com.musicai.app.viewmodel.ChatViewModel

@Composable
fun ChatScreen(vm: ChatViewModel = viewModel()) {

    val messages by vm.messages.collectAsState()
    var input by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("AI Chat Assistant", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true // newest message at bottom
        ) {
            items(messages.reversed()) { msg -> ChatBubble(msg) }
        }

        Spacer(Modifier.height(12.dp))

        Row {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text("Ask MelodyMind…") },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    vm.sendMessage(input)
                    input = ""
                }
            ) { Text("Send") }
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(onClick = vm::clear) {
            Text("Clear Chat")
        }
    }
}

@Composable
fun ChatBubble(msg: ChatUiMessage) {
    val bg = if (msg.isUser) Color(0xFF6C5CE7) else Color(0xFF1DA1F2)
    val align = if (msg.isUser) Arrangement.End else Arrangement.Start

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = align
    ) {
        Surface(
            color = bg,
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 4.dp,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Text(
                msg.text,
                color = Color.White,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
