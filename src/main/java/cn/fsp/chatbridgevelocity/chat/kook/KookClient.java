package cn.fsp.chatbridgevelocity.chat.kook;

import cn.fsp.chatbridgevelocity.ChatBridgeVelocity;
import cn.fsp.chatbridgevelocity.chat.Status;
import cn.fsp.chatbridgevelocity.chat.kook.signaling.Hello;
import cn.fsp.chatbridgevelocity.chat.kook.signaling.Ping;
import cn.fsp.chatbridgevelocity.chat.util.JsonUtil;
import cn.fsp.chatbridgevelocity.chat.util.URIUtil;
import cn.fsp.chatbridgevelocity.refactoring.config.Config;
import cn.fsp.chatbridgevelocity.refactoring.event.KookMessageEvent;
import com.google.gson.JsonObject;
import com.velocitypowered.api.scheduler.ScheduledTask;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.concurrent.TimeUnit;

public class KookClient extends WebSocketClient {
    public Boolean conn;
    private ScheduledTask task;
    private long openTimestamp;
    private int sn;
    public String sessionId;
    private final ChatBridgeVelocity plugin;
    private final Config config;
    private ScheduledTask connTask;

    public KookClient(URI serverUri, ChatBridgeVelocity plugin) {
        super(serverUri);
        this.conn = false;
        this.sn = 0;
        this.plugin = plugin;
        this.config = plugin.config;
        plugin.logger.info("Kook start..");
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        openTimestamp = System.currentTimeMillis();
        plugin.logger.info("Kook 已连接");
        startConnTask();
    }

    @Override
    public void onMessage(String s) {
        JsonObject msg = JsonUtil.getJsonObject(s);
        int code = msg.get("s").getAsInt();
        switch (code) {
            case 0:
                JsonObject d = msg.getAsJsonObject("d");
                msgParse(d);
                sn = msg.get("sn").getAsInt();
                break;
            case 1:
                // Hello
                sessionId = Hello.hello(msg.getAsJsonObject("d"));
                if (sessionId != null) {
                    conn = true;
                    startPing();
                }else {
                    close();
                }
                break;
            case 3:
                // pong!!
//                System.out.println("pong!!");
                break;
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Status.kookChatStatus = false;
        connTask.cancel();
    }

    @Override
    public void onError(Exception ex) {
        plugin.logger.error("连接异常");
        Status.kookChatStatus = false;
    }

    public void startPing() {
        task = plugin.server.getScheduler().buildTask(plugin, () -> {
            if (isOpen()) {
                send(Ping.ping(sn));
            }
        })
                .repeat(30L, TimeUnit.SECONDS)
                .schedule();
    }

    private void msgParse(JsonObject d) {
        int type = d.get("type").getAsInt();
        JsonObject extra = d.getAsJsonObject("extra");
        JsonObject author = extra.getAsJsonObject("author");
        if (
                !(type == 1 || type == 9)
                || d.get("author_id").getAsString().equals("1")
                || author.get("bot").getAsBoolean()
        ) {
            return;
        }
        String server = extra.get("guild_id").getAsString();
        String channel = d.get("target_id").getAsString();
        String message = d.get("content").getAsString();
        String sender = author.get("nickname").getAsString();
        // 事件
        msgEvent(server, channel, sender, message);
    }

    private void msgEvent(String server, String channel, String sender, String message) {
        plugin.server.getEventManager().fire(new KookMessageEvent(server, channel, sender, message));
    }

    public void close() {
        if (conn) {
            task.cancel();
        }
        super.close();
    }

    public void _reconnect() {
        uri = URIUtil.createURI(getURI() + "&resume=1&sn=" + sn + "&session_id=" + sessionId);
        reconnect();
        if (isOpen()) {
            Status.kookChatStatus = true;
        }
    }

    public void startConnTask() {
        connTask = plugin.server.getScheduler().buildTask(
                        plugin, () -> {
                            if (!isOpen()) {
                                _reconnect();
                            }
                        })
                .repeat(10L, TimeUnit.SECONDS)
                .schedule();
    }
}
