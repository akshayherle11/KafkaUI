package com.kafkaui.Utils

import java.text.DateFormat
import java.text.SimpleDateFormat

fun formatTimestamp(ts:Long): String {
    try {
        val df =SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
       return df.format(ts)
    }
    catch (e : Exception)
    {
        return ""
    }
}