package cn.fsp.chatbridgevelocity.chat.qq.command;

import cn.fsp.chatbridgevelocity.chat.Constants;
import cn.fsp.chatbridgevelocity.chat.message.Message;
import cn.fsp.chatbridgevelocity.chat.qq.QQChat;
import org.slf4j.Logger;

/**
 * 处理QQ群中的特殊命令
 * 包括聊天同步命令的处理和权限检查
 */
public class QQCommandHandler {
    private final QQChat qqChat;
    private final Message message;
    private final Logger logger;

    public QQCommandHandler(QQChat qqChat, Message message, Logger logger) {
        this.qqChat = qqChat;
        this.message = message;
        this.logger = logger;
    }

    /**
     * 处理聊天同步命令
     */
    public void handleChatSync(String command, boolean hasPermission) {
        if (!command.startsWith(Constants.CMD_PREFIX_CHAT_SYNC) && !command.startsWith(Constants.CMD_PREFIX_CHAT_SYNC_LOWER)) {
            return;
        }

        if (!hasPermission) {
            qqChat.sendMessage(message.getNoPermission(), "sync");
            return;
        }

        String cmd = command.substring(10).trim();
        if (cmd.equals("on")) {
            if (qqChat.getSync()) {
                qqChat.sendMessage(message.getOnState(), "sync");
                return;
            }
            qqChat.setSync(true);
            qqChat.sendMessage(message.getOn(), "on");
        } else if (cmd.equals("off")) {
            if (!qqChat.getSync()) {
                qqChat.sendMessage(message.getOffState(), "sync");
                return;
            }
            qqChat.setSync(false);
            qqChat.sendMessage(message.getOff(), "off");
        } else {
            qqChat.sendMessage("Chat sync help\n!!chatSync on/off\n!!chatsync on/off", "sync");
        }
    }

    /**
     * 检查Go-CQHTTP用户是否有权限
     */
    public static boolean hasPermission(String role) {
        return Constants.isGoCQHttpAdmin(role);
    }

    /**
     * 检查Mirai用户是否有权限
     */
    public static boolean hasMiraiPermission(String permission) {
        return Constants.isMiraiAdmin(permission);
    }
}

