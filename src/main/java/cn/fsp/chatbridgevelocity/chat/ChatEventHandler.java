package cn.fsp.chatbridgevelocity.chat;

import cn.fsp.chatbridgevelocity.ChatBridgeVelocity;
import cn.fsp.chatbridgevelocity.chat.platform.ChatPlatform;
import cn.fsp.chatbridgevelocity.chat.util.QQPlatformSender;
import cn.fsp.chatbridgevelocity.config.Config;
import cn.fsp.chatbridgevelocity.event.KookMessageEvent;
import cn.fsp.chatbridgevelocity.event.PlatformCommandEvent;
import cn.fsp.chatbridgevelocity.event.QQMessageEvent;
import cn.fsp.chatbridgevelocity.event.SocketEvent;
import cn.fsp.chatbridgevelocity.chat.message.Message;
import cn.fsp.chatbridgevelocity.chat.util.PlatformSender;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    private final Map<String, String> playerCurrentServer = new ConcurrentHashMap<>();
    private final Map<String, Long> timestampMap = new ConcurrentHashMap<>();
    private final long cooldownMillis;
    private final Message message;

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
        this.message = new Message();
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
            // Kook平台聊天转发常开，直接发送加入消息
            if (kookPlatform != null) {
                kookPlatform.sendMessage(joinMessage, "join");
            }
        }
    }

    @Subscribe
    public void onKookMessageEvent(KookMessageEvent event) {
        // Kook平台的聊天转发是常开功能，直接转发所有消息
        if (!config.getKookServerID().equals(event.getServer())) {
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

    @Subscribe
    public void onQQMessageEvent(QQMessageEvent event) {
        if (
                !statusManager.isQqChatEnabled()
                || !config.getQQGroup().equals(event.getGroup())
                || event.getSender().getQqID().equals(config.getBotQQ())
        ) return;

        broadcastMessageFromPlatform(event.getSender().getName(), event.getMessage(), Constants.SOURCE_QQ);
    }

    @Subscribe
    public void onPlatformCommandEvent(PlatformCommandEvent event) {
        String command = event.getCommand();
        PlatformSender sender = event.getSender();
        String platform = event.getPlatform();

        logger.info("Processing platform command: {} from {}", command, platform);

        // 解析命令
        if (command.equals("!!online")) {
            // 处理在线命令
            sender.reply("Online players: " + server.getPlayerCount());
        } else if (command.startsWith("!!mc")) {
            server.getEventManager().fire(
                new QQMessageEvent(((QQPlatformSender) sender.getSender()).getGroup(),
                ((QQPlatformSender) sender.getSender()).getMsgSender(),
                command.substring(3).trim())
            );
        }else if (command.startsWith("!!chatSync")) {
            // 处理聊天同步命令
            handleChatSyncCommand(command, sender, platform);
        } else if (command.equals("!!ping")) {
            sender.reply("pong!!");
        } else if (command.equals("!!help")) {
            sender.reply("FSP-ChatBridgeVelocity\n!!help\t显示此信息\n!!mc\t发送信息到mc\n!!chatSync on/off\t聊天同步\n!!online\t显示在线玩家\n!!ping\tpong!!");
        } else {
            sender.reply("Unknown command: " + command);
        }
    }

    private void handleChatSyncCommand(String command, PlatformSender sender, String platform) {
        String cmd = command.substring(10).trim();
        if (cmd.equals("on")) {
            if (platform.equals("QQ") && qqPlatform != null) {
                qqPlatform.setSync(true);
                sender.reply(message.getOn());
            } else if (platform.equals("KOOK") && kookPlatform != null) {
                kookPlatform.setSync(true);
                sender.reply(message.getOn());
            }
        } else if (cmd.equals("off")) {
            if (platform.equals("QQ") && qqPlatform != null) {
                qqPlatform.setSync(false);
                sender.reply(message.getOff());
            } else if (platform.equals("KOOK") && kookPlatform != null) {
                kookPlatform.setSync(false);
                sender.reply(message.getOff());
            }
        } else {
            sender.reply("Chat sync help\n!!chatSync on/off\n!!chatsync on/off");
        }
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
        logger.info("{} <{}> {}", source, sender, message);
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
