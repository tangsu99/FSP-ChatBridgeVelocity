package cn.fsp.chatbridgevelocity.event;

public class KookMessageEvent {
    private final String server;
    private final String channel;
    private final String sender;
    private final String message;

    public KookMessageEvent(String server, String channel, String sender, String message) {
        this.server = server;
        this.channel = channel;
        this.sender = sender;
        this.message = message;
    }

    public String getServer() {
        return server;
    }

    public String getChannel() {
        return channel;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }
}
