package cn.fsp.chatbridgevelocity.chat.kook.signaling;

import com.google.gson.JsonObject;

public class Hello {
    public static String hello(JsonObject d) {
        if (d.get("code").getAsInt() != 0) {
            return null;
        }
        return d.get("sessionId").getAsString();
    }
}
