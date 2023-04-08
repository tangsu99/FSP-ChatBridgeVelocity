package cn.fsp.chatbridgevelocity.chat.qq;

import cn.fsp.chatbridgevelocity.chat.ChatForward;
import cn.fsp.chatbridgevelocity.chat.util.Handler;
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
    private Logger logger;
    private Config config;
    private Handler handler;
    public QQChat(URI serverUri, ChatForward chatForward, Handler handler) {
        super(serverUri);
        this.server = chatForward.server;
        this.logger = chatForward.logger;
        this.config = chatForward.config;
        this.handler = handler;
        this.handler.setQQChat(this);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        logger.info("QQ 已连接");
    }

    @Override
    public void onMessage(String s) {
        handler.exec(s);
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
        String message = handler.send(config.getQQGroup(), msg);
        if (isOpen()) {
            send(message);
//            send(gson.toJson(new sendGroupMsg(config.getQQGroup(), msg, echo)));
            return;
        }
        reconnect();
        sendMessage(message, echo, 1);
    }

    private void sendMessage(String msg, String echo, int i) {
        if (isOpen()) {
            send(msg);
            return;
        }
        logger.error("发送失败, 无法连接");
        reconnect();
    }
}
