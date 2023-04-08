package cn.fsp.chatbridgevelocity.chat.util;

import cn.fsp.chatbridgevelocity.chat.ChatForward;
import cn.fsp.chatbridgevelocity.chat.qq.MiraiSendGroupMsg;
import cn.fsp.chatbridgevelocity.chat.qq.QQChat;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class MiraiHandler extends Handler{
    private MiraiSendGroupMsg miraiSendGroupMsg;
    private String sessionKey;
    public MiraiHandler(ChatForward chatForward) {
        super(chatForward);
        miraiSendGroupMsg = new MiraiSendGroupMsg(config.getQQGroup(), "awa");
    }

    @Override
    public void setQQChat(QQChat qqChat) {
        this.qqChat = qqChat;
    }

    @Override
    public void exec(String s) {
        JsonObject jsonObject = gson.fromJson(s, JsonObject.class);
        if (!jsonObject.has("data")) {
            return;
        }

        JsonObject data = jsonObject.get("data").getAsJsonObject();
        if (data.has("session")){
            sessionKey = data.get("session").getAsString();
            return;
        }
        if (!data.has("type")) {
            return;
        }
        if (!data.get("type").getAsString().equals("GroupMessage")) {
            logger.info(data.get("type").getAsString());
            return;
        }
        JsonArray messageChain = data.getAsJsonArray("messageChain");
        String message = null;
        for (JsonElement jsonElement : messageChain) {
            if (jsonElement.getAsJsonObject().get("type").getAsString().equals("Plain")) {
                message = jsonElement.getAsJsonObject().get("text").getAsString();
                break;
            }
        }
        if (message == null) {
            return;
        }
        JsonObject sender = data.get("sender").getAsJsonObject();
        String group = sender.get("group").getAsJsonObject().get("id").getAsString();
        String name = sender.get("memberName").getAsString();
        if (!group.equals(config.getQQGroup())) {
            return;
        }
        if (message.startsWith(config.getQQRespondPrefix())) {
            String msg = message.substring(4, message.length()).trim();
            chatForward.allPlayerSendMessage(name, msg);
        }
        if (message.equals("!!online")) {
            qqChat.sendMessage(chatForward.getOnline().toString(), "online");
        }
        if (message.equals("!!ping")) {
            qqChat.sendMessage("pong!!", "pong");
        }
        if (message.equals("!!help")) {
            qqChat.sendMessage("FSP-ChatBridgeVelocity\n!!help\t显示此信息\n!!mc\t发送信息到mc\n!!ping\tpong!!", "help");
        }
    }

    @Override
    public String send(String group, String msg) {
        return gson.toJson(miraiSendGroupMsg.setMsg(group, msg, sessionKey));
    }
}
