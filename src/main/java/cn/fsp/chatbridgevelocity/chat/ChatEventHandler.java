package cn.fsp.chatbridgevelocity.chat;

import cn.fsp.chatbridgevelocity.ChatBridgeVelocity;
import cn.fsp.chatbridgevelocity.chat.platform.ChatPlatform;
import cn.fsp.chatbridgevelocity.refactoring.config.Config;
import cn.fsp.chatbridgevelocity.refactoring.event.KookMessageEvent;
import cn.fsp.chatbridgevelocity.refactoring.event.SocketEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * 处理Velocity服务器端的聊天事件
 * 负责玩家聊天、服务器状态、登入登出等事件的处理
 */
public class ChatEventHandler {
    private final ProxyServer server;
    private final Logger logger;
    private final Config config;
    private final StatusManager statusManager;
    private final MessageFormatter messageFormatter;
    private final ChatPlatform qqPlatform;
    private final ChatPlatform kookPlatform;
    private final Map<String, String> playerCurrentServer = new HashMap<>();
    private final Map<String, Long> timestampMap = new HashMap<>();
    private final long cooldownMillis;

    public ChatEventHandler(ChatBridgeVelocity plugin, StatusManager statusManager,
                          MessageFormatter messageFormatter, ChatPlatform qqPlatform,
                          ChatPlatform kookPlatform) {
        this.server = plugin.server;
        this.logger = plugin.logger;
        this.config = plugin.config;
        this.statusManager = statusManager;
        this.messageFormatter = messageFormatter;
        this.qqPlatform = qqPlatform;
        this.kookPlatform = kookPlatform;
        this.cooldownMillis = config.getCD() * 1000L;
    }

    @Subscribe
    public void onPlayerChatEvent(PlayerChatEvent event) {
        if (!statusManager.isChatForwardEnabled()) {
            return;
        }

        String currentServerName = event.getPlayer().getCurrentServer().orElseThrow().getServer().getServerInfo().getName();
        String playerName = event.getPlayer().getUsername();
        String message = event.getMessage();

        // 处理QQ消息
        if (qqPlatform != null && (message.startsWith(config.getMCRespondPrefix()) || qqPlatform.getSync())) {
            if (!qqPlatform.getSync()) {
                message = message.substring(config.getMCRespondPrefix().length()).trim();
            } else {
                message += "\t" + Constants.CHAT_SYNC_FLAG;
            }
            String qqMessage = messageFormatter.formatQQMessage(currentServerName, playerName, message);
            qqPlatform.sendMessage(qqMessage, String.valueOf(event.hashCode()));
        }

        // 转发到其他服务器
        for (RegisteredServer server : this.server.getAllServers()) {
            if (!server.getServerInfo().getName().equals(currentServerName)) {
                for (Player player : server.getPlayersConnected()) {
                    player.sendMessage(messageFormatter.formatChatMessage(currentServerName, playerName, message));
                }
            }
        }

        logger.info("[" + currentServerName + "]<" + playerName + "> " + message);
    }

    @Subscribe
    public void onServerConnectedEvent(ServerConnectedEvent event) {
        if (!statusManager.isChatForwardEnabled()) {
            return;
        }

        String currentServer = event.getServer().getServerInfo().getName();
        String playerName = event.getPlayer().getUsername();
        playerCurrentServer.put(playerName, currentServer);

        if (event.getPreviousServer().isPresent()) {
            String previousServer = event.getPreviousServer().get().getServerInfo().getName();
            broadcastLeaveMessage(previousServer, playerName);
            logger.info("[" + previousServer + "] " + playerName + " left " + previousServer);
        }

        broadcastJoinMessage(currentServer, playerName);
        logger.info("[" + currentServer + "] " + playerName + " joined " + currentServer);
    }

    @Subscribe
    public void onDisconnectEvent(DisconnectEvent event) {
        if (!statusManager.isChatForwardEnabled()) {
            return;
        }

        String playerName = event.getPlayer().getUsername();
        String previousServer = playerCurrentServer.get(playerName);
        if (previousServer != null) {
            broadcastLeaveMessage(previousServer, playerName);
            playerCurrentServer.remove(playerName);
        }
    }

    @Subscribe
    public void onLoginEvent(LoginEvent event) {
        if (!config.getQQJoinMessageEnabled()) {
            return;
        }

        String playerName = event.getPlayer().getUsername();
        if (event.getResult().isAllowed() && shouldSendJoinMessage(playerName)) {
            String joinMessage = messageFormatter.formatQQJoinMessage(playerName);
            if (statusManager.isQqChatEnabled() && qqPlatform != null) {
                qqPlatform.sendMessage(joinMessage, String.valueOf(event.hashCode()));
            }
            if (statusManager.isKookChatEnabled() && kookPlatform != null) {
                kookPlatform.sendMessage(joinMessage, "join");
            }
        }
    }

    @Subscribe
    public void onKookMessageEvent(KookMessageEvent event) {
        if (!statusManager.isKookChatEnabled() || !config.getKookServerID().equals(event.getServer())) {
            return;
        }

        if (event.getMessage().startsWith(Constants.CMD_ONLINE)) {
            // 处理在线指令
            return;
        }

        if (config.getKookChannelID().equals(event.getChannel())) {
            broadcastMessageFromPlatform(event.getSender(), event.getMessage(), Constants.SOURCE_KOOK);
        }
    }

    @Subscribe
    public void onSocketEvent(SocketEvent event) {
        server.getAllPlayers().forEach(player -> {
            if (event.getStatus() == Constants.SERVER_STARTED_STATUS) {
                player.sendMessage(messageFormatter.formatServerStartedMessage(event.getServerName()));
            } else {
                player.sendMessage(messageFormatter.formatServerStoppedMessage(event.getServerName()));
            }
        });
        logger.info(event.getServerName() + (event.getStatus() == Constants.SERVER_STARTED_STATUS ? " Started!" : " Stopped!"));
    }

    private void broadcastJoinMessage(String serverName, String playerName) {
        for (RegisteredServer server : this.server.getAllServers()) {
            if (!server.getServerInfo().getName().equals(serverName)) {
                for (Player player : server.getPlayersConnected()) {
                    player.sendMessage(messageFormatter.formatJoinMessage(serverName, playerName));
                }
            }
        }
    }

    private void broadcastLeaveMessage(String serverName, String playerName) {
        for (RegisteredServer server : this.server.getAllServers()) {
            if (!server.getServerInfo().getName().equals(serverName)) {
                for (Player player : server.getPlayersConnected()) {
                    player.sendMessage(messageFormatter.formatLeaveMessage(serverName, playerName));
                }
            }
        }
    }

    private void broadcastMessageFromPlatform(String sender, String message, String source) {
        server.getAllPlayers().forEach(player ->
            player.sendMessage(messageFormatter.formatChatMessage(source, sender, message))
        );
        logger.info(source + "<" + sender + "> " + message);
    }

    private boolean shouldSendJoinMessage(String playerName) {
        long currentTime = System.currentTimeMillis();
        Long lastSendTime = timestampMap.get(playerName);

        if (lastSendTime == null || currentTime >= lastSendTime) {
            timestampMap.put(playerName, currentTime + cooldownMillis);
            return true;
        }
        return false;
    }
}