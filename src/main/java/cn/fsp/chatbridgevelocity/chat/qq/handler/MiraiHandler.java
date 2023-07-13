package cn.fsp.chatbridgevelocity.chat.qq.handler;

import cn.fsp.chatbridgevelocity.chat.ChatForward;
import cn.fsp.chatbridgevelocity.chat.Command;
import cn.fsp.chatbridgevelocity.chat.Status;
import cn.fsp.chatbridgevelocity.chat.qq.MiraiSendGroupMsg;
import cn.fsp.chatbridgevelocity.chat.qq.QQChat;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
        boolean permission = hasPermission(sender.get("permission").getAsString());
        String name = sender.get("memberName").getAsString();
        if (!group.equals(config.getQQGroup())) {
            return;
        }
        if (message.startsWith(config.getQQRespondPrefix()) || qqChat.getSync()) {
            String msg;
            if (qqChat.getSync()) {
                msg = message + "    [chatSync]";
            }else {
                msg = message.substring(4).trim();
            }
            chatForward.allPlayerSendMessage(name, msg);
        }
        switch (message) {
            case "!!online":
                qqChat.sendMessage(chatForward.getOnline().toString(), "online");
                break;
            case "!!ping":
                qqChat.sendMessage("pong!!", "pong");
                break;
            case "!!help":
                qqChat.sendMessage("FSP-ChatBridgeVelocity\n!!help\t显示此信息\n!!mc\t发送信息到mc\n!!chatSync on/off\t聊天同步\n!!online\t显示在线玩家\n!!ping\tpong!!", "help");
                break;
            case "status":
                qqChat.sendMessage(Status.isOnline(),"status");
                break;
        }
//        if (message.equals("!!online")) {
//            qqChat.sendMessage(chatForward.getOnline().toString(), "online");
//        }
//        if (message.equals("!!ping")) {
//            qqChat.sendMessage("pong!!", "pong");
//        }
//        if (message.equals("!!help")) {
//            qqChat.sendMessage("FSP-ChatBridgeVelocity\n!!help\t显示此信息\n!!mc\t发送信息到mc\n!!chatSync on/off\t聊天同步\n!!online\t显示在线玩家\n!!ping\tpong!!", "help");
//        }
        Command.chatSync(message, permission, this.qqChat, this.message);
    }

    @Override
    public String send(String group, String msg) {
        return gson.toJson(miraiSendGroupMsg.setMsg(group, msg, sessionKey));
    }

    private boolean hasPermission(String permission) {
        return permission.equals("ADMINISTRATOR") || permission.equals("OWNER");
    }
}
