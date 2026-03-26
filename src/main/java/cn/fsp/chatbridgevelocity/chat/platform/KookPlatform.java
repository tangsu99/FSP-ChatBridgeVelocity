package cn.fsp.chatbridgevelocity.chat.platform;

/**
 * Kook平台适配器
 * 通过实现ChatPlatform接口，统一Kook客户端的管理
 */
import cn.fsp.chatbridgevelocity.chat.kook.API.ChannelMessage;
import cn.fsp.chatbridgevelocity.chat.kook.KookClient;
import cn.fsp.chatbridgevelocity.chat.util.ChannelMsgBody;
import cn.fsp.chatbridgevelocity.refactoring.config.Config;

public class KookPlatform implements ChatPlatform {
    private final KookClient kookClient;
    private final ChannelMessage channelMessage;
    private final Config config;

    public KookPlatform(KookClient kookClient, Config config) {
        this.kookClient = kookClient;
        this.config = config;
        this.channelMessage = new ChannelMessage(config.getKookBotToken());
    }

    @Override
    public void connect() {
        kookClient.connect();
    }

    @Override
    public void disconnect() {
        kookClient.close();
    }

    @Override
    public void sendMessage(String message, String echo) {
        String body = ChannelMsgBody.msgBody(config.getKookChannelID(), message);
        int result = channelMessage.sendMessage(body);
        if (result != 0) {
            // 处理发送失败
            System.err.println("Failed to send Kook message: " + result);
        }
    }

    @Override
    public boolean isConnected() {
        return kookClient.conn;
    }

    @Override
    public void setSync(boolean sync) {
        // Kook可能不需要sync
    }

    @Override
    public boolean getSync() {
        return false;
    }
}
