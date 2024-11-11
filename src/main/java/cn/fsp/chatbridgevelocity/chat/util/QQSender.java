package cn.fsp.chatbridgevelocity.chat.util;

public class QQSender {
    String nick;
    String name;
    String role;
    String qqID;
    public QQSender(String nick, String name, String role, String qqID) {
        this.name = name;
        this.nick = nick;
        this.role = nick;
        this.qqID = qqID;
    }

    public String getName() {
        return name;
    }

    public String getNick() {
        return nick;
    }

    public String getQqID() {
        return qqID;
    }

    public String getRole() {
        return role;
    }
}
