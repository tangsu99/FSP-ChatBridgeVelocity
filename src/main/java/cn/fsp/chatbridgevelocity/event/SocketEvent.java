package cn.fsp.chatbridgevelocity.event;

public class SocketEvent {
    private final int status;
    private final String serverName;
    public SocketEvent(int status, String serverName) {
        this.status = status;
        this.serverName = serverName;
    }

    public int getStatus() {
        return status;
    }

    public String getServerName() {
        return serverName;
    }
}
