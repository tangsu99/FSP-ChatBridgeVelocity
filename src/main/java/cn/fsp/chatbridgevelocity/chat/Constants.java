package cn.fsp.chatbridgevelocity.chat;

/**
 * 常量定义类
 * 集中管理插件中使用的各种魔数和常量
 */
public class Constants {
    // 服务器状态代码
    public static final int SERVER_STARTED_STATUS = 49;
    public static final int SERVER_STOPPED_STATUS = 48;

    // QQ命令前缀
    public static final String CMD_PREFIX_CHAT_SYNC = "!!chatSync";
    public static final String CMD_PREFIX_CHAT_SYNC_LOWER = "!!chatsync";
    public static final String CMD_ONLINE = "!!online";
    public static final String CMD_PING = "!!ping";
    public static final String CMD_HELP = "!!help";
    public static final String CMD_STATUS = "!!status";

    // QQ权限
    public static final String PERMISSION_ADMIN = "ADMINISTRATOR";
    public static final String PERMISSION_OWNER = "OWNER";
    public static final String PERMISSION_ADMIN_ONEBOT11 = "admin";
    public static final String PERMISSION_OWNER_ONEBOT11 = "owner";

    // OneBot11权限检查
    public static boolean isOneBot11Admin(String role) {
        return PERMISSION_ADMIN_ONEBOT11.equals(role) || PERMISSION_OWNER_ONEBOT11.equals(role);
    }

    // 聊天同步标志
    public static final String CHAT_SYNC_FLAG = "[chatSync]";

    // 消息来源标记
    public static final String SOURCE_QQ = "[QQ]";
    public static final String SOURCE_KOOK = "[KOOK]";
    public static final String SOURCE_VELOCITY = "[Velocity]";
}

