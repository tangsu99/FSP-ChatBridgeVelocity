package cn.fsp.chatbridgevelocity.chat.qq;

import cn.fsp.chatbridgevelocity.chat.ChatForward;
import cn.fsp.chatbridgevelocity.config.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.velocitypowered.api.proxy.ProxyServer;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;

import java.net.URI;;

public class QQChat extends WebSocketClient {
    private Gson gson = new GsonBuilder().create();
    private ProxyServer server;
    private ChatForward chatForward;
    private Logger logger;
    private Config config;
    public QQChat(URI serverUri, ChatForward chatForward) {
        super(serverUri);
        this.chatForward = chatForward;
        this.server = chatForward.server;
        this.logger = chatForward.logger;
        this.config = chatForward.config;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        logger.info("QQ聊天互通已启用");
    }

    @Override
    public void onMessage(String s) {
        JsonObject jsonObject = gson.fromJson(s, JsonObject.class);
        if (!jsonObject.has("message_type")) {
            return;
        }
        if (jsonObject.get("message_type").getAsString().equals("group")) {
            if (jsonObject.get("group_id").getAsString().equals(config.getQQGroup())) {
                String message = jsonObject.get("message").getAsString();
                JsonObject sender = jsonObject.get("sender").getAsJsonObject();
                String name = getName(sender);
                if (message.startsWith(config.getQQRespondPrefix())) {
                    String msg = message.substring(4, message.length()).trim();
                    chatForward.allPlayerSendMessage(name, msg);
                }
                if (message.equals("!!online")) {
                    sendMessage(chatForward.getOnline().toString(), "online");
                }
                if (message.equals("!!ping")) {
                    sendMessage("pong!!", "pong");
                }
                if (message.equals("!!help")) {
                    sendMessage("FSP-ChatBridgeVelocity\n!!help\t显示此信息\n!!mc\t发送信息到mc\n!!ping\tpong!!", "help");
                }
            }
        }
    }

    private String getName(JsonObject sender) {
        if (!sender.get("card").getAsString().equals("")) {
            return sender.get("card").getAsString();
        }
        return sender.get("nickname").getAsString();
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        logger.info(s + i);
    }

    @Override
    public void onError(Exception e) {
        logger.error(e.getMessage());
    }

    public void sendMessage(String msg, String echo) {
        if (isOpen()) {
            send(gson.toJson(new sendGroupMsg(config.getQQGroup(), msg, echo)));
            return;
        }
        reconnect();
        sendMessage(msg, echo, 1);
    }

    private void sendMessage(String msg, String echo, int i) {
        if (isOpen()) {
            send(gson.toJson(new sendGroupMsg(config.getQQGroup(), msg, echo)));
            return;
        }
        logger.error("发送失败, 无法连接");
        reconnect();
    }
}
