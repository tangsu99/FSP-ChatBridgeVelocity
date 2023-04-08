package cn.fsp.chatbridgevelocity.chat.util;

import cn.fsp.chatbridgevelocity.chat.ChatForward;
import cn.fsp.chatbridgevelocity.chat.qq.GoCQHttpSendGroupMsg;
import cn.fsp.chatbridgevelocity.chat.qq.QQChat;
import com.google.gson.JsonObject;

public class GoCQHttpHandler extends Handler{
    public GoCQHttpHandler(ChatForward chatForward) {
        super(chatForward);
    }

    public void setQQChat(QQChat qqChat) {
        this.qqChat = qqChat;
    }

    @Override
    public void exec(String s) {
        JsonObject jsonObject = gson.fromJson(s, JsonObject.class);
        if (!jsonObject.has("message_type")) {
            return;
        }
        if (jsonObject.get("message_type").getAsString().equals("group")) {
            if (jsonObject.get("group_id").getAsString().equals(config.getQQGroup())) {
                String message = jsonObject.get("message").getAsString();
                JsonObject sender = jsonObject.get("sender").getAsJsonObject();
                String name = getName(sender);
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
        }
    }

    @Override
    public String send(String group, String msg) {
        return gson.toJson(new GoCQHttpSendGroupMsg(group, msg, "0"));
    }

    private String getName(JsonObject sender) {
        if (!sender.get("card").getAsString().equals("")) {
            return sender.get("card").getAsString();
        }
        return sender.get("nickname").getAsString();
    }
}
