package cn.fsp.chatbridgevelocity.chat.platform;

import cn.fsp.chatbridgevelocity.chat.qq.QQChat;

public class QQPlatform implements ChatPlatform {
    private final QQChat qqChat;

    public QQPlatform(QQChat qqChat) {
        this.qqChat = qqChat;
    }

    @Override
    public void connect() {
        qqChat.connect();
    }

    @Override
    public void disconnect() {
        qqChat.close();
    }

    @Override
    public void sendMessage(String message, String echo) {
        qqChat.sendMessage(message, echo);
    }

    @Override
    public boolean isConnected() {
        return qqChat.isOpen();
    }

    @Override
    public void setSync(boolean sync) {
        qqChat.setSync(sync);
    }

    @Override
    public boolean getSync() {
        return qqChat.getSync();
    }
}
