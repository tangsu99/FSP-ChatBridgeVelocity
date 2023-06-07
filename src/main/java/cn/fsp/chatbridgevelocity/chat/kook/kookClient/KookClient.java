package cn.fsp.chatbridgevelocity.chat.kook.kookClient;

import cn.fsp.chatbridgevelocity.ChatBridgeVelocity;
import cn.fsp.chatbridgevelocity.chat.kook.kookClient.signaling.Hello;
import cn.fsp.chatbridgevelocity.chat.kook.kookClient.signaling.Ping;
import cn.fsp.chatbridgevelocity.chat.kook.util.JsonUtil;
import cn.fsp.chatbridgevelocity.event.KookMessageEvent;
import cn.fsp.chatbridgevelocity.event.SocketEvent;
import com.google.gson.JsonObject;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.concurrent.TimeUnit;

public class KookClient extends WebSocketClient {
    private ScheduledTask task;
    private long openTimestamp;
    private boolean hello;
    private int sn;
    public String sessionId;
    private final ChatBridgeVelocity plugin;

    public KookClient(URI serverUri, ChatBridgeVelocity plugin) {
        super(serverUri);
        this.sn = 0;
        this.plugin = plugin;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        openTimestamp = System.currentTimeMillis();
    }

    @Override
    public void onMessage(String message) {
        JsonObject msg = JsonUtil.getJsonObject(message);
        int code = msg.get("s").getAsInt();
        switch (code) {
            case 0:
                sn = msg.get("sn").getAsInt();
                JsonObject d = msg.getAsJsonObject("d");
                if (d.get("type").getAsInt() != 1) {
                    break;
                }
                // kook 消息
//                msgEvent();
                break;
            case 1:
                // Hello
                sessionId = Hello.hello(msg.getAsJsonObject("d"));
                if (sessionId == null) {
                    close();
                }else {
                    startPing();
                }
                break;
            case 2:
                break;
            case 3:
                // pong!!
                System.out.println("pong!!");
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("已关闭");
    }

    @Override
    public void onError(Exception ex) {
        System.out.println(ex.toString());
    }

    public void startPing() {
//        ping = new Thread(() -> {
//            while (true) {
//                send(Ping.ping(sn));
//                try {
//                    wait();
//                    Thread.sleep(30000);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });
//        ping.start();
        task = plugin.server.getScheduler().buildTask(plugin, () -> send(Ping.ping(sn)))
                .repeat(30L, TimeUnit.SECONDS)
                .schedule();
    }

    private void msgEvent(String channel, String sender, String message) {
        if (true) {
            return;
        }
        plugin.logger.info("");
        plugin.server.getEventManager().fire(new KookMessageEvent());
    }

    public void close() {
        task.cancel();

    }
}
