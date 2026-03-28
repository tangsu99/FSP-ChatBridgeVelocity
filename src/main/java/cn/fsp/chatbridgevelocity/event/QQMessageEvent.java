package cn.fsp.chatbridgevelocity.event;

import cn.fsp.chatbridgevelocity.chat.util.QQSender;

public class QQMessageEvent {
    private final String group;
    private final QQSender sender;
    private final String message;

    public QQMessageEvent(String group, QQSender sender, String message) {
        this.group = group;
        this.sender = sender;
        this.message = message;
    }

    public String getGroup() {
        return group;
    }

    public QQSender getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }
}
