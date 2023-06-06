package cn.tangsu99.kookBot.kookClient;

import cn.tangsu99.kookBot.kookClient.signaling.Hello;
import cn.tangsu99.kookBot.kookClient.signaling.Ping;
import cn.tangsu99.kookBot.util.JsonUtil;
import com.google.gson.JsonObject;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class KookClient extends WebSocketClient {
    private Thread ping;
    private long openTimestamp;
    private boolean hello;
    private int sn;
    public String sessionId;
    public KookClient(URI serverUri) {
        super(serverUri);
        this.sn = 0;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        openTimestamp = System.currentTimeMillis();
        System.out.println("已连接");
    }

    @Override
    public void onMessage(String message) {
        JsonObject msg = JsonUtil.getJsonObject(message);
        int code = msg.get("s").getAsInt();
        switch (code) {
            case 0:
                sn = msg.get("sn").getAsInt();
                JsonObject d = msg.getAsJsonObject("d");
                if (d.get("type").getAsInt() == 1) {
                    System.out.println(d.get("content").getAsString());
                }
                System.out.println(d.get("content").getAsString());
                break;
            case 1:
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
        ping = new Thread(() -> {
            while (true) {
                System.out.println("!!ping");
                send(Ping.ping(sn));
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        ping.start();
        System.out.println("开始 ping");
    }
}
