package cn.fsp.chatbridgevelocity;

import cn.fsp.chatbridgevelocity.chat.ChatForward;
import cn.fsp.chatbridgevelocity.command.CmdBuilder;
import cn.fsp.chatbridgevelocity.command.CommandReg;
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
    public ChatForward chatForward;
    private SocketServer socketServer;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        config = new Config();
        socketServer = new SocketServer(this);
        chatForward = new ChatForward(this);
        try {
            socketServer.startListener(chatForward);
        } catch (IOException ignored) {
        }
        server.getEventManager().register(this, chatForward);
        commandManager.register(injector.getInstance(CmdBuilder.class).register(this));
        CommandReg.regCommand(commandManager, this);
    }

    @Subscribe
    public void onProxyShutdownEvent(ProxyShutdownEvent event) {
        socketServer.close();
        chatForward.qqChatClose();
    }

    public void reload() {
        config.reLoadConfig();
        chatForward.reload();
    }
}
