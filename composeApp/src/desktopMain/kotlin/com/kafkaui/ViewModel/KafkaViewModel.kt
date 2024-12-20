package com.kafkaui.ViewModel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kafkaui.models.JsonFilter
import com.kafkaui.models.KafkaMessage
import kafka.KafkaAdminClient
import kafka.KafkaConsumerClient
import kafka.KafkaFilterOption
import kafka.KafkaUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kql.KQL
import org.apache.kafka.clients.admin.TopicDescription
import org.apache.kafka.clients.consumer.ConsumerRecord

class KafkaViewModel: ViewModel() {
    var selectedIndex = mutableStateOf(-1)
    var data = mutableListOf<String>()
    var topicLoading = mutableStateOf(false)
    var loadingTopicDesc = mutableStateOf(false)

    var selectedTopicsDesc = mutableStateOf<TopicDescription?>(null)
    var selectedTopic = mutableStateOf("");

    var client: KafkaAdminClient? = null ;
    var consumerClient : KafkaConsumerClient? =  null;

    var topicsListError = mutableStateOf<String?>(null);


    var loadingMsg = mutableStateOf(false);


    var msg = mutableStateListOf<KafkaMessage?>()

    var msgErrorText = mutableStateOf<String?>("Select Topic to check the messages");



    //search

    var searchText = mutableStateOf("")
    var jsonSearch = mutableStateOf(false)
    var noOfMsg = mutableStateOf(100L)
    var jsonFilters = mutableStateListOf<JsonFilter>()

    var showSearchDialog =  mutableStateOf(false)


    var brokers = mutableStateOf("localhost:29092");

    var connected = mutableStateOf(false);



    suspend fun connect(){
        withContext(Dispatchers.IO) {
            connected.value = false;
            topicLoading.value = true
            try {
                client =  KafkaAdminClient(brokers.value,"grpID");
                client!!.connectClient()
                data = client!!.allTopics
                topicsListError.value = null
             consumerClient =    KafkaConsumerClient(brokers.value,"grpID");
            }
            catch (e : Exception)
            {
                topicsListError.value = "Error Check the borkers!!"
            }finally {
                topicLoading.value = false
                connected.value = true;
            }
        }
    }




    suspend fun searchMessage( query:String,noMSg:String)
    {
        loadingMsg.value  =true
        msg.clear()
        withContext(Dispatchers.IO) {
            try {
                msg.addAll(  KQL(selectedTopic.value,query).executeQuery(consumerClient, Integer.parseInt(noMSg), selectedTopicsDesc.value!!.partitions()))

                msgErrorText.value =null

                if(msg.size==0)
                {
                    msgErrorText.value = "No messages found";
                }

            } catch (e: Exception) {
                msgErrorText.value = "Error Loading Meassages! "+e.message
            } finally {
                  loadingMsg.value  =false

            }
        }
    }


    fun addFilter(path:String, type :  String,  value : String)
    {
        jsonFilters.add(JsonFilter(path,type,value))

    }
    fun updateFilterPath(filter: JsonFilter,path:String)
    {
        jsonFilters[  jsonFilters.indexOf(filter)]=filter.copy(path=path)
        println(jsonFilters)
    }
    fun updateFilterValue(filter: JsonFilter,value:String)
    {
        jsonFilters[  jsonFilters.indexOf(filter)]=filter.copy(value=value)
        println(jsonFilters)
    }
    fun updateFilterType(filter: JsonFilter,type:String)
    {
        jsonFilters[  jsonFilters.indexOf(filter)]=filter.copy(type=type)
        println(jsonFilters)
    }

    fun removeJsonFilter(filter: JsonFilter)
    {
        jsonFilters.remove(filter)
    }

    fun selectTopic(index : Int)
    {
        loadingTopicDesc.value = true
        selectedIndex.value = index;
        selectedTopic.value = data.get(index)
        viewModelScope.launch {
            selectedTopicsDesc.value = client?.describeSingleTopic(selectedTopic.value)
        }
        loadingTopicDesc.value = false
        msg.clear()
        msgErrorText.value="Click On The Search Icon to load the msg";
    }
}