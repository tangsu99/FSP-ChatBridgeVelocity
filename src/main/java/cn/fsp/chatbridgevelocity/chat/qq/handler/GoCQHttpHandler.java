package cn.fsp.chatbridgevelocity.chat.qq.handler;

import cn.fsp.chatbridgevelocity.chat.Constants;
import cn.fsp.chatbridgevelocity.chat.qq.GoCQHttpSendGroupMsg;
import cn.fsp.chatbridgevelocity.chat.qq.command.QQCommandHandler;
import cn.fsp.chatbridgevelocity.chat.util.QQSender;
import cn.fsp.chatbridgevelocity.refactoring.config.Config;
import cn.fsp.chatbridgevelocity.refactoring.event.QQMessageEvent;
import com.google.gson.JsonObject;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

/**
 * Go-CQHTTP机器人框架处理器
 * 负责处理来自Go-CQHTTP框架的QQ消息
 */
public class GoCQHttpHandler extends Handler {
    private final QQCommandHandler commandHandler;

    public GoCQHttpHandler(ProxyServer server, Logger logger, Config config) {
        super(server, logger, config);
        this.commandHandler = new QQCommandHandler(null, message, logger);
    }

    @Override
    public void exec(String json) {
        try {
            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
            if (!jsonObject.has("message_type")) {
                return;
            }

            if ("group".equals(jsonObject.get("message_type").getAsString())) {
                handleGroupMessage(jsonObject);
            }
        } catch (Exception e) {
            logger.error("Error processing GoCQHttp message", e);
        }
    }

    private void handleGroupMessage(JsonObject jsonObject) {
        String groupId = jsonObject.get("group_id").getAsString();
        if (!groupId.equals(config.getQQGroup())) {
            return;
        }

        String messageText = jsonObject.get("message").getAsString();
        JsonObject sender = jsonObject.get("sender").getAsJsonObject();
        String senderName = getName(sender);
        String senderRole = sender.get("role").getAsString();

        // 处理聊天同步前缀
        if (messageText.startsWith(config.getQQRespondPrefix()) || qqChat.getSync()) {
            String processedMsg;
            if (qqChat.getSync()) {
                processedMsg = messageText + "    " + Constants.CHAT_SYNC_FLAG;
            } else {
                processedMsg = messageText.substring(config.getQQRespondPrefix().length()).trim();
            }
            // 广播到游戏内
            fireMessageEvent(groupId, senderName, processedMsg);
            return;
        }

        // 处理特殊命令
        handleSpecialCommands(messageText);

        // 处理权限相关命令
        boolean hasPermission = Constants.isGoCQHttpAdmin(senderRole);
        commandHandler.handleChatSync(messageText, hasPermission);

        // 发送消息事件
        fireMessageEvent(groupId, senderName, messageText);
    }

    private void handleSpecialCommands(String message) {
        switch (message) {
            case Constants.CMD_ONLINE:
                logger.info("Received !!online command");
                break;
            case Constants.CMD_PING:
                qqChat.sendMessage("pong!!", "pong");
                break;
            case Constants.CMD_HELP:
                qqChat.sendMessage("FSP-ChatBridgeVelocity\n!!help\t显示此信息\n!!mc\t发送信息到mc\n!!chatSync on/off\t聊天同步\n!!online\t显示在线玩家\n!!ping\tpong!!", "help");
                break;
            case Constants.CMD_STATUS:
                logger.info("Received status command");
                break;
        }
    }

    @Override
    public String send(String group, String msg) {
        return gson.toJson(new GoCQHttpSendGroupMsg(group, msg, "0"));
    }

    @Override
    protected void fireMessageEvent(String group, String sender, String message) {
        QQSender qqSender = new QQSender(sender, sender, "member", "");
        server.getEventManager().fire(new QQMessageEvent(group, qqSender, message));
    }

    private String getName(JsonObject sender) {
        String card = sender.get("card").getAsString();
        if (card != null && !card.isEmpty()) {
            return card;
        }
        return sender.get("nickname").getAsString();
    }
}
