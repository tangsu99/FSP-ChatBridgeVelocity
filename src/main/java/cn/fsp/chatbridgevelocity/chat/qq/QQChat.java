package cn.fsp.chatbridgevelocity.chat.qq;

import cn.fsp.chatbridgevelocity.ChatBridgeVelocity;
import cn.fsp.chatbridgevelocity.chat.StatusManager;
import cn.fsp.chatbridgevelocity.chat.qq.handler.Handler;
import cn.fsp.chatbridgevelocity.config.Config;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;

import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * QQ聊天WebSocket客户端
 * 负责与QQ群的连接和消息收发
 */
public class QQChat extends WebSocketClient {
    private final ChatBridgeVelocity plugin;
    private final ProxyServer server;
    private final Logger logger;
    private final Config config;
    private final Handler handler;
    private final StatusManager statusManager;
    private boolean sync = false;
    private ScheduledTask connTask;

    public QQChat(URI serverUri, ChatBridgeVelocity plugin, ProxyServer server, Logger logger, Config config, 
                  Handler handler, StatusManager statusManager) {
        super(serverUri);
        this.plugin = plugin;
        this.server = server;
        this.logger = logger;
        this.config = config;
        this.handler = handler;
        this.statusManager = statusManager;
        this.handler.setQQChat(this);
        logger.info("QQChat start..");
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        logger.info("QQ 已连接");
        startConnTask();
        if (isOpen()) {
            statusManager.setQqChatEnabled(true);
        }
    }

    @Override
    public void onMessage(String s) {
        try {
            handler.exec(s);
        } catch (Exception e) {
            logger.error("Error processing QQ message", e);
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        statusManager.setQqChatEnabled(false);
        if (connTask != null) {
            connTask.cancel();
        }
    }

    @Override
    public void onError(Exception e) {
        logger.error("QQ连接异常: {}", e.getMessage());
        statusManager.setQqChatEnabled(false);
    }

    /**
     * 发送消息到QQ群
     */
    public void sendMessage(String msg, String echo) {
        if (!isOpen()) {
            logger.warn("QQ连接已断开，消息发送失败");
            statusManager.setQqChatEnabled(false);
            return;
        }
        
        try {
            String message = handler.send(config.getQQGroup(), msg);
            send(message);
        } catch (Exception e) {
            logger.error("Failed to send message to QQ", e);
            statusManager.setQqChatEnabled(false);
        }
    }

    public void setSync(boolean b) {
        this.sync = b;
    }

    public boolean getSync() {
        return sync;
    }

    private void startConnTask() {
        if (connTask != null) {
            connTask.cancel();
        }
        connTask = server.getScheduler().buildTask(plugin, () -> {
            if (!isOpen()) {
                logger.info("尝试重新连接QQ...");
                try {
                    reconnect();
                } catch (Exception e) {
                    logger.error("Failed to reconnect to QQ", e);
                }
            }
        }).repeat(10L, TimeUnit.SECONDS).schedule();
    }
}
