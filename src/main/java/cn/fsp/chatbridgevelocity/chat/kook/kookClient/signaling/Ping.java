package cn.fsp.chatbridgevelocity.chat.kook.kookClient.signaling;

import cn.fsp.chatbridgevelocity.chat.kook.kookClient.KookClient;
import cn.fsp.chatbridgevelocity.chat.kook.util.TimeUtil;

public class Ping implements Runnable{
    private boolean Timeout;
    private final KookClient kookClient;
    private long pongTimestamp;

    public Ping(KookClient kookClient) {
        this.Timeout = false;
        this.kookClient = kookClient;
    }

    @Override
    public void run() {
        pong();
    }

    private boolean pong() {
        if (Timeout) {
            return true;
        }
        while (System.currentTimeMillis() - pongTimestamp < 6000) {
        }
        kookClient.close();
        return pong();
    }

    public void setPingTimestamp() {
        this.pongTimestamp = TimeUtil.getCurrentTimeStamp();
    }

    public static String ping(int sn) {
        return "{\"s\": 2, \"sn\": " + sn + "}";
    }
}
