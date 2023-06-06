package cn.tangsu99.kookBot.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class JsonUtil {
    public static final Gson gson = new GsonBuilder().create();

    public static JsonObject getJsonObject(String msg) {
        return gson.fromJson(msg, JsonObject.class);
    }
}
