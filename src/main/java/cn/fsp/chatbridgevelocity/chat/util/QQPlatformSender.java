package cn.fsp.chatbridgevelocity.chat.util;

import cn.fsp.chatbridgevelocity.chat.qq.QQChat;

/**
 * QQ平台发送者
 */
public class QQPlatformSender implements PlatformSender {
    private final QQChat qqChat;
    private final String group;
    private QQSender MsgSender;

    public QQPlatformSender(QQChat qqChat, String group) {
        this.qqChat = qqChat;
        this.group = group;
    }

    @Override
    public void reply(String message) {
        qqChat.sendMessage(message, "reply");
    }

    @Override
    public PlatformSender getSender() {
        return this;
    }

    public String getGroup() {
        return group;
    }

    public QQChat getQQChat() {
        return qqChat;
    }

    public QQPlatformSender setMessageSender(QQSender sender) {
        this.MsgSender = sender;
        return this;
    }

    public QQSender getMsgSender() {
        return MsgSender;
    }
}
