package cn.fsp.chatbridgevelocity.chat.qq.handler;

import cn.fsp.chatbridgevelocity.chat.Constants;
import cn.fsp.chatbridgevelocity.chat.qq.OneBot11SendGroupMsg;
import cn.fsp.chatbridgevelocity.chat.util.PlatformSender;
import cn.fsp.chatbridgevelocity.chat.util.QQPlatformSender;
import cn.fsp.chatbridgevelocity.chat.util.QQSender;
import cn.fsp.chatbridgevelocity.config.Config;
import cn.fsp.chatbridgevelocity.event.PlatformCommandEvent;
import cn.fsp.chatbridgevelocity.event.QQMessageEvent;
import com.google.gson.JsonObject;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

/**
 * OneBot11机器人框架处理器
 * 负责处理来自OneBot11框架的QQ消息
 */
public class OneBot11Handler extends Handler {

    public OneBot11Handler(ProxyServer server, Logger logger, Config config) {
        super(server, logger, config);
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
            logger.error("Error processing OneBot11 message", e);
        }
    }

    private void handleGroupMessage(JsonObject jsonObject) {
        if (!jsonObject.has("group_id") || !jsonObject.has("message") || !jsonObject.has("sender")) {
            return;
        }

        String groupId = jsonObject.get("group_id").getAsString();
        if (!groupId.equals(config.getQQGroup())) {
            return;
        }

        String messageText = jsonObject.get("message").getAsString();
        JsonObject sender = jsonObject.get("sender").getAsJsonObject();
        String senderName = getName(sender);
        String senderRole = sender.has("role") ? sender.get("role").getAsString() : "member";
        String senderQQ = sender.get("user_id").getAsNumber().toString();

        QQSender qqSender = new QQSender(senderName, senderName, senderRole, senderQQ);

        // 检查是否是命令
        if (messageText.startsWith("!!")) {
            PlatformSender platformSender = new QQPlatformSender(qqChat, config.getQQGroup()).setMessageSender(qqSender);
            server.getEventManager().fire(new PlatformCommandEvent("QQ", messageText, platformSender));
            return;
        }

        // 处理聊天同步前缀
        if (messageText.startsWith(config.getQQRespondPrefix()) || qqChat.getSync()) {
            String processedMsg;
            if (qqChat.getSync()) {
                processedMsg = messageText + "    " + Constants.CHAT_SYNC_FLAG;
            } else {
                processedMsg = messageText.substring(config.getQQRespondPrefix().length()).trim();
            }
            // 广播到游戏内
            fireMessageEvent(groupId, qqSender, processedMsg);
        }
    }

    @Override
    public String send(String group, String msg) {
        return gson.toJson(new OneBot11SendGroupMsg(group, msg, "0"));
    }

    @Override
    protected void fireMessageEvent(String group, QQSender qqSender, String message) {
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
