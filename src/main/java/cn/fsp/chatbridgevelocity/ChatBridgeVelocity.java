package cn.fsp.chatbridgevelocity;

import cn.fsp.chatbridgevelocity.chat.ChatEventHandler;
import cn.fsp.chatbridgevelocity.chat.MessageFormatter;
import cn.fsp.chatbridgevelocity.chat.StatusManager;
import cn.fsp.chatbridgevelocity.chat.kook.API.Gateway;
import cn.fsp.chatbridgevelocity.chat.platform.ChatPlatform;
import cn.fsp.chatbridgevelocity.chat.platform.KookPlatform;
import cn.fsp.chatbridgevelocity.chat.platform.QQPlatform;
import cn.fsp.chatbridgevelocity.chat.qq.QQChat;
import cn.fsp.chatbridgevelocity.chat.kook.KookClient;
import cn.fsp.chatbridgevelocity.chat.qq.handler.OneBot11Handler;
import cn.fsp.chatbridgevelocity.chat.util.URIUtil;
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
    public Config config;
    public StatusManager statusManager;
    public MessageFormatter messageFormatter;
    public ChatPlatform qqPlatform;
    public ChatPlatform kookPlatform;
    public ChatEventHandler chatEventHandler;
    public SocketServer socketServer;
    public static ChatBridgeVelocity cbv;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        cbv = this;
        config = new Config();
        statusManager = new StatusManager();
        messageFormatter = new MessageFormatter(config);

        // 初始化状态
        statusManager.setChatForwardEnabled(config.ChatForwardEnabled());
        statusManager.setQqChatEnabled(config.getQQChatEnabled());
        statusManager.setKookChatEnabled(config.getKookEnabled());

        // 初始化平台
        initializePlatforms();

        chatEventHandler = new ChatEventHandler(this, statusManager, messageFormatter, qqPlatform, kookPlatform);
        server.getEventManager().register(this, chatEventHandler);

        // 初始化SocketServer
        try {
            socketServer = new SocketServer(this);
            socketServer.startListener();
        } catch (Exception e) {
            logger.error("Failed to start socket server", e);
        }

        commandManager.register(injector.getInstance(CmdBuilder.class).register(this));
    }

    private void initializePlatforms() {
        if (statusManager.isQqChatEnabled()) {
            try {
                URI qqUri = URIUtil.createURI("ws://" + config.getHost() + ":" + config.getPort() + "/");
                OneBot11Handler handler = new OneBot11Handler(server, logger, config);
                QQChat qqChat = new QQChat(qqUri, this, server, logger, config, handler, statusManager);
                qqChat.addHeader("Authorization", "Bearer " + config.getToken());
                qqPlatform = new QQPlatform(qqChat);
                qqPlatform.connect();
                logger.info("QQ platform initialized successfully");
            } catch (Exception e) {
                logger.error("Failed to initialize QQ platform", e);
                statusManager.setQqChatEnabled(false);
            }
        }

        if (statusManager.isKookChatEnabled()) {
            try {
                Gateway gateway = new Gateway(config.getKookBotToken(), 0);
                KookClient kookClient = new KookClient(gateway.getGatewayURL(), this);
                kookPlatform = new KookPlatform(kookClient, config);
                kookPlatform.connect();
                logger.info("Kook platform initialized successfully");
            } catch (Exception e) {
                logger.error("Failed to initialize Kook platform", e);
                statusManager.setKookChatEnabled(false);
            }
        }
    }

    @Subscribe
    public void onProxyShutdownEvent(ProxyShutdownEvent event) {
        if (socketServer != null) {
            socketServer.close();
        }
        if (qqPlatform != null) {
            qqPlatform.disconnect();
        }
        if (kookPlatform != null) {
            kookPlatform.disconnect();
        }
    }

    public void reload() {
        config.reLoadConfig();
        statusManager.setChatForwardEnabled(config.ChatForwardEnabled());
        statusManager.setQqChatEnabled(config.getQQChatEnabled());
        statusManager.setKookChatEnabled(config.getKookEnabled());

        // 重新初始化平台
        if (qqPlatform != null) {
            qqPlatform.disconnect();
        }
        if (kookPlatform != null) {
            kookPlatform.disconnect();
        }
        initializePlatforms();
    }
}
