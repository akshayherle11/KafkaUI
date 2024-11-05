package com.kafkaui.UI

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kafkaui.ViewModel.KafkaViewModel
import com.kafkaui.models.JsonFilter
import kotlinx.coroutines.launch


@Composable
fun KafkaTopicDesc(vm: KafkaViewModel)
{



    if(vm.showSearchDialog.value)
    {
       searchDialog(vm)
    }

    Box(modifier = Modifier.padding(10.dp))
    {
        Column {
            Text(modifier = Modifier.padding(1.dp), fontSize = 16.sp, text = "Topic Info")
            if(vm.selectedTopicsDesc.value!=null)
            {
               Card (modifier = Modifier.fillMaxWidth()){
                   Row (modifier = Modifier.padding(3.dp).fillMaxWidth(),verticalAlignment = Alignment.CenterVertically,
                       horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                       Text( fontSize = 14.sp,text = "Topic Name  : " + vm.selectedTopicsDesc.value!!.name())
                       Text(fontSize = 14.sp,text ="Partitions : " + vm.selectedTopicsDesc.value!!.partitions().size)
                       Spacer(Modifier.weight(1f))
                       IconButton(onClick = {
                           vm.showSearchDialog.value = true
                       }){
                           Icon(Icons.Default.Search, "Search")
                       }
                   }
               }
            }
            else
            {
                if(vm.loadingTopicDesc.value)
                {
                    CircularProgressIndicator()
                }
                else {
                    Text("No Topic Selected!")
                }
            }
        }


    }
}


@Composable
fun searchDialog(vm: KafkaViewModel)
{

    val coroutineScope = rememberCoroutineScope()

    Dialog(
        onDismissRequest = {vm.showSearchDialog.value = false
                           },
    )
    {
        Surface(shape = RoundedCornerShape(5.dp),
        ) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Search")
                Spacer(modifier = Modifier.size(5.dp))
                HorizontalDivider(thickness = 1.dp)
                //
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp,Alignment.CenterHorizontally), verticalAlignment = Alignment.CenterVertically)
                {
                    OutlinedTextField(
                        modifier = Modifier.height(60.dp).width(150.dp),
                        textStyle = TextStyle(fontSize = 14.sp),
                        value = vm.noOfMsg.value.toString(),
                        onValueChange = { vm.noOfMsg.value= it.toInt() },
                        label = { Text("No Of Message")
                        },
                        keyboardOptions = KeyboardOptions(keyboardType =  KeyboardType.Number)
                    )
                    OutlinedTextField(
                        enabled = !vm.jsonSearch.value,
                        modifier = Modifier.height(60.dp),
                        textStyle = TextStyle(fontSize = 14.sp),
                        value = vm.searchText.value,
                        onValueChange = { vm.searchText.value = it },
                        label = { Text("Search Text")
                        }
                    )
                }

                if(vm.jsonSearch.value)
                {

                    Text("Advanced Search")

                    jsonSearchView(vm)
                }


                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Text("JSON Search  : ")
                    Switch(checked = vm.jsonSearch.value, onCheckedChange = {vm.jsonSearch.value = it})
                    Spacer(Modifier.width(10.dp))
                    IconButton(
                        enabled = vm.jsonSearch.value,
                        onClick = {
                            vm.addFilter("Path","String","value");
                    }){
                        Icon(Icons.Default.Add, "Add", tint = Color.Black)
                    }
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = {
                        vm.showSearchDialog.value = false
                      coroutineScope.launch{
                          vm.searchMessage()
                      }
                    }
                    )
                    {
                        Text("Search")
                    }

                }

            }
        }
    }
}


@Composable
fun jsonSearchView(vm : KafkaViewModel)
{
    Column(modifier = Modifier.width(500.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp,Alignment.CenterHorizontally)) {
            Text(  modifier = Modifier.height(36.dp).width(150.dp), text = "Json Path")
            Text(  modifier = Modifier.height(36.dp).width(130.dp), text = "Data Type")
            Text(  modifier = Modifier.height(36.dp).width(150.dp), text = "Value")
        }

        LazyColumn {
            itemsIndexed(vm.jsonFilters) { idndex, it ->
                jsonRow(vm,it)
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun jsonRow(vm: KafkaViewModel, filter: JsonFilter)
{

    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp,Alignment.CenterHorizontally)) {
        InputTextField(
            value = filter.path,
            onValueChange = { vm.updateFilterPath(filter,it) },
            modifier = Modifier.height(36.dp).width(150.dp),
        )
        DropMenu(listOf("String","Number","Decimal","Bool"),filter.type, onClick = {
            txt ->         vm.updateFilterType(filter,txt)
        })
        InputTextField(
            value = filter.value,
            onValueChange = { vm.updateFilterValue(filter,it) },
            modifier = Modifier.height(36.dp).width(150.dp),
        )
        IconButton(onClick = {
            vm.removeJsonFilter(filter)
        }){
            Icon(Icons.Default.Remove, "Delete", tint = Color.Red)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun InputTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine :Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,

    ) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        interactionSource = interactionSource,
        singleLine = singleLine,
    ) { innerTextField ->

        TextFieldDefaults.TextFieldDecorationBox(
            value = value,
            visualTransformation = visualTransformation,
            innerTextField = innerTextField,
            singleLine = singleLine,
            enabled = true,
            interactionSource = interactionSource,
            contentPadding = PaddingValues(5.dp), // this is how you can remove the padding
            shape = RoundedCornerShape(5.dp)
        )

    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DropMenu(items: List<String>,selectedTxt : String,onClick: (txt :String)-> Unit){
    var expanded by remember { mutableStateOf(false) }


    Box(modifier = Modifier.width(130.dp).
        height(36.dp).
        clip(RoundedCornerShape(5.dp)).
    clickable(onClick = { expanded = true }).
    background( MaterialTheme.colors.onSurface.copy(alpha = TextFieldDefaults.BackgroundOpacity))
        .padding(5.dp),
        contentAlignment = Alignment.Center) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()){
            Text(selectedTxt)
            Icon(Icons.Default.ArrowDropDown, "DropDown")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = {expanded = false}){
            items.forEach{
                DropdownMenuItem(onClick = {

                    expanded = false
                    onClick(it)
                })
                {
                    Text(it)
                }
            }
        }
    }


}


