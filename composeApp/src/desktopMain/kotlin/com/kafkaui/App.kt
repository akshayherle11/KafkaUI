package com.kafkaui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kafkaui.UI.InputTextField
import com.kafkaui.UI.KafkaMessageView
import com.kafkaui.UI.KafkaTopicDesc
import com.kafkaui.UI.KafkaTopicsList
import com.kafkaui.ViewModel.KafkaViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App() {

    var kafakaVM = KafkaViewModel()
    val coroutineScope = rememberCoroutineScope()

    MaterialTheme {
        Box(modifier = Modifier.padding(10.dp)) {
        Column {
            Row{
                InputTextField(value = kafakaVM.brokers.value, onValueChange = {kafakaVM.brokers.value = it})
                Button(onClick = {
                  coroutineScope.launch {   kafakaVM.connect() }
                                 }, modifier = Modifier.height(30.dp))
                {
                    Text(fontSize = 14.sp, text="Connect")
                }
            }
            Row() {
                KafkaTopicsList(kafakaVM)
                Column(modifier = Modifier.fillMaxSize()) {
                    KafkaTopicDesc(kafakaVM)
                    KafkaMessageView(kafakaVM)
                }
            }
        }
        }
    }
}