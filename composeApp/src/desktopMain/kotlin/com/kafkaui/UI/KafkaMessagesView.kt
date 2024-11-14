package com.kafkaui.UI

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kafkaui.Utils.beautifyJson
import com.kafkaui.Utils.formatTimestamp
import com.kafkaui.ViewModel.KafkaViewModel
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.TableColumnWidth
import com.seanproctor.datatable.material3.PaginatedDataTable
import com.seanproctor.datatable.paging.rememberPaginatedDataTableState

@Composable
fun KafkaMessageView(vm: KafkaViewModel) {

    var selectedRow by remember { mutableStateOf<Int?>(null) }

    var pageState = rememberPaginatedDataTableState(10,0)
    Box(modifier = Modifier.padding(10.dp))
    {
        Column (modifier = Modifier.fillMaxSize()){
            if(vm.loadingMsg.value)
            {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize())                {
                    CircularProgressIndicator()
                }
            }
            else
            {
                if(vm.msgErrorText.value !=null)
                {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize())
                    {
                        Text(vm.msgErrorText.value!!)
                    }
                }
                else{
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Card(
                            modifier = Modifier.weight(2f),
                            shape = RoundedCornerShape(5.dp)
                        ){   PaginatedDataTable(
                            sortColumnIndex = 0,
                            sortAscending = true,
                            modifier = Modifier.fillMaxSize(),
                            rowHeight = 24.dp,
                            rowBackgroundColor = {Color.White},
                            headerBackgroundColor =  Color.LightGray,
                            footerBackgroundColor =  Color.LightGray,
                            columns = listOf(
                                DataColumn(width = TableColumnWidth.Fraction(0.15f)) {
                                    Text("Offset")
                                },
                                DataColumn(width = TableColumnWidth.Fraction(0.10f)) {
                                    Text("Partition")
                                },
                                DataColumn(width = TableColumnWidth.Fraction(0.55f)) {
                                    Text("Data")
                                },
                                DataColumn(width = TableColumnWidth.Fraction(0.20f)) {
                                    Text("Timestamp")
                                },
                            ),
                            state =pageState,
                            ) {

                            vm.msg.forEachIndexed { index, it ->
                                row {
                                    cell { Text(it?.offset.toString()) }
                                    cell { Text("1") }
                                    cell {
                                        Box(modifier = Modifier.fillMaxWidth().clickable {
                                            selectedRow = index;
                                        })
                                        {
                                            Text(
                                                maxLines = 1, overflow = TextOverflow.Ellipsis,
                                                text = it!!.msg.replace("\n", "")
                                                //text="Aaa"
                                            )
                                        }
                                    }
                                    cell {
                                        Text(formatTimestamp(it!!.ts))
                                    }
                                }
                            }
                        }
                        }

                            if (selectedRow != null && selectedRow!! < vm.msg.size) {
                                MessageView(vm.msg.get(selectedRow!!)!!.msg,Modifier.weight(1f))
                            } else {
                                MessageView("Select Msg",Modifier.weight(1f))
                            }
                        }

                    }
            }
        }
    }
}

@Composable
fun MessageView(txt : String, modifier: Modifier)
{
    var beautifyJson by remember { mutableStateOf(true) }
    Card(modifier = modifier) {
        Column( )
        {
           Box(modifier = Modifier.height(26.dp).fillMaxWidth().background(Color.LightGray).padding(start = 5.dp))
           {
             Row(modifier=Modifier.fillMaxWidth())
             {
                 Text(fontSize = 16.sp, text ="Message",)
                 Spacer(Modifier.weight(1f))
                 Text("Beautify JSON ")
                 Switch(checked = beautifyJson, onCheckedChange = { beautifyJson=!beautifyJson})
             }
           }
            Box(
                modifier = Modifier.weight(1f)
            ) {

                val stateVertical = rememberScrollState(0)
                val stateHorizontal = rememberScrollState(0)

                InputTextField(modifier = Modifier.fillMaxHeight().fillMaxWidth().verticalScroll(stateVertical).horizontalScroll(stateHorizontal)
                    .background(Color.LightGray.copy(alpha = 0.1f)),
                    value = if(beautifyJson) beautifyJson(txt) else  txt, onValueChange = {},
                    shape = RectangleShape,
                    singleLine = false)
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd)
                        .fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(stateVertical)
                )
                HorizontalScrollbar(
                    modifier = Modifier.align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(end = 12.dp),
                    adapter = rememberScrollbarAdapter(stateHorizontal)
                )
            }
        }
}

}