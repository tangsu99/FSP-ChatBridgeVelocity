package cn.fsp.chatbridgevelocity.chat;

import cn.fsp.chatbridgevelocity.ChatBridgeVelocity;

public class Status {
    public static boolean ChatForwardStatus = false;
    public static boolean qqChatStatus = false;
    public static boolean kookChatStatus = false;

    public static void init() {
        ChatForwardStatus = ChatBridgeVelocity.config.ChatForwardEnabled();
        qqChatStatus = ChatBridgeVelocity.config.getQQChatEnabled();
        kookChatStatus = ChatBridgeVelocity.config.getKookEnabled();
    }

    public static String isOnline() {
        return  "Chat: " + tf(ChatForwardStatus) + "\nQQ: " + tf(qqChatStatus) + "\nKook: " + tf(kookChatStatus);
    }

    public static String tf(boolean b) {
        return (b ? "在线" : "离线");
    }
}
