package cn.fsp.chatbridgevelocity.chat;

import cn.fsp.chatbridgevelocity.ChatBridgeVelocity;
import cn.fsp.chatbridgevelocity.chat.qq.QQChat;
import cn.fsp.chatbridgevelocity.chat.util.GoCQHttpHandler;
import cn.fsp.chatbridgevelocity.chat.util.MiraiHandler;
import cn.fsp.chatbridgevelocity.config.Config;
import com.velocitypowered.api.event.connection.ConnectionHandshakeEvent;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.slf4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class ChatForward {
    public ProxyServer server;
    public Logger logger;
    Map<String, String> playerCurrentServer = new HashMap<>();
    private Map<String, Long> Timestamp = new HashMap<>();
    public Config config;
    public QQChat qqChat;
    private long CD;
    private boolean ChatForwardEnabled;
    private URI uri1;

    public ChatForward(ChatBridgeVelocity plugin) {
        this.server = plugin.server;
        this.logger = plugin.logger;
        this.config = plugin.config;
        this.CD = config.getCD() * 1000;
        this.ChatForwardEnabled = config.ChatForwardEnabled();
        // 功能未启用
        if (!config.getQQChatEnabled()) {
            logger.info("QQ聊天互通已禁用");
            return;
        }
        if (config.getGoCQHttp()) {
            goCQHttp();
        }else {
            Mirai();
        }
    }

    private void goCQHttp() {
        try {
            uri1 = new URI("ws://" + config.getHost() + ":" + config.getPort() + "/");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        qqChat = new QQChat(uri1, this, new GoCQHttpHandler(this));
        qqChat.addHeader("Authorization", "Bearer " + config.getToken());
        qqChat.connect();
    }

    private void Mirai() {
        try {
            uri1 = new URI("ws://" + config.getHost() + ":" + config.getPort() + "/all?verifyKey=" + config.getToken() + "&qq=" + config.getBotQQ());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        qqChat = new QQChat(uri1, this, new MiraiHandler(this));
        qqChat.connect();
    }

//    @Subscribe
//    public void onPing(ProxyPingEvent event) {
//        logger.info(event.getPing().toString());
//        ServerPing response = event.getPing();
//        ServerPing.SamplePlayer[] playerInfo = server.getAllPlayers().stream().map(player -> new ServerPing
//                .SamplePlayer(player.getUsername(), player.getUniqueId())).toArray(ServerPing.SamplePlayer[]::new);
//        ServerPing newResponse = response.asBuilder().samplePlayers(playerInfo).build();
//        event.setPing(newResponse);
//    }

    @Subscribe
    public void onPlayerChatEvent(PlayerChatEvent event) {
        if (!this.ChatForwardEnabled) {
            return;
        }
        String currentServerName = event.getPlayer().getCurrentServer().orElseThrow().getServer().getServerInfo().getName();
        String playerName = event.getPlayer().getUsername();
        String message = event.getMessage();
        if (message.startsWith(config.getMCRespondPrefix())) {
            message = message.substring(4, message.length()).trim();
            MessageFormat str = new MessageFormat(config.getQQMessageFormat());
            qqChat.sendMessage(str.format(new String[]{currentServerName, playerName, message}), String.valueOf(event.hashCode()));
        }
        for (RegisteredServer server1 : server.getAllServers()) {
            if (!server1.getServerInfo().getName().equals(currentServerName)) {
                for (Player player : server1.getPlayersConnected()) {
                    player.sendMessage(Msg(currentServerName, playerName, message));
                }
            }
        }
        logger.info("[" + currentServerName + "]<" + playerName + "> " + message);
    }

    @Subscribe
    public void onServerConnectedEvent(ServerConnectedEvent event) {
        if (!this.ChatForwardEnabled) {
            return;
        }
        String previousServer;
        String currentServer = event.getServer().getServerInfo().getName();
        String playerName = event.getPlayer().getUsername();
        playerCurrentServer.put(playerName, currentServer);
        if (event.getPreviousServer().isPresent()) {
            previousServer = event.getPreviousServer().get().getServerInfo().getName();
            for (RegisteredServer server1 : server.getAllServers()) {
                if (!server1.getServerInfo().getName().equals(previousServer)) {
                    for (Player player : server1.getPlayersConnected()) {
                        player.sendMessage(left(previousServer, playerName));
                    }
                }
            }
            logger.info("[" + previousServer + "] " + playerName + " left " + previousServer);
        }
        for (RegisteredServer server1 : server.getAllServers()) {
            if (!server1.getServerInfo().getName().equals(currentServer)) {
                for (Player player : server1.getPlayersConnected()) {
                    player.sendMessage(join(currentServer, playerName));
                }
            }
        }
        logger.info("[" + currentServer + "] " + playerName + " joined " + currentServer);
    }

    @Subscribe
    public void onDisconnectEvent(DisconnectEvent event) {
        if (!this.ChatForwardEnabled) {
            return;
        }
        String playerName = event.getPlayer().getUsername();
        String previousServer = playerCurrentServer.get(playerName);
        for (RegisteredServer server1 : server.getAllServers()) {
            if (!server1.getServerInfo().getName().equals(previousServer)) {
                for (Player player : server1.getPlayersConnected()) {
                    player.sendMessage(left(previousServer, playerName));
                }
            }
        }
        playerCurrentServer.remove(playerName);
    }

    @Subscribe
    public void onLoginEvent(LoginEvent event) {
        // 功能未启用
        if (!config.getQQJoinMessageEnabled()) {
            logger.info("登录转发已禁用");
            return;
        }
        String playerName = event.getPlayer().getUsername();
        if (event.getResult().isAllowed()) {
            if (getTimestamp(playerName)) {
                MessageFormat str = new MessageFormat(config.getQQJoinFormat());
                qqChat.sendMessage(str.format(new String[]{playerName}), String.valueOf(event.hashCode()));
            }
        }
    }

    @Subscribe
    public void onConnectionHandshakeEvent(ConnectionHandshakeEvent event) {

    }

    public void allPlayerSendMessage(String name, String msg) {
        for (Player player : server.getAllPlayers()) {
            player.sendMessage(Msg("QQ", name, msg));
        }
        logger.info("[QQ]<" + name + "> " + msg);
    }

    public void sendMessageALL(String msg) {
        for (Player player : server.getAllPlayers()) {
            player.sendMessage(Component.text("[Velocity] " + msg).color(NamedTextColor.GRAY));
        }
        qqChat.sendMessage("[Velocity] " + msg, "Velocity");
        logger.info("[Velocity] " + msg);
    }

    public StringBuilder getOnline() {
        StringBuilder sb = new StringBuilder();
        RegisteredServer ser;
        if (server.getAllPlayers().size() == 0) {
            sb.append("当前没有玩家在线");
            return sb;
        }
        for (String sl : config.getServerList()) {
            ser = server.getServer(sl).get();
            if (ser.getPlayersConnected().size() == 0) {
                continue;
            }
            sb.append(server.getServer(sl).get().getServerInfo().getName()).append(" online:\n");
            for (Player player : ser.getPlayersConnected()) {
                sb.append("- ").append(player.getUsername()).append("\n");
            }
        }
        return sb;
    }

    private Component Msg(String serverName, String playerName, String message) {
        MessageFormat str = new MessageFormat(config.getMessageFormat());
        Component msg = Component.text(str.format(new String[]{serverName, playerName, message}));
        return msg;
    }

    private Component join(String serverName, String playerName) {
        MessageFormat str = new MessageFormat(config.getJoinFormat());
        Component msg = Component.text(str.format(new String[]{serverName, playerName})).color(NamedTextColor.GRAY);
        return msg;
    }

    private Component left(String serverName, String playerName) {
        MessageFormat str = new MessageFormat(config.getLeftFormat());
        Component msg = Component.text(str.format(new String[]{serverName, playerName})).color(NamedTextColor.GRAY);
        return msg;
    }

    /**
     * 判断记录中是否有时间戳
     * 没有会新建记录
     * 判断是否超出设置的冷却时间
     * 超出会重置时间戳
     * @param name
     * @return Boolean
     */
    private boolean getTimestamp(String name) {
        if (!this.Timestamp.containsKey(name)) {
            setTimestamp(name);
            return true;
        }
        // 大于冷却时间
        if (System.currentTimeMillis() >= this.Timestamp.get(name)) {
            setTimestamp(name);
            return true;
        }
        return false;
    }

    private void setTimestamp(String name) {
        this.Timestamp.put(name, System.currentTimeMillis() + this.CD);
    }

    public void qqChatClose() {
        qqChat.close();
        if (qqChat.isOpen()) {
            logger.info("starting");
        }else {
            logger.info("closed");
        }
    }
}
