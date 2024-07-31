package cn.fsp.chatbridgevelocity;

import cn.fsp.chatbridgevelocity.chat.ChatForward;
import cn.fsp.chatbridgevelocity.chat.Status;
import cn.fsp.chatbridgevelocity.chat.kook.API.ChannelMessage;
import cn.fsp.chatbridgevelocity.chat.kook.API.Gateway;
import cn.fsp.chatbridgevelocity.chat.kook.KookClient;
import cn.fsp.chatbridgevelocity.command.CmdBuilder;
import cn.fsp.chatbridgevelocity.config.Config;
import cn.fsp.chatbridgevelocity.serverPacket.SocketServer;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URI;

@Plugin(
        id = "chatbridgevelocity",
        name = "FSP-ChatBridgeVelocity",
        version = BuildConstants.VERSION,
        authors = "tangsu99",
        url = "https://github.com/tangsu99/FSP-ChatBridgeVelocity"
)
public class ChatBridgeVelocity {

    @Inject
    public Logger logger;
    @Inject
    public CommandManager commandManager;
    @Inject
    public Injector injector;
    @Inject
    public ProxyServer server;
    public static Config config;
    public ChatForward chatForward;
    private SocketServer socketServer;
    private Gateway gateway;
    private KookClient kookClient;
    public static ChannelMessage channelMessage;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        config = new Config();
        Status.init();
        socketServer = new SocketServer(this);
        chatForward = new ChatForward(this);
        try {
            socketServer.startListener();
        } catch (IOException e) {
//            throw new RuntimeException(e);
        }
        // kook 互通
        kook();
        server.getEventManager().register(this, chatForward);
        commandManager.register(injector.getInstance(CmdBuilder.class).register(this));
    }

    @Subscribe
    public void onProxyShutdownEvent(ProxyShutdownEvent event) {
        socketServer.close();
        chatForward.qqChatClose();
    }

    private void kook() {
        if (!Status.kookChatStatus) {
            logger.info("kook聊天互通已禁用");
            return;
        }
        this.gateway = new Gateway(config.getKookBotToken(), 0);
        URI uri = gateway.getGatewayURL();
        if (uri == null) {
            logger.error("kook 网关获取错误");
            return;
        }
        channelMessage = new ChannelMessage(config.getKookBotToken());
        this.kookClient = new KookClient(uri, this);
        this.kookClient.connect();
    }

    public void reload() {
        config.reLoadConfig();
        chatForward.reload();
        kookClient._reconnect();
    }
}
