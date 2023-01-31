package cn.fsp.chatbridgevelocity.chat.qq;

import cn.fsp.chatbridgevelocity.chat.ChatForward;
import cn.fsp.chatbridgevelocity.chat.qq.API.sendGroupMsg;
import cn.fsp.chatbridgevelocity.chat.qq.Event.groupEvent;
import cn.fsp.chatbridgevelocity.config.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
        if (s.startsWith("{\"post_type\":\"message\",\"message_type\":\"group\"")) {
            groupEvent event = gson.fromJson(s, groupEvent.class);
            if (event.group_id.equals(config.getQQGroup())) {
                if (event.message.startsWith(config.getQQRespondPrefix())) {
                    String msg = event.message.substring(4, event.message.length()).trim();
                    chatForward.allPlayerSendMessage(event.sender.card, msg);
                }
                if (event.message.equals("!!online")) {
                    sendMessage(chatForward.getOnline(), "online");
                }
                if (event.message.equals("!!ping")) {
                    sendMessage("pong!!", "pong");
                }
                if (event.message.equals("!!help")) {
                    sendMessage("FSP-ChatBridgeVelocity\n!!help\t显示此信息\n!!mc\t发送信息到mc\n!!ping\tpong!!", "help");
                }
                if (event.message.equals("信不信群里都是我小号，不信我换另一个号再发一遍")) {
                    sendMessage("信不信群里都是我小号，不信我换另一个号再发一遍", "rua");
                }
            }
        }
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
        logger.error("发送失败, 无法连接");
    }

    public void sendMessage(StringBuilder msg, String echo) {
        if (isOpen()) {
            send(gson.toJson(new sendGroupMsg(config.getQQGroup(), msg.toString(), echo)));
            return;
        }
        logger.error("发送失败, 无法连接");
    }
}
