package com.musicai.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musicai.app.viewmodel.AiAssistantViewModel

@Composable
fun AiAssistantScreen(vm: AiAssistantViewModel = viewModel()) {

    val st by vm.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text("AI Assistant", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = st.theme, onValueChange = vm::updateTheme,
            label = { Text("Theme (topic)") }, modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = st.mood, onValueChange = vm::updateMood,
                label = { Text("Mood") }, modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = st.style, onValueChange = vm::updateStyle,
                label = { Text("Style") }, modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = st.key, onValueChange = vm::updateKey,
                label = { Text("Key (e.g., C, G, A#)") }, modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = st.mode, onValueChange = vm::updateMode,
                label = { Text("Mode (major/minor)") }, modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = vm::generateAll, enabled = !st.loading
            ) { Text(if (st.loading) "Generating..." else "Generate") }

            OutlinedButton(onClick = vm::clearOutputs, enabled = !st.loading) { Text("Clear") }
        }

        st.error?.let {
            Text("Error: $it", color = MaterialTheme.colorScheme.error)
        }

        Divider(Modifier.padding(vertical = 8.dp))

        Text("Chords (4 bars)", style = MaterialTheme.typography.titleMedium)
        if (st.chords.isEmpty()) {
            Text("No chords yet.")
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                st.chords.forEach { c ->
                    AssistChip(onClick = {}, label = { Text(c) })
                }
            }
        }

        Divider(Modifier.padding(vertical = 8.dp))

        Text("Lyrics", style = MaterialTheme.typography.titleMedium)
        if (st.lyrics.isBlank()) {
            Text("No lyrics yet.")
        } else {
            Surface(
                tonalElevation = 2.dp,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = st.lyrics,
                    modifier = Modifier.padding(12.dp),
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
