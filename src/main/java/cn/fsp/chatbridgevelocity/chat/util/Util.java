package cn.fsp.chatbridgevelocity.chat.util;

import cn.fsp.chatbridgevelocity.chat.message.Message;
import cn.fsp.chatbridgevelocity.chat.qq.QQChat;

public class Util {
    public static void chatSync(String command, boolean permission, QQChat qqChat, Message message){
        if (command.startsWith("!!chatSync") || (command.startsWith("!!chatsync"))) {
            if (!permission) {
                qqChat.sendMessage(message.getNoPermission(), "sync");
                return;
            }
            String cmd = command.substring(10).trim();
            if (cmd.equals("on")) {
                if (qqChat.getSync()) {
                    qqChat.sendMessage(message.getOnState(), "sync");
                    return;
                }
                qqChat.setSync(true);
                qqChat.sendMessage(message.getOn(), "on");
                return;
            }
            if (cmd.equals("off")) {
                if (!qqChat.getSync()) {
                    qqChat.sendMessage(message.getOffState(), "sync");
                    return;
                }
                qqChat.setSync(false);
                qqChat.sendMessage(message.getOff(), "off");
                return;
            }
            qqChat.sendMessage("Chat sync help\n!!chatSync on/off\n!!chatsync on/off", "sync");
        }
    }
}
