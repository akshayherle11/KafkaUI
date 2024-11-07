package com.kafkaui.Utils

import org.json.JSONObject


fun beautifyJson(txt:String): String {
    try{
        var json = JSONObject(txt);
        return json.toString(4);
    }
    catch (e:Exception)
    {

    }
    return txt;
}