package cn.fsp.chatbridgevelocity.chat;

import cn.fsp.chatbridgevelocity.ChatBridgeVelocity;
import cn.fsp.chatbridgevelocity.chat.kook.util.ChannelMsgBody;
import cn.fsp.chatbridgevelocity.chat.kook.util.URIUtil;
import cn.fsp.chatbridgevelocity.chat.qq.QQChat;
import cn.fsp.chatbridgevelocity.chat.qq.handler.GoCQHttpHandler;
import cn.fsp.chatbridgevelocity.chat.qq.handler.MiraiHandler;
import cn.fsp.chatbridgevelocity.config.Config;
import cn.fsp.chatbridgevelocity.event.KookMessageEvent;
import cn.fsp.chatbridgevelocity.event.SocketEvent;
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
    private boolean qqChatEnabled;
    private boolean kookChatEnabled;
    public ChatBridgeVelocity plugin;

    public ChatForward(ChatBridgeVelocity plugin) {
        this.plugin = plugin;
        this.server = plugin.server;
        this.logger = plugin.logger;
        this.config = plugin.config;
        this.CD = config.getCD() * 1000;
        this.ChatForwardEnabled = config.ChatForwardEnabled();
        this.qqChatEnabled = config.getQQChatEnabled();
        this.kookChatEnabled = config.getKookEnabled();
        // qq 互通
        if (qqChatEnabled) {
            connect();
        }else {
            logger.info("QQ聊天互通已禁用");
        }
    }

    private void connect() {
        if (config.getGoCQHttp()) {
            goCQHttp();
        }else {
            Mirai();
        }
    }

    private void goCQHttp() {
//        try {
//            uri1 = new URI("ws://" + config.getHost() + ":" + config.getPort() + "/");
//        } catch (URISyntaxException e) {
//            throw new RuntimeException(e);
//        }
        qqChat = new QQChat(URIUtil.createURI("ws://" + config.getHost() + ":" + config.getPort() + "/"), this, new GoCQHttpHandler(this));
        qqChat.addHeader("Authorization", "Bearer " + config.getToken());
        qqChat.connect();
    }

    private void Mirai() {
//        try {
//            uri1 = new URI("ws://" + config.getHost() + ":" + config.getPort() + "/all?verifyKey=" + config.getToken() + "&qq=" + config.getBotQQ());
//        } catch (URISyntaxException e) {
//            throw new RuntimeException(e);
//        }
        qqChat = new QQChat(URIUtil.createURI("ws://" + config.getHost() + ":" + config.getPort() + "/all?verifyKey=" + config.getToken() + "&qq=" + config.getBotQQ()), this, new MiraiHandler(this));
        qqChat.connect();
    }

    @Subscribe
    public void onPlayerChatEvent(PlayerChatEvent event) {
        if (!this.ChatForwardEnabled) {
            return;
        }
        String currentServerName = event.getPlayer().getCurrentServer().orElseThrow().getServer().getServerInfo().getName();
        String playerName = event.getPlayer().getUsername();
        String message = event.getMessage();
        if (message.startsWith(config.getMCRespondPrefix()) || qqChat.getSync()) {
            if (!qqChat.getSync()) {
                message = message.substring(4).trim();
            }else {
                message += "\t[chatSync]";
            }
            MessageFormat str = new MessageFormat(config.getQQMessageFormat());
            if (qqChatEnabled) {
                qqChat.sendMessage(str.format(new String[]{currentServerName, playerName, message}), String.valueOf(event.hashCode()));
            }
        }
        for (RegisteredServer server1 : server.getAllServers()) {
            if (!server1.getServerInfo().getName().equals(currentServerName)) {
                for (Player player : server1.getPlayersConnected()) {
                    player.sendMessage(Msg(currentServerName, playerName, message));
                }
            }
        }
        if (kookChatEnabled) {
            ChatBridgeVelocity.channelMessage.sendMessage(
                    ChannelMsgBody.msgBody(
                            config.getKookChannelID(), "[" + currentServerName + "]<" + playerName + "> " + message));
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
            String s = new MessageFormat(config.getQQJoinFormat()).format(new String[]{playerName});
            if (kookChatEnabled) {
                sendKookMsg(s);
            }
            if (getTimestamp(playerName)) {
                if (qqChatEnabled) {
                    qqChat.sendMessage(s, String.valueOf(event.hashCode()));
                }
            }
        }
    }

    @Subscribe
    public void onKookMessageEvent(KookMessageEvent event) {
        // 功能禁用与停止对非指定服务器的相应
        if (!kookChatEnabled || !config.getKookServerID().equals(event.getServer())) {
            return;
        }
        // online 指令
        if (event.getMessage().startsWith("!!online")) {
            ChatBridgeVelocity.channelMessage.sendMessage(
                    ChannelMsgBody.msgBody(event.getChannel(), getOnline().toString()));
            return;
        }
        // 只转发指定频道的消息
        if (config.getKookChannelID().equals(event.getChannel())) {
            allPlayerSendMessage(event.getSender(), event.getMessage(), "[KOOK]");
        }
    }

    @Subscribe
    public void onSocketEvent(SocketEvent event) {
        server.getAllPlayers().forEach(
                player -> player.sendMessage((event.getStatus() == 49) ? serverStarted(event.getServerName()) : serverStopped(event.getServerName()))
        );
        logger.info((event.getStatus() == 49) ? (event.getServerName() + "Started!") : (event.getServerName() + "Stopped!"));
    }

    public void allPlayerSendMessage(String name, String msg) {
        server.getAllPlayers().forEach(
                player -> player.sendMessage(Msg("QQ", name, msg))
        );
        logger.info("[QQ]<" + name + "> " + msg);
    }

    public void allPlayerSendMessage(String name, String msg, String p) {
        server.getAllPlayers().forEach(
                player -> player.sendMessage(Msg(p, name, msg))
        );
        logger.info(p + "<" + name + "> " + msg);
    }

    public void sendMessageALL(String msg) {
        String s = "[Velocity] " + msg;
        for (Player player : server.getAllPlayers()) {
            player.sendMessage(Component.text(s).color(NamedTextColor.GRAY));
        }
        if (qqChatEnabled) {
            qqChat.sendMessage(s, "Velocity");
        }
        if (kookChatEnabled) {
            sendKookMsg(s);
        }
        logger.info("[Velocity] " + msg);
    }

    public StringBuilder getOnline() {
        StringBuilder sb = new StringBuilder();
        if (server.getAllPlayers().size() == 0) {
            sb.append("当前没有玩家在线");
            return sb;
        }
        for (String sl : config.getServerList()) {
            if (server.getServer(sl).isEmpty()) {
                continue;
            }
            RegisteredServer ser = server.getServer(sl).get();
            if (ser.getPlayersConnected().size() == 0) {
                continue;
            }

            sb.append(sl).append(" online:\n");
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

    private Component serverStarted(String serverName) {
        Component msg = Component.text(serverName + " started!").color(NamedTextColor.GRAY);
        return msg;
    }

    private Component serverStopped(String serverName) {
        Component msg = Component.text(serverName + " stopped!").color(NamedTextColor.GRAY);
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
    }

    public void reload() {
        qqChatClose();
        connect();
    }

    public void sendKookMsg(String s) {
        ChatBridgeVelocity.channelMessage.sendMessage(ChannelMsgBody.msgBody(config.getKookChannelID(), s));
    }

    public void setQQChatEnabled(boolean e) {
        this.qqChatEnabled = e;
    }

    public void setKookChatEnabled(boolean e) {
        this.kookChatEnabled = e;
    }
}
