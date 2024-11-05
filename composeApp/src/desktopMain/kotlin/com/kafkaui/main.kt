package com.kafkaui

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.json.JSONObject
import util.JsonUtil

fun main() = application {
    val state = rememberWindowState(
        position = WindowPosition(Alignment.Center), size = DpSize(1280.dp, 768.dp)
    )


    Window(
        state =state,
        onCloseRequest = ::exitApplication,
        title = "KafkaUI",
    ) {
//        MenuBar{
//            Menu("File", mnemonic = 'F') {
//                Item("HEHE", onClick = {  }, shortcut = KeyShortcut(Key.C, ctrl = true))
//            }
//        }
        App()
    }
}