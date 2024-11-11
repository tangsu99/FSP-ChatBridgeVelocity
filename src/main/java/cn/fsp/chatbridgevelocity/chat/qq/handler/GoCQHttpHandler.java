package cn.fsp.chatbridgevelocity.chat.qq.handler;

import cn.fsp.chatbridgevelocity.chat.ChatForward;
import cn.fsp.chatbridgevelocity.chat.Command;
import cn.fsp.chatbridgevelocity.chat.Status;
import cn.fsp.chatbridgevelocity.chat.qq.GoCQHttpSendGroupMsg;
import cn.fsp.chatbridgevelocity.chat.qq.QQChat;
import cn.fsp.chatbridgevelocity.chat.util.QQSender;
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
                if (message.startsWith(config.getQQRespondPrefix()) || qqChat.getSync()) {
                    String msg;
                    if (qqChat.getSync()) {
                        msg = message + "    [chatSync]";
                    }else {
                        msg = message.substring(4).trim();
                    }
                    chatForward.allPlayerSendMessage(name, msg);
                    return;
                }
                sendEvent(jsonObject.get("group_id").getAsString(), new QQSender(name, name, sender.get("role").getAsString(), ""), message);
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
//                if (message.equals("!!online")) {
//                    qqChat.sendMessage(chatForward.getOnline().toString(), "online");
//                    return;
//                }
//                if (message.equals("!!ping")) {
//                    qqChat.sendMessage("pong!!", "pong");
//                    return;
//                }
//                if (message.equals("!!help")) {
//                    qqChat.sendMessage("FSP-ChatBridgeVelocity\n!!help\t显示此信息\n!!mc\t发送信息到mc\n!!chatSync on/off\t聊天同步\n!!online\t显示在线玩家\n!!ping\tpong!!", "help");
//                    return;
//                }
                boolean permission = hasPermission(sender.get("role").getAsString());
                Command.chatSync(message, permission, this.qqChat, this.message);
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

    private boolean hasPermission(String permission) {
        return permission.equals("admin") || permission.equals("owner");
    }
}
