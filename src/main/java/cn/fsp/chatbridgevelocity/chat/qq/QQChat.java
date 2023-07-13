package cn.fsp.chatbridgevelocity.chat.qq;

import cn.fsp.chatbridgevelocity.chat.ChatForward;
import cn.fsp.chatbridgevelocity.chat.Status;
import cn.fsp.chatbridgevelocity.chat.qq.handler.Handler;
import cn.fsp.chatbridgevelocity.config.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;

import java.net.URI;
import java.util.concurrent.TimeUnit;;

public class QQChat extends WebSocketClient {
    private Gson gson = new GsonBuilder().create();
    private ChatForward chatForward;
    private ProxyServer server;
    private Logger logger;
    private Config config;
    private Handler handler;
    private boolean sync;
    private ScheduledTask connTask;

    public QQChat(URI serverUri, ChatForward chatForward, Handler handler) {
        super(serverUri);
        this.sync = false;
        this.chatForward = chatForward;
        this.server = chatForward.server;
        this.logger = chatForward.logger;
        this.config = chatForward.config;
        this.handler = handler;
        this.handler.setQQChat(this);
        logger.info("QQChat start..");
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        logger.info("QQ 已连接");
        startConnTask();
        if (isOpen()) {
            Status.qqChatStatus = true;
        }
    }

    @Override
    public void onMessage(String s) {
        handler.exec(s);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        Status.qqChatStatus = false;
        connTask.cancel();
    }

    @Override
    public void onError(Exception e) {
        logger.error("连接异常");
        Status.qqChatStatus = false;
    }

    public void sendMessage(String msg, String echo) {
        String message = handler.send(config.getQQGroup(), msg);
        if (isOpen()) {
            send(message);
//            send(gson.toJson(new sendGroupMsg(config.getQQGroup(), msg, echo)));
            return;
        }
        logger.info("连接异常，信息发送失败");
        Status.qqChatStatus = true;
    }

    public void setSync(boolean b) {
        this.sync = b;
    }
    public boolean getSync() {
        return sync;
    }

    public void startConnTask() {
        connTask = server.getScheduler().buildTask(
                chatForward.plugin, () -> {
                    if (!isOpen()) {
                        reconnect();
                    }
                })
                .repeat(10L, TimeUnit.SECONDS)
                .schedule();
    }
}
