package com.kafkaui.UI

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kafkaui.ViewModel.KafkaViewModel


@Composable
fun KafkaTopicsListView(vm: KafkaViewModel) {
    var selectedIndex by remember { mutableStateOf(-1) }
    Box(modifier = Modifier.width(200.dp).fillMaxHeight()) {
        Column {
            Text(modifier = Modifier.padding(1.dp), fontSize = 18.sp, text = "Topics")
            if (vm.topicLoading.value) {
                CircularProgressIndicator()
            } else if (!vm.connected.value) {
                Text("Not Connected")
            } else if (vm.topicsListError.value != null) {
                Text("Error  : " + vm.topicsListError.value)
            } else {
                Card(
                    modifier = Modifier.fillMaxHeight().padding(top = 5.dp, bottom = 5.dp),
                    shape = RoundedCornerShape(5.dp)
                ) {
                    LazyColumn(
                        userScrollEnabled = true,
                    ) {
                        itemsIndexed(vm.data) { index, value ->
                            RowItem(
                                value,
                                if (index == selectedIndex) Color.LightGray else Color.Transparent,
                                onClick = {
                                    if (selectedIndex != index) {
                                        selectedIndex = index
                                        vm.selectTopic(selectedIndex)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun RowItem(text: String, selectedColor: Color, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth()
            .background(color = selectedColor)
            .clickable {
                onClick()
            }) {
        Text(
            maxLines = 1,
            modifier = Modifier.padding(start = 5.dp, end = 5.dp),
            text = text,
            fontSize = 16.sp
        )
    }
}