package cn.fsp.chatbridgevelocity.chat.platform;

public interface ChatPlatform {
    void connect();
    void disconnect();
    void sendMessage(String message, String echo);
    boolean isConnected();
    void setSync(boolean sync);
    boolean getSync();
}
