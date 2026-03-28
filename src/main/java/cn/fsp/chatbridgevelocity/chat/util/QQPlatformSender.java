package cn.fsp.chatbridgevelocity.chat.util;

import cn.fsp.chatbridgevelocity.chat.qq.QQChat;

/**
 * QQ平台发送者
 */
public class QQPlatformSender implements PlatformSender {
    private final QQChat qqChat;
    private final String group;

    public QQPlatformSender(QQChat qqChat, String group) {
        this.qqChat = qqChat;
        this.group = group;
    }

    @Override
    public void reply(String message) {
        qqChat.sendMessage(message, "reply");
    }
}
