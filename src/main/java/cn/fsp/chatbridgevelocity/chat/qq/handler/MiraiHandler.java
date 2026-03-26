package cn.fsp.chatbridgevelocity.chat.qq.handler;

import cn.fsp.chatbridgevelocity.chat.Constants;
import cn.fsp.chatbridgevelocity.chat.qq.MiraiSendGroupMsg;
import cn.fsp.chatbridgevelocity.chat.qq.command.QQCommandHandler;
import cn.fsp.chatbridgevelocity.refactoring.config.Config;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

/**
 * Mirai机器人框架处理器
 */
public class MiraiHandler extends Handler {
    private final MiraiSendGroupMsg miraiSendGroupMsg;
    private final QQCommandHandler commandHandler;
    private String sessionKey;

    public MiraiHandler(ProxyServer server, Logger logger, Config config) {
        super(server, logger, config);
        this.miraiSendGroupMsg = new MiraiSendGroupMsg(config.getQQGroup(), "");
        this.commandHandler = new QQCommandHandler(null, message, logger);
    }

    @Override
    public void exec(String json) {
        try {
            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
            if (!jsonObject.has("data")) {
                return;
            }

            JsonObject data = jsonObject.get("data").getAsJsonObject();
            
            // 处理会话密钥
            if (data.has("session")) {
                sessionKey = data.get("session").getAsString();
                return;
            }

            if (!data.has("type")) {
                return;
            }

            if ("GroupMessage".equals(data.get("type").getAsString())) {
                handleGroupMessage(data);
            }
        } catch (Exception e) {
            logger.error("Error processing Mirai message", e);
        }
    }

    private void handleGroupMessage(JsonObject data) {
        String message = extractMessageFromChain(data.getAsJsonArray("messageChain"));
        if (message == null) {
            return;
        }

        JsonObject sender = data.get("sender").getAsJsonObject();
        String group = sender.get("group").getAsJsonObject().get("id").getAsString();
        String senderName = sender.get("memberName").getAsString();
        String permission = sender.get("permission").getAsString();

        if (!group.equals(config.getQQGroup())) {
            return;
        }

        // 处理聊天同步
        if (message.startsWith(config.getQQRespondPrefix()) || qqChat.getSync()) {
            String processedMsg;
            if (qqChat.getSync()) {
                processedMsg = message + "    [chatSync]";
            } else {
                processedMsg = message.substring(config.getQQRespondPrefix().length()).trim();
            }
            fireMessageEvent(group, senderName, processedMsg);
            return;
        }

        // 处理特殊命令
        handleSpecialCommands(message);

        // 处理权限相关命令
        boolean hasPermission = "ADMINISTRATOR".equals(permission) || "OWNER".equals(permission);
        commandHandler.handleChatSync(message, hasPermission);

        // 发送消息事件
        fireMessageEvent(group, senderName, message);
    }

    private String extractMessageFromChain(JsonArray messageChain) {
        for (JsonElement element : messageChain) {
            JsonObject obj = element.getAsJsonObject();
            if ("Plain".equals(obj.get("type").getAsString())) {
                return obj.get("text").getAsString();
            }
        }
        return null;
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
        return gson.toJson(miraiSendGroupMsg.setMsg(group, msg, sessionKey));
    }

    @Override
    protected void fireMessageEvent(String group, String sender, String message) {
        // 委托给EventManager处理
        logger.info("[Mirai][{}] <{}> {}", group, sender, message);
    }
}
