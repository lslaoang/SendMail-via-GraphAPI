package com.lao.mailgraph.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RequestUtil {
    public static String objectAsString(Object object) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(object);
    }
}
