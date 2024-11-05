package com.kafkaui.UI

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kafkaui.Utils.formatTimestamp
import com.kafkaui.ViewModel.KafkaViewModel
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.TableColumnWidth
import com.seanproctor.datatable.material3.PaginatedDataTable
import com.seanproctor.datatable.paging.rememberPaginatedDataTableState
import org.jetbrains.skia.paragraph.TextBox

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
                                    cell { Text(it!!.offset().toString()) }
                                    cell { Text(it!!.partition().toString()) }
                                    cell {
                                        Box(modifier = Modifier.fillMaxWidth().clickable {
                                            selectedRow = index;
                                            println(it!!.value().toString())
                                        })
                                        {
                                            Text(
                                                maxLines = 1, overflow = TextOverflow.Ellipsis,
                                                text = it!!.value().toString().replace("\n", "")
                                                //text="Aaa"
                                            )
                                        }
                                    }
                                    cell {
                                        Text(formatTimestamp(it!!.timestamp()))
                                    }
                                }
                            }
                        }
                        }

                            if (selectedRow != null && selectedRow!! < vm.msg.size) {
                                messageView(vm.msg.get(selectedRow!!)?.value().toString(),Modifier.weight(1f))
                            } else {
                                messageView("Select Msg",Modifier.weight(1f))
                            }
                        }

                    }
            }
        }
    }
}

@Composable
fun messageView(txt : String,modifier: Modifier)
{
    Card(modifier = modifier) {
        Column( )
        {
           Box(modifier = Modifier.height(26.dp).fillMaxWidth().background(Color.LightGray).padding(start = 5.dp))
           {
               Text(fontSize = 16.sp, text ="Message",)
           }
            Box(
                modifier = Modifier.weight(1f)
            ) {

                val stateVertical = rememberScrollState(0)
                val stateHorizontal = rememberScrollState(0)

                InputTextField(modifier = Modifier.fillMaxHeight().fillMaxWidth().verticalScroll(stateVertical).horizontalScroll(stateHorizontal),
                    value = txt, onValueChange = {},
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