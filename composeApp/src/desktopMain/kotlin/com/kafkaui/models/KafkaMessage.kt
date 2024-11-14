package com.kafkaui.models

data class KafkaMessage(var msg:String, var offset : Long, var ts : Long)
