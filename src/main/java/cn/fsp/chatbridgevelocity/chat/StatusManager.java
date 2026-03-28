package cn.fsp.chatbridgevelocity.chat;

public class StatusManager {
    private boolean chatForwardEnabled = false;
    private boolean qqChatEnabled = false;
    private boolean kookChatEnabled = false;

    public void setChatForwardEnabled(boolean enabled) {
        this.chatForwardEnabled = enabled;
    }

    public boolean isChatForwardEnabled() {
        return chatForwardEnabled;
    }

    public void setQqChatEnabled(boolean enabled) {
        this.qqChatEnabled = enabled;
    }

    public boolean isQqChatEnabled() {
        return qqChatEnabled;
    }

    public void setKookChatEnabled(boolean enabled) {
        this.kookChatEnabled = enabled;
    }

    public boolean isKookChatEnabled() {
        return kookChatEnabled;
    }

    public String getStatusString() {
        return "Chat: " + (chatForwardEnabled ? "在线" : "离线") + "\n" +
               "QQ: " + (qqChatEnabled ? "在线" : "离线") + "\n" +
               "Kook: " + (kookChatEnabled ? "在线" : "离线");
    }
}